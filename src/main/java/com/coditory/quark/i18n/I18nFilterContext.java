package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public record I18nFilterContext(
        Object value,
        List<Expression> filterArgs,
        List<Object> expressionArgs,
        I18nMessages messages,
        ExpressionResolver resolver
) {
    public I18nFilterContext(
            Object value,
            List<Expression> filterArgs,
            List<Object> expressionArgs,
            I18nMessages messages,
            ExpressionResolver resolver
    ) {
        this.value = value;
        this.filterArgs = List.copyOf(filterArgs);
        this.expressionArgs = List.copyOf(expressionArgs);
        this.messages = requireNonNull(messages);
        this.resolver = requireNonNull(resolver);
    }

    @NotNull
    public List<Object> resolveFilterArgs() {
        return filterArgs.stream()
                .map(this::resolve)
                .toList();
    }

    @NotNull
    public Object resolve(Expression expression) {
        return resolver.resolve(expression, expressionArgs);
    }

    @NotNull
    public String resolveToString(Expression expression) {
        Object value = resolve(expression);
        return Objects.toString(value);
    }

    public int resolveToInt(Expression expression) {
        try {
            String value = resolveToString(expression);
            return Integer.parseInt(value);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Expected int. Got: " + expression);
        }
    }

    @Nullable
    public Object resolveFirstFilterArgOrNull() {
        return filterArgs.isEmpty()
                ? null
                : resolve(filterArgs.get(0));
    }

    @Nullable
    public String valueAsString() {
        return Objects.toString(value);
    }

    public int valueAsInt() {
        try {
            String value = valueAsString();
            return Integer.parseInt(value);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Expected int value. Got: " + value);
        }
    }
}
