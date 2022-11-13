package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface I18nFormatterProvider {
    @NotNull
    I18nFormatter formatter(@NotNull I18nMessageTemplates messages, @NotNull List<String> args);

    @NotNull
    default I18nFormatter formatter(@NotNull I18nMessageTemplates messages) {
        return formatter(messages, List.of());
    }
}

