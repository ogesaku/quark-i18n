package com.coditory.quark.i18n.api;

import java.util.Arrays;

import static java.lang.String.format;

@FunctionalInterface
public interface I18nUnresolvedMessageHandler {
    static I18nUnresolvedMessageHandler throwError() {
        return (key, args) -> {
            throw new I18nMessagesException(format("Could not resolve i18n message (locale: %s, key: %s, args: %s)",
                    key.locale(), key.path(), Arrays.toString(args)));
        };
    }

    static I18nUnresolvedMessageHandler generateErrorMessage() {
        return (key, args) -> key.getPathValue();
    }

    String onUnresolvedMessage(I18nKey key, Object... args);
}
