package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Supplier;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class Reloadable18nMessagePack implements I18nMessagePack {
    private final Supplier<I18nMessagePack> i18nMessagePackSupplier;
    private volatile I18nMessagePack i18nMessagePack;

    Reloadable18nMessagePack(Supplier<I18nMessagePack> i18nMessagePackSupplier) {
        expectNonNull(i18nMessagePackSupplier, "i18nMessagePackSupplier");
        this.i18nMessagePackSupplier = i18nMessagePackSupplier;
        this.i18nMessagePack = i18nMessagePackSupplier.get();
    }

    public void reload() {
        this.i18nMessagePack = i18nMessagePackSupplier.get();
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
