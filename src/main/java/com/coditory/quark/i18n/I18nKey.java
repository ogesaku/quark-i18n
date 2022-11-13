package com.coditory.quark.i18n;


import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import static com.coditory.quark.i18n.Preconditions.expectNonBlank;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public record I18nKey(Locale locale, I18nPath path) {
    public I18nKey(@NotNull Locale locale, @NotNull I18nPath path) {
        this.locale = expectNonNull(locale, "locale");
        this.path = expectNonNull(path, "path");
    }

    public static I18nKey of(@NotNull Locale locale, @NotNull String path) {
        expectNonNull(locale, "locale");
        expectNonBlank(path, "path");
        return new I18nKey(locale, I18nPath.of(path));
    }

    public static I18nKey of(@NotNull Locale locale, String... path) {
        expectNonNull(locale, "locale");
        expectNonNull(path, "path");
        return new I18nKey(locale, I18nPath.of(path));
    }

    public static I18nKey of(@NotNull Locale locale, @NotNull I18nPath path) {
        expectNonNull(locale, "locale");
        expectNonNull(path, "path");
        return new I18nKey(locale, path);
    }

    public I18nKey withLocale(@NotNull Locale locale) {
        expectNonNull(locale, "locale");
        return Objects.equals(this.locale, locale)
                ? this
                : new I18nKey(locale, path);
    }

    public I18nKey withPath(@NotNull I18nPath path) {
        expectNonNull(path, "path");
        return Objects.equals(this.path, path)
                ? this
                : new I18nKey(locale, path);
    }

    public String getPathValue() {
        return path.getPathValue();
    }

    public String toShortString() {
        return "" + locale + ':' + path;
    }

    @Override
    public String toString() {
        return "I18nKey{" + toShortString() + '}';
    }
}
