package com.coditory.quark.i18n.loader;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface I18nParser {
    @NotNull
    Map<String, Object> parse(@NotNull String content);
}
