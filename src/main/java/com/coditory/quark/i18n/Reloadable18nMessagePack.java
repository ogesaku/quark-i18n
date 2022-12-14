package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class Reloadable18nMessagePack implements I18nMessagePack {
    private final Function<Map<I18nKey, String>, I18nMessagePack> i18nMessagePackCreator;
    private final AggregatedI18nLoader loader;
    private volatile I18nMessagePack i18nMessagePack;

    Reloadable18nMessagePack(AggregatedI18nLoader loader, Function<Map<I18nKey, String>, I18nMessagePack> i18nMessagePackCreator) {
        expectNonNull(i18nMessagePackCreator, "i18nMessagePackCreator");
        expectNonNull(loader, "loader");
        this.i18nMessagePackCreator = i18nMessagePackCreator;
        this.loader = loader;
        reload();
    }

    public void reload() {
        reload(loader.load());
    }

    private void reload(Map<I18nKey, String> entries) {
        this.i18nMessagePack = i18nMessagePackCreator.apply(entries);
    }

    public synchronized void startWatching() {
        loader.startWatching(this::reload);
    }

    public synchronized void stopWatching() {
        loader.stopWatching();
    }

    @Override
    public @NotNull I18nMessages localize(@NotNull Locale locale) {
        return i18nMessagePack.localize(locale);
    }

    @Override
    public @NotNull I18nMessagePack addPrefix(@NotNull String prefix) {
        return i18nMessagePack.addPrefix(prefix);
    }

    @Override
    public @NotNull String getMessage(@NotNull Locale locale, @NotNull String key, Object... args) {
        return i18nMessagePack.getMessage(locale, key, args);
    }

    @Override
    public @NotNull String format(@NotNull Locale locale, @NotNull String template, Object... args) {
        return i18nMessagePack.format(locale, template, args);
    }
}
