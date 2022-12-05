package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.String.format;

@FunctionalInterface
public interface I18nUnresolvedMessageHandler {
    @NotNull
    static I18nUnresolvedMessageHandler throwError() {
        return (key, args) -> {
            String argsString = args == null || args.length == 0 ? "" : Arrays.toString(args);
            String argsStringInParenthesis = argsString.isEmpty() ? "" : "(" + argsString.substring(1, argsString.length() - 1) + ')';
            throw new I18nMessagesException(format("Missing message %s%s", key.toShortString(), argsStringInParenthesis));
        };
    }

    @NotNull
    static I18nUnresolvedMessageHandler generateErrorMessage() {
        return (key, args) -> key.pathValue();
    }

    @NotNull
    String onUnresolvedMessage(@NotNull I18nKey key, @NotNull Object... args);
}
