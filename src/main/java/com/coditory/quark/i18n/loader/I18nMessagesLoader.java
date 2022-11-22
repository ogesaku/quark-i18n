package com.coditory.quark.i18n.loader;

import com.coditory.quark.i18n.I18nKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@FunctionalInterface
public interface I18nMessagesLoader {
    @NotNull
    Map<I18nKey, String> load();
}
