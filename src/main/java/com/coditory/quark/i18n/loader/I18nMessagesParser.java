package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nPath;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface I18nMessagesParser {
    @NotNull
    Map<I18nPath, String> parse(@NotNull String content);
}
