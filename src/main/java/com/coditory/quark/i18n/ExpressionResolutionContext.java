package com.coditory.quark.i18n;

import java.util.List;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

public final class ExpressionResolutionContext {
    private final List<Object> args;
    private final I18nMessages messages;

    ExpressionResolutionContext(List<Object> args, I18nMessages messages) {
        this.args = List.copyOf(args);
        this.messages = requireNonNull(messages);
    }

    List<Object> getArgs() {
        return args;
    }

    I18nMessages getMessages() {
        return messages;
    }

    Locale getLocale() {
        return messages.getLocale();
    }
}
