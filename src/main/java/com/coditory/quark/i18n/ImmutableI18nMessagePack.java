package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ImmutableI18nMessagePack implements I18nMessagePack {
    private final Map<I18nKey, MessageTemplate> templates;
    private final MessageTemplateParser parser;
    private final I18nUnresolvedMessageHandler unresolvedMessageHandler;
    private final I18nKeyGenerator keyGenerator;
    private final List<I18nPath> prefixes;

    ImmutableI18nMessagePack(
            Map<I18nKey, MessageTemplate> templates,
            MessageTemplateParser parser,
            I18nUnresolvedMessageHandler unresolvedMessageHandler,
            I18nKeyGenerator keyGenerator,
            List<I18nPath> prefixes
    ) {
        expectNonNull(templates, "templates");
        expectNonNull(parser, "parser");
        expectNonNull(unresolvedMessageHandler, "unresolvedMessageHandler");
        expectNonNull(keyGenerator, "keyGenerator");
        expectNonNull(prefixes, "prefixes");
        this.templates = Map.copyOf(templates);
        this.parser = parser;
        this.unresolvedMessageHandler = unresolvedMessageHandler;
        this.keyGenerator = keyGenerator;
        this.prefixes = List.copyOf(prefixes);
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

    @Override
    @Nullable
    public String getMessageOrNull(@NotNull I18nKey key, Object... args) {
        expectNonNull(key, "key");
        expectNonNull(args, "args");
        I18nMessages messages = localize(key.locale());
        ExpressionResolutionContext context = new ExpressionResolutionContext(Arrays.asList(args), messages);
        return keyGenerator.keys(prefixes, key)
                .stream()
                .map(templates::get)
                .filter(Objects::nonNull)
                .map(message -> message.resolve(context))
                .findFirst()
                .orElse(null);
    }

    @NotNull
    @Override
    public String format(@NotNull Locale locale, @NotNull String expression, Object... args) {
        expectNonNull(locale, "locale");
        expectNonNull(expression, "expression");
        expectNonNull(args, "args");
        I18nMessages messages = localize(locale);
        ExpressionResolutionContext context = new ExpressionResolutionContext(Arrays.asList(args), messages);
        Object value = parser.parse(expression)
                .resolve(context);
        return Objects.toString(value);
    }

    @Override
    @NotNull
    public I18nMessagePack addPrefix(@NotNull String prefix) {
        I18nPath path = I18nPath.of(prefix);
        List<I18nPath> prefixes = new ArrayList<>(this.prefixes);
        prefixes.add(path);
        return new ImmutableI18nMessagePack(templates, parser, unresolvedMessageHandler, keyGenerator, prefixes);
    }
}
