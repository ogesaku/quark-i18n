package com.coditory.quark.i18n;

import com.coditory.quark.i18n.formatter.I18nFormatter;
import com.coditory.quark.i18n.formatter.I18nFormatterProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.coditory.quark.i18n.MessageTemplateNode.expressionNode;
import static com.coditory.quark.i18n.MessageTemplateNode.staticNode;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static com.coditory.quark.i18n.QuotedSpliterator.splitBy;
import static java.util.stream.Collectors.toMap;

final class MessageTemplateParser implements MessageTemplateFormatter {
    private final I18nMessageTemplatesPack messages;
    private final Map<String, I18nFormatterProvider> namedFormatters;
    private final Map<Class<?>, I18nFormatterProvider> typedFormatters;
    private final Map<Locale, Map<Class<?>, I18nFormatter>> typedFormattersCache = new HashMap<>();

    MessageTemplateParser(
            I18nMessageTemplatesPack messages,
            Map<String, I18nFormatterProvider> namedFormatters,
            Map<Class<?>, I18nFormatterProvider> typedFormatters
    ) {
        expectNonNull(messages, "messages");
        expectNonNull(namedFormatters, "namedFormatters");
        expectNonNull(typedFormatters, "typedFormatters");
        this.messages = messages;
        this.namedFormatters = Map.copyOf(namedFormatters);
        this.typedFormatters = Map.copyOf(typedFormatters);
    }

    MessageTemplate parse(Locale locale, String template) {
        expectNonNull(locale, "locale");
        expectNonNull(template, "template");
        I18nMessageTemplates localizedMessages = messages.withLocale(locale);
        List<MessageTemplateNode> nodes = new ArrayList<>();
        boolean expression = false;
        Set<Character> escapable = Set.of('{', '}');
        boolean escaped = false;
        StringBuilder chunk = new StringBuilder();
        for (char c : template.toCharArray()) {
            if (escaped) {
                if (!escapable.contains(c)) {
                    throw new RuntimeException("Could not escape: \"" + c + "\" in template: \"" + template + "\"");
                }
                escaped = false;
                chunk.append(c);
            } else if ('\\' == c) {
                escaped = true;
            } else if ('{' == c) {
                if (chunk.length() > 0) {
                    nodes.add(staticNode(chunk.toString()));
                    chunk = new StringBuilder();
                }
                expression = true;
            } else if ('}' == c) {
                if (expression) {
                    if (chunk.length() > 0) {
                        nodes.add(parseExpression(localizedMessages, chunk.toString().trim()));
                        chunk = new StringBuilder();
                    }
                    expression = false;
                } else {
                    throw new RuntimeException("Unopened expression in template: \"" + template + "\"");
                }
            } else {
                chunk.append(c);
            }
        }
        if (chunk.length() > 0) {
            nodes.add(staticNode(chunk.toString()));
        }
        return new MessageTemplate(template, nodes);
    }

    private MessageTemplateNode parseExpression(I18nMessageTemplates messages, String expression) {
        List<String> chunks = splitBy(expression, '|');
        if (chunks.isEmpty()) {
            throw new RuntimeException("Empty expression");
        }
        int argumentIndex;
        try {
            argumentIndex = Integer.parseInt(chunks.get(0));
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not extract argument index from expression: \"" + expression + "\"");
        }
        List<I18nFormatter> formatters = new ArrayList<>();
        for (int i = 1; i < chunks.size(); ++i) {
            formatters.add(parseFormatter(messages, chunks.get(i)));
        }
        Map<Class<?>, I18nFormatter> typeFormatters = resolveTypeFormatters(messages);
        return expressionNode(expression, argumentIndex, formatters, typeFormatters);
    }

    private Map<Class<?>, I18nFormatter> resolveTypeFormatters(I18nMessageTemplates messages) {
        return typedFormattersCache.computeIfAbsent(messages.getLocale(), (__) ->
                this.typedFormatters.entrySet()
                        .stream()
                        .map(e -> Map.entry(e.getKey(), e.getValue().formatter(messages)))
                        .collect(toMap(Entry::getKey, Entry::getValue))
        );
    }

    private I18nFormatter parseFormatter(I18nMessageTemplates messages, String formatter) {
        List<String> chunks = splitBy(formatter, ' ');
        if (chunks.isEmpty()) {
            throw new RuntimeException("Empty formatter");
        }
        String formatterName;
        try {
            formatterName = chunks.get(0);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not extract formatter name from expression: \"" + formatter + "\"");
        }
        List<String> arguments = chunks.subList(1, chunks.size());
        I18nFormatterProvider formatterProvider = namedFormatters.get(formatterName);
        if (formatterProvider == null) {
            throw new RuntimeException("Could not find formatter with name: \"" + formatterName + "\" in: \"" + formatter + "\"");
        }
        return formatterProvider.formatter(messages, arguments);
    }

    @Override
    public String format(Locale locale, String message, Object... args) {
        expectNonNull(locale, "locale");
        expectNonNull(message, "message");
        expectNonNull(args, "args");
        return parse(locale, message)
                .format(args);
    }
}
