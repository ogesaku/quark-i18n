package com.coditory.quark.i18n;

import java.util.Objects;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class MessageTemplate {
    static MessageTemplate parse(ExpressionParser parser, String value) {
        Expression expression = parser.parse(value);
        return new MessageTemplate(value, expression);
    }

    private final String value;
    private final Expression expression;

    MessageTemplate(String value, Expression expression) {
        expectNonNull(value, "value");
        expectNonNull(expression, "expression");
        this.value = value;
        this.expression = expression;
    }

    public String resolve(ExpressionContext context) {
        expectNonNull(context, "args");
        Object value = expression.resolve(context);
        return Objects.toString(value);
    }

    @Override
    public String toString() {
        return "MessageTemplate{" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageTemplate that = (MessageTemplate) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}