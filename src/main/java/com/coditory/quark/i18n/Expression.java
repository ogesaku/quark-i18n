package com.coditory.quark.i18n;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.coditory.quark.i18n.Preconditions.expect;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.joining;

public interface Expression {
    Object resolve(ExpressionContext context);

    @Nullable
    default Object getStaticValue() {
        return null;
    }
}

final class StaticExpression implements Expression {
    private static final StaticExpression EMPTY = new StaticExpression("");
    private final String value;

    static StaticExpression empty() {
        return EMPTY;
    }

    static StaticExpression parse(StringBuilder value) {
        return value.isEmpty() ? EMPTY : new StaticExpression(value.toString());
    }

    private StaticExpression(String value) {
        this.value = expectNonNull(value, "value");
    }

    public String getValue() {
        return value;
    }

    @Override
    public String resolve(ExpressionContext context) {
        expectNonNull(context, "context");
        return value;
    }

    @Override
    @Nullable
    public Object getStaticValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StaticExpression{\"" + value + "\"}";
    }
}

final class CompositeExpression implements Expression {
    private final List<Expression> expressions;

    static Expression of(List<Expression> expressions) {
        expectNonNull(expressions, "expressions");
        expressions = expressions.stream()
                .filter(expression -> !(StaticExpression.empty().equals(expression)))
                .toList();
        if (expressions.isEmpty()) {
            return StaticExpression.empty();
        }
        if (expressions.size() == 1) {
            return expressions.get(0);
        }
        return new CompositeExpression(expressions);
    }

    private CompositeExpression(List<Expression> expressions) {
        this.expressions = List.copyOf(expressions);
    }

    @Override
    public String resolve(ExpressionContext context) {
        expectNonNull(context, "context");
        return expressions.stream()
                .map(expression -> expression.resolve(context))
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .collect(joining());
    }

    @Override
    public String toString() {
        return "CompositeExpression{" + expressions + '}';
    }
}

final class ArgExpression implements Expression {
    private final int index;
    private final List<ExpressionFilter> filters;

    public static ArgExpression parse(String value, List<ExpressionFilter> filters) {
        expectNonNull(value, "value");
        expectNonNull(filters, "filters");
        int argIndex = Integer.parseInt(value);
        expect(argIndex >= 0, "Expected argument index >= 0. Got: " + argIndex);
        return new ArgExpression(argIndex, filters);
    }

    private ArgExpression(int index, List<ExpressionFilter> filters) {
        this.index = index;
        this.filters = List.copyOf(filters);
    }

    @Override
    public Object resolve(ExpressionContext context) {
        expectNonNull(context, "context");
        return context.args().get(index);
    }

    @Override
    public String toString() {
        return "ArgExpression{" +
                "index=" + index +
                ", filters=" + filters +
                '}';
    }
}

final class ReferenceExpression implements Expression {
    private final I18nPath reference;
    private final List<ExpressionFilter> filters;

    static ReferenceExpression parse(String value, List<ExpressionFilter> filters) {
        expectNonNull(value, "value");
        expectNonNull(filters, "filters");
        I18nPath path = I18nPath.of(value);
        return new ReferenceExpression(path, filters);
    }

    private ReferenceExpression(I18nPath reference, List<ExpressionFilter> filters) {
        this.reference = reference;
        this.filters = List.copyOf(filters);
    }

    @Override
    public String resolve(ExpressionContext context) {
        expectNonNull(context, "context");
        I18nMessages messages = context.messages();
        Object[] args = context.args().toArray();
        return messages.getMessage(reference, args);
    }

    @Override
    public String toString() {
        return "ReferenceExpression{" +
                "reference=" + reference +
                ", filters=" + filters +
                '}';
    }
}