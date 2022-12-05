package com.coditory.quark.i18n;

import com.coditory.quark.i18n.loader.I18nMessagesLoader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class AggregatedI18nMessagesLoader implements I18nMessagesLoader {
    private final List<I18nMessagesLoader> loaders = new ArrayList<>();
    private final Map<I18nKey, String> currentEntries = new LinkedHashMap<>();

    public void addLoader(I18nMessagesLoader loader) {
        requireNonNull(loader);
        appendCurrentEntries();
        loaders.add(loader);
    }

    public void addMessage(I18nKey key, String value) {
        requireNonNull(key);
        requireNonNull(value);
        currentEntries.put(key, value);
    }

    public void addMessages(Map<I18nKey, String> messages) {
        requireNonNull(messages);
        currentEntries.putAll(messages);
    }

    public AggregatedI18nMessagesLoader copy() {
        appendCurrentEntries();
        AggregatedI18nMessagesLoader copy = new AggregatedI18nMessagesLoader();
        copy.loaders.addAll(loaders);
        return copy;
    }

    @Override
    public @NotNull Map<I18nKey, String> load() {
        appendCurrentEntries();
        return loaders.stream()
                .map(I18nMessagesLoader::load)
                .reduce(new LinkedHashMap<>(), (m, e) -> {
                    m.putAll(e);
                    return m;
                });
    }

    private void appendCurrentEntries() {
        if (!currentEntries.isEmpty()) {
            Map<I18nKey, String> copy = new LinkedHashMap<>(currentEntries);
            loaders.add(() -> copy);
            currentEntries.clear();
        }
    }
}
