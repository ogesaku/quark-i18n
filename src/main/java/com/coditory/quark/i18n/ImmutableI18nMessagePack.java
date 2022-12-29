package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ImmutableI18nMessagePack implements I18nMessagePack {
    private final Map<I18nKey, MessageTemplate> templates;
    private final MessageTemplateParser parser;
    private final I18nUnresolvedMessageHandler unresolvedMessageHandler;
    private final I18nPath queryPrefix;
    private final I18nKeyGenerator keyGenerator;

    ImmutableI18nMessagePack(
            Map<I18nKey, MessageTemplate> templates,
            MessageTemplateParser parser,
            I18nUnresolvedMessageHandler unresolvedMessageHandler,
            I18nKeyGenerator keyGenerator
    ) {
        this(templates, parser, unresolvedMessageHandler, keyGenerator, null);
    }

    private ImmutableI18nMessagePack(
            Map<I18nKey, MessageTemplate> templates,
            MessageTemplateParser parser,
            I18nUnresolvedMessageHandler unresolvedMessageHandler,
            I18nKeyGenerator keyGenerator,
            I18nPath queryPrefix
    ) {
        expectNonNull(templates, "templates");
        this.templates = Map.copyOf(templates);
        this.parser = expectNonNull(parser, "parser");
        this.unresolvedMessageHandler = expectNonNull(unresolvedMessageHandler, "unresolvedMessageHandler");
        this.keyGenerator = expectNonNull(keyGenerator, "keyGenerator");
        this.queryPrefix = queryPrefix;
    }

    @NotNull
    @Override
    public I18nMessages localize(@NotNull Locale locale) {
        expectNonNull(locale, "locale");
        return new I18nMessages(this, locale);
    }

    @NotNull
    @Override
    public String getMessage(@NotNull I18nKey key, Object... args) {
        expectNonNull(key, "key");
        expectNonNull(args, "args");
        String result = getMessageOrNull(key, args);
        return result == null
                ? unresolvedMessageHandler.onUnresolvedMessage(key, args)
                : result;
    }

    @NotNull
    @Override
    public String getMessage(@NotNull I18nKey key, @NotNull Map<String, Object> args) {
        expectNonNull(key, "key");
        expectNonNull(args, "args");
        String result = getMessageOrNull(key, args);
        return result == null
                ? unresolvedMessageHandler.onUnresolvedMessage(key, args)
                : result;
    }

    @Override
    @Nullable
    public String getMessageOrNull(@NotNull I18nKey key, Object... args) {
        expectNonNull(key, "key");
        expectNonNull(args, "args");
        return getTemplate(key)
                .map(message -> message.resolve(key.locale(), args))
                .orElse(null);
    }

    @Override
    @Nullable
    public String getMessageOrNull(@NotNull I18nKey key, @NotNull Map<String, Object> args) {
        expectNonNull(key, "key");
        expectNonNull(args, "args");
        return getTemplate(key)
                .map(message -> message.resolve(key.locale(), args))
                .orElse(null);
    }

    private Optional<MessageTemplate> getTemplate(I18nKey key) {
        return keyGenerator.keys(key, queryPrefix).stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .findFirst();
    }

    @NotNull
    @Override
    public String format(@NotNull Locale locale, @NotNull String expression, Object... args) {
        expectNonNull(locale, "locale");
        expectNonNull(expression, "expression");
        expectNonNull(args, "args");
        Object value = parser.parseTemplate(locale, expression)
                .resolve(locale, args);
        return Objects.toString(value);
    }

    @NotNull
    @Override
    public String format(@NotNull Locale locale, @NotNull String expression, @NotNull Map<String, Object> args) {
        expectNonNull(locale, "locale");
        expectNonNull(expression, "expression");
        expectNonNull(args, "args");
        Object value = parser.parseTemplate(locale, expression)
                .resolve(locale, args);
        return Objects.toString(value);
    }

    @Override
    @NotNull
    public I18nMessagePack withQueryPrefix(@NotNull String prefix) {
        I18nPath path = I18nPath.of(prefix);
        return new ImmutableI18nMessagePack(templates, parser, unresolvedMessageHandler, keyGenerator, path);
    }
}
