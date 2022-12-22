package com.coditory.quark.i18n.formatter;

import org.jetbrains.annotations.NotNull;

public interface I18nFormatterProvider {
    @NotNull
    I18nFormatter formatter(@NotNull FormatterContext context);
}

