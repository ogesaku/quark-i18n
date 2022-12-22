package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.I18nFormatter;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class ExpressionContext {
    private final FormatterResolver formatterResolver;
    private final MessageTemplateParser parser;

    ExpressionContext(
            FormatterResolver formatterResolver,
            MessageTemplateParser parser
    ) {
        this.parser = expectNonNull(parser, "parser");
        this.formatterResolver = expectNonNull(formatterResolver, "formatterResolver");
    }

    MessageTemplate parse(String expression) {
        return parser.parse(expression);
    }

    I18nFormatter getTypedFormatter() {
        return formatterResolver.getTypedFormatter();
    }

    I18nFormatter getFormatterByName(String name) {
        return getFormatterByName(name, null);
    }

    I18nFormatter getFormatterByName(String name, String style) {
        return formatterResolver.getFormatterByName(name, style);
    }
}
