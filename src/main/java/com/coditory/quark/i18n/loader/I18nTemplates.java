package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public record I18nTemplates(Map<I18nKey, String> templates, I18nPath prefix) {
    public I18nTemplates(@NotNull Map<I18nKey, String> templates) {
        this(templates, I18nPath.root());
    }

    public I18nTemplates(@NotNull Map<I18nKey, String> templates, @Nullable I18nPath prefix) {
        this.templates = Map.copyOf(templates);
        this.prefix = requireNonNull(prefix);
    }
}
