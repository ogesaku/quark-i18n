package com.coditory.quark.i18n;

import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ExpressionResolver {
    private final I18nMessages messages;

    ExpressionResolver(I18nMessages messages) {
        this.messages = expectNonNull(messages, "messages");
    }

    Object resolve(Expression expression, List<Object> args) {
        ExpressionContext context = new ExpressionContext(args, messages);
        try {
            return expression.resolve(context);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not resolve expression: " + expression, e);
        }
    }
}
