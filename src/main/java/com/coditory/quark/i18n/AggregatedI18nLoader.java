package com.coditory.quark.i18n;

import com.coditory.quark.i18n.loader.I18nLoader;
import com.coditory.quark.i18n.loader.WatchableI18nLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

final class AggregatedI18nLoader implements WatchableI18nLoader {
    private final List<I18nLoader> loaders = new ArrayList<>();
    private final Map<I18nKey, String> currentEntries = new LinkedHashMap<>();
    private final ConcurrentHashMap<I18nLoader, Map<I18nKey, String>> cachedResults = new ConcurrentHashMap<>();
    private final Set<I18nLoaderChangeListener> listeners = new LinkedHashSet<>();
    private boolean watching = false;

    public synchronized void addLoader(I18nLoader loader) {
        requireNonNull(loader);
        appendCurrentEntries();
        loaders.add(loader);
    }

    public synchronized void addMessage(I18nKey key, String value) {
        requireNonNull(key);
        requireNonNull(value);
        currentEntries.put(key, value);
    }

    public synchronized void addMessages(Map<I18nKey, String> messages) {
        requireNonNull(messages);
        currentEntries.putAll(messages);
    }

    public synchronized AggregatedI18nLoader copy() {
        appendCurrentEntries();
        AggregatedI18nLoader copy = new AggregatedI18nLoader();
        copy.loaders.addAll(loaders);
        return copy;
    }

    @Override
    public synchronized void addChangeListener(I18nLoaderChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void startWatching() {
        if (watching) {
            throw new IllegalStateException("Loader is already watching for changes");
        }
        watching = true;
        for (I18nLoader loader : loaders) {
            if (loader instanceof WatchableI18nLoader watchableLoader) {
                watchableLoader.startWatching();
                watchableLoader.addChangeListener(entries -> onEntriesChange(loader, entries));
            }
        }
    }

    private synchronized void onEntriesChange(I18nLoader loader, Map<I18nKey, String> entries) {
        cachedResults.put(loader, entries);
        Map<I18nKey, String> result = loaders.stream()
                .map(l -> cachedResults.getOrDefault(l, Map.of()))
                .reduce(new LinkedHashMap<>(), (m, e) -> {
                    m.putAll(e);
                    return m;
                });
        for (I18nLoaderChangeListener listener : listeners) {
            listener.onChange(result);
        }
    }

    @Override
    public synchronized void stopWatching() {
        if (!watching) {
            return;
        }
        for (I18nLoader loader : loaders) {
            if (loader instanceof WatchableI18nLoader watchableLoader) {
                watchableLoader.stopWatching();
            }
        }
    }

    @Override
    @NotNull
    public synchronized Map<I18nKey, String> load() {
        appendCurrentEntries();
        return loaders.stream()
                .map(this::load)
                .reduce(new LinkedHashMap<>(), (m, e) -> {
                    m.putAll(e);
                    return m;
                });
    }

    private Map<I18nKey, String> load(I18nLoader loader) {
        Map<I18nKey, String> entries = loader.load();
        cachedResults.put(loader, entries);
        return entries;
    }

    private void appendCurrentEntries() {
        if (!currentEntries.isEmpty()) {
            Map<I18nKey, String> copy = new LinkedHashMap<>(currentEntries);
            I18nLoader loader = () -> copy;
            loaders.add(loader);
            cachedResults.put(loader, copy);
            currentEntries.clear();
        }
    }
}
