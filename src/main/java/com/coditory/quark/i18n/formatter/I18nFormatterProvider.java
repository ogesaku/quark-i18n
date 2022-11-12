package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessageTemplates;

import java.util.List;

public interface I18nFormatterProvider {
    I18nFormatter formatter(I18nMessageTemplates messages, List<String> args);

    default I18nFormatter formatter(I18nMessageTemplates messages) {
        return formatter(messages, List.of());
    }
}

