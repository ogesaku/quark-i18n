package com.coditory.quark.i18n;

import java.util.List;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

record ExpressionContext(List<Object> args, I18nMessages messages) {
    ExpressionContext(List<Object> args, I18nMessages messages) {
        this.args = List.copyOf(args);
        this.messages = requireNonNull(messages);
    }

    public Locale getLocale() {
        return messages.getLocale();
    }
}
