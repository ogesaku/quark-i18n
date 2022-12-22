package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.I18nFormatter;

import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.joining;

public interface Expression {
    Object resolve(ExpressionResolutionContext context);
}

final class StaticExpression implements Expression {
    private final String value;

    static StaticExpression parse(String value) {
        return new StaticExpression(value);
    }

    private StaticExpression(String value) {
        this.value = expectNonNull(value, "value");
    }

    @Override
    public String resolve(ExpressionResolutionContext context) {
        expectNonNull(context, "context");
        return value;
    }
}

final class CompositeExpression implements Expression {
    private final List<Expression> expressions;

    static CompositeExpression of(List<Expression> expressions) {
        expectNonNull(expressions, "expressions");
        return new CompositeExpression(expressions);
    }

    private CompositeExpression(List<Expression> expressions) {
        this.expressions = List.copyOf(expressions);
    }

    @Override
    public String resolve(ExpressionResolutionContext context) {
        expectNonNull(context, "context");
        return expressions.stream()
                .map(expression -> expression.resolve(context).toString())
                .collect(joining());
    }
}

final class FormatterExpression implements Expression {
    private final Expression expression;
    private final I18nFormatter formatter;

    static FormatterExpression parse(String expression, ExpressionContext context) {
        expectNonNull(expression, "expression");
        expectNonNull(context, "context");
        List<String> chunks = ExpressionSpliterator.splitBy(expression, '|');
        if (chunks.isEmpty()) {
            throw new RuntimeException("Empty expression");
        }
        if (chunks.size() > 2) {
            throw new RuntimeException("Expected single '|'. Got: " + chunks.size());
        }
        String rawValue = chunks.get(0);
        Expression valueExpression = context.parse(rawValue);
        I18nFormatter formatter = chunks.size() == 0
                ? context.getTypedFormatter()
                : context.getFormatterByName(rawValue, chunks.get(1));
        return new FormatterExpression(valueExpression, formatter);
    }

    private FormatterExpression(Expression expression, I18nFormatter formatter) {
        this.expression = expectNonNull(expression, "expression");
        this.formatter = expectNonNull(formatter, "formatter");
    }

    @Override
    public Object resolve(ExpressionResolutionContext context) {
        expectNonNull(context, "context");
        Object arg = expression.resolve(context);
        return formatter != null
                ? formatter.format(arg)
                : arg;
    }
}

final class ReferenceExpression implements Expression {
    private final I18nPath path;

    static ReferenceExpression parse(String value) {
        I18nPath path = I18nPath.of(value);
        return new ReferenceExpression(path);
    }

    private ReferenceExpression(I18nPath path) {
        this.path = expectNonNull(path, "path");
    }

    @Override
    public String resolve(ExpressionResolutionContext context) {
        expectNonNull(context, "context");
        I18nMessages messages = context.getMessages();
        Object[] args = context.getArgs().toArray();
        return messages.getMessage(path, args);
    }
}