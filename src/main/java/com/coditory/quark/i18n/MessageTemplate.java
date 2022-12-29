package com.coditory.quark.i18n;

import com.ibm.icu.text.MessageFormat;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class MessageTemplate {
    static MessageTemplate parse(String template) {
        try {
            MessageFormat messageFormat = new MessageFormat(template);
            return new MessageTemplate(template, messageFormat);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse message template: " + template, e);
        }
    }

    private final ConcurrentHashMap<Locale, MessageFormat> formats = new ConcurrentHashMap<>();
    private final String template;
    private final MessageFormat messageFormat;
    private final boolean dynamic;

    private MessageTemplate(String template, MessageFormat messageFormat) {
        expectNonNull(template, "template");
        expectNonNull(messageFormat, "messageFormat");
        this.template = template;
        this.messageFormat = messageFormat;
        this.dynamic = messageFormat.getFormats().length > 0;
    }

    public String resolve(Locale locale, Object[] args) {
        expectNonNull(locale, "locale");
        expectNonNull(args, "args");
        MessageFormat messageFormat = getMessageFormat(locale);
        return messageFormat.format(args);
    }

    public String resolve(Locale locale, Map<String, Object> args) {
        expectNonNull(locale, "locale");
        expectNonNull(args, "args");
        MessageFormat messageFormat = getMessageFormat(locale);
        return messageFormat.format(args);
    }

    private MessageFormat getMessageFormat(Locale locale) {
        return dynamic
                ? formats.computeIfAbsent(locale, this::createMessageFormat)
                : messageFormat;
    }

    private MessageFormat createMessageFormat(Locale locale) {
        MessageFormat copy = (MessageFormat) this.messageFormat.clone();
        copy.setLocale(locale);
        return copy;
    }

    @Override
    public String toString() {
        return "MessageTemplate{" + template + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageTemplate that = (MessageTemplate) o;
        return template.equals(that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template);
    }
}