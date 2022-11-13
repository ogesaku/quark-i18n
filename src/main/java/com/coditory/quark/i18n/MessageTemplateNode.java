package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.I18nFormatter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.coditory.quark.i18n.Preconditions.expect;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;

interface MessageTemplateNode {
    static MessageTemplateNode staticNode(String value) {
        return new MessageTemplateStaticNode(value);
    }

    static MessageTemplateNode expressionNode(String expression, int argumentIndex, List<I18nFormatter> formatters, Map<Class<?>, I18nFormatter> typedFormatters) {
        return new MessageTemplateExpressionNode(expression, argumentIndex, formatters, typedFormatters);
    }

    String resolve(Object[] args);
}

final class MessageTemplateStaticNode implements MessageTemplateNode {
    private final String value;

    public MessageTemplateStaticNode(String value) {
        expectNonNull(value, "value");
        this.value = value;
    }

    @Override
    public String resolve(Object[] args) {
        return value;
    }
}

final class MessageTemplateExpressionNode implements MessageTemplateNode {
    private final int argumentIndex;
    private final List<I18nFormatter> formatters;
    private final String expression;
    private final Map<Class<?>, I18nFormatter> typedFormatters;

    public MessageTemplateExpressionNode(
            String expression,
            int argumentIndex,
            List<I18nFormatter> formatters,
            Map<Class<?>, I18nFormatter> typedFormatters
    ) {
        expect(argumentIndex >= 0, "Expected argumentIndex >= 0");
        expectNonNull(formatters, "formatters");
        expectNonNull(expression, "expression");
        expectNonNull(typedFormatters, "typedFormatters");
        this.argumentIndex = argumentIndex;
        this.formatters = List.copyOf(formatters);
        this.expression = expression;
        this.typedFormatters = Map.copyOf(typedFormatters);
    }

    @Override
    public String resolve(Object[] args) {
        expectNonNull(args, "args");
        if (argumentIndex >= args.length) {
            String errorMessage = String.format("Missing argument \"%d\" for expression: \"%s\"", argumentIndex, expression);
            throw new MessageTemplateException(errorMessage);
        }
        Object arg = args[argumentIndex];
        for (I18nFormatter formatter : formatters) {
            arg = formatter.format(arg);
        }
        I18nFormatter typeFormatter = typedFormatters.get(arg.getClass());
        return typeFormatter != null
                ? typeFormatter.format(arg)
                : Objects.toString(arg);
    }
}