package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nMessages;
import com.coditory.quark.i18n.MessageTemplateParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class FormatterContext {
    private final List<String> args;
    private final I18nMessages messages;
    private final MessageTemplateParser parser;

    public FormatterContext(List<String> args, I18nMessages messages, MessageTemplateParser parser) {
        this.args = List.copyOf(args);
        this.messages = requireNonNull(messages);
        this.parser = requireNonNull(parser);
    }

    @NotNull
    public List<String> getArgs() {
        return args;
    }

    @Nullable
    public String getFirstArgOrNull() {
        return args.isEmpty() ? null : args.get(0);
    }

    @NotNull
    public I18nMessages getMessages() {
        return messages;
    }

    @NotNull
    public MessageTemplateParser getParser() {
        return parser;
    }
}
