package com.coditory.quark.i18n;

import java.util.HashMap;
import java.util.Map;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class MessageTemplateParser {
    private final ExpressionParser expressionParser;

    MessageTemplateParser(FilterResolver formatter) {
        expectNonNull(formatter, "formatterResolver");
        this.expressionParser = new ExpressionParser(formatter);
    }

    Map<I18nKey, MessageTemplate> parseTemplates(I18nMessageTemplatesPack messages) {
        Map<I18nKey, MessageTemplate> result = new HashMap<>();
        Map<String, MessageTemplate> templates = new HashMap<>();
        for (Map.Entry<I18nKey, String> entry : messages.entries()) {
            I18nKey key = entry.getKey();
            String value = entry.getValue();
            MessageTemplate template = templates.computeIfAbsent(value, this::parse);
            result.put(key, template);
        }
        return result;
    }

    public MessageTemplate parse(String expression) {
        Expression parsed = expressionParser.parse(expression);
        return new MessageTemplate(expression, parsed);
    }
}
