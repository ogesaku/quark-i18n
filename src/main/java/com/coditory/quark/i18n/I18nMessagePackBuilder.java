package com.coditory.quark.i18n;

import com.coditory.quark.i18n.loader.I18nFileLoaderFactory;
import com.coditory.quark.i18n.loader.I18nLoader;
import com.coditory.quark.i18n.loader.I18nTemplates;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import static com.coditory.quark.i18n.I18nArgTransformers.javaTimeI18nArgTransformers;
import static com.coditory.quark.i18n.Preconditions.expectNonBlank;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class I18nMessagePackBuilder {
    private final AggregatedI18nLoader loader = new AggregatedI18nLoader();
    private final List<I18nPath> referenceFallbackPaths = new ArrayList<>();
    private final List<I18nPath> messageFallbackPaths = new ArrayList<>();
    private final List<I18nArgTransformer<?>> argTransformers = new ArrayList<>();
    private boolean useJava8ArgumentTransformers = true;
    private I18nUnresolvedMessageHandler unresolvedMessageHandler = I18nUnresolvedMessageHandler.throwError();
    private Locale defaultLocale;

    private I18nMessagePackBuilder copy() {
        I18nMessagePackBuilder builder = new I18nMessagePackBuilder();
        builder.loader.addLoader(loader.copy());
        builder.referenceFallbackPaths.clear();
        builder.referenceFallbackPaths.addAll(referenceFallbackPaths);
        builder.messageFallbackPaths.clear();
        builder.messageFallbackPaths.addAll(messageFallbackPaths);
        builder.unresolvedMessageHandler = unresolvedMessageHandler;
        return builder;
    }

    @NotNull
    public I18nMessagePackBuilder scanFileSystem(@NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        I18nLoader loader = I18nFileLoaderFactory.scanFileSystem(firstPattern, others);
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder scanFileSystem(@NotNull FileSystem fileSystem, @NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        expectNonNull(fileSystem, "fileSystem");
        I18nLoader loader = I18nFileLoaderFactory.scanFileSystem(fileSystem, firstPattern, others);
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder scanClassPath(@NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        return scanClassPath(Thread.currentThread().getContextClassLoader(), firstPattern, others);
    }

    @NotNull
    public I18nMessagePackBuilder scanClassPath(@NotNull ClassLoader classLoader, @NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        expectNonNull(classLoader, "classLoader");
        I18nLoader loader = I18nFileLoaderFactory.scanClassPath(classLoader, firstPattern, others);
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public <T> I18nMessagePackBuilder addArgumentTransformer(Class<T> type, Function<T, Object> transform) {
        expectNonNull(type, "type");
        expectNonNull(transform, "transform");
        this.argTransformers.add(I18nArgTransformer.of(type, transform));
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder disableJava8ArgumentTransformers() {
        this.useJava8ArgumentTransformers = false;
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addLoader(@NotNull I18nLoader loader) {
        expectNonNull(loader, "loader");
        this.loader.addLoader(loader);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull Locale locale, @NotNull I18nPath path, @NotNull String template) {
        expectNonNull(locale, "locale");
        expectNonNull(path, "path");
        expectNonBlank(template, "template");
        return addMessage(I18nKey.of(locale, path), template);
    }

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull Locale locale, @NotNull String path, @NotNull String template) {
        expectNonNull(locale, "locale");
        expectNonBlank(path, "path");
        expectNonBlank(template, "template");
        return addMessage(I18nKey.of(locale, path), template);
    }

    @NotNull
    public I18nMessagePackBuilder addMessage(@NotNull I18nKey key, @NotNull String template) {
        expectNonNull(key, "key");
        expectNonBlank(template, "template");
        this.loader.addMessage(key, template);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addMessages(@NotNull Map<I18nKey, String> messages) {
        expectNonNull(messages, "messages");
        this.loader.addMessages(messages);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setUnresolvedMessageHandler(@NotNull I18nUnresolvedMessageHandler unresolvedMessageHandler) {
        expectNonNull(unresolvedMessageHandler, "unresolvedMessageHandler");
        this.unresolvedMessageHandler = unresolvedMessageHandler;
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setDefaultLocale(@NotNull Locale defaultLocale) {
        expectNonNull(defaultLocale, "defaultLocale");
        this.defaultLocale = defaultLocale;
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setReferenceFallbackPaths(@NotNull List<String> paths) {
        expectNonNull(paths, "paths");
        this.referenceFallbackPaths.clear();
        paths.forEach(this::addReferenceFallbackPath);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setReferenceFallbackPaths(@NotNull String... paths) {
        expectNonNull(paths, "paths");
        this.referenceFallbackPaths.clear();
        Arrays.stream(paths).forEach(this::addReferenceFallbackPath);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addReferenceFallbackPath(@NotNull String path) {
        expectNonBlank(path, "paths");
        I18nPath i18nPath = I18nPath.of(path);
        this.referenceFallbackPaths.add(i18nPath);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setMessageFallbackPaths(@NotNull List<String> paths) {
        expectNonNull(paths, "paths");
        this.messageFallbackPaths.clear();
        paths.forEach(this::addMessageFallbackPath);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setMessageFallbackPaths(@NotNull String... paths) {
        expectNonNull(paths, "paths");
        this.messageFallbackPaths.clear();
        Arrays.stream(paths).forEach(this::addMessageFallbackPath);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addMessageFallbackPath(@NotNull String path) {
        expectNonBlank(path, "paths");
        I18nPath i18nPath = I18nPath.of(path);
        this.messageFallbackPaths.add(i18nPath);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setFallbackPaths(@NotNull List<String> paths) {
        expectNonNull(paths, "paths");
        setMessageFallbackPaths(paths);
        setReferenceFallbackPaths(paths);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder setFallbackPaths(@NotNull String... paths) {
        expectNonNull(paths, "paths");
        setMessageFallbackPaths(paths);
        setReferenceFallbackPaths(paths);
        return this;
    }

    @NotNull
    public I18nMessagePackBuilder addFallbackPaths(@NotNull String path) {
        expectNonBlank(path, "paths");
        addMessageFallbackPath(path);
        addReferenceFallbackPath(path);
        return this;
    }

    @NotNull
    public I18nMessagePack build() {
        List<I18nTemplates> entries = loader.load();
        return build(entries);
    }

    @NotNull
    public Reloadable18nMessagePack buildReloadable() {
        I18nMessagePackBuilder copy = this.copy();
        AggregatedI18nLoader loader = copy.loader;
        return new Reloadable18nMessagePack(loader, copy::build);
    }

    @NotNull
    public Reloadable18nMessagePack buildAndWatchForChanges() {
        Reloadable18nMessagePack messagePack = buildReloadable();
        messagePack.startWatching();
        return messagePack;
    }

    private I18nMessagePack build(List<I18nTemplates> entries) {
        LocaleResolver localeResolver = LocaleResolver.of(defaultLocale, entries);
        I18nKeyGenerator messageKeyGenerator = new I18nKeyGenerator(defaultLocale, messageFallbackPaths, localeResolver);
        MessageTemplateParser parser = buildMessageTemplateParser(entries, localeResolver);
        Map<I18nKey, MessageTemplate> templates = parser.parseTemplates(entries);
        return new ImmutableI18nMessagePack(templates, parser, unresolvedMessageHandler, messageKeyGenerator);
    }

    private MessageTemplateParser buildMessageTemplateParser(List<I18nTemplates> entries, LocaleResolver localeResolver) {
        I18nKeyGenerator referenceKeyGenerator = new I18nKeyGenerator(defaultLocale, referenceFallbackPaths, localeResolver);
        ReferenceResolver referenceResolver = new ReferenceResolver(entries, referenceKeyGenerator);
        ArgumentResolver argumentResolver = buildArgumentResolver();
        return new MessageTemplateParser(referenceResolver, argumentResolver);
    }

    private ArgumentResolver buildArgumentResolver() {
        List<I18nArgTransformer<?>> result = new ArrayList<>();
        if (useJava8ArgumentTransformers) {
            result.addAll(javaTimeI18nArgTransformers());
        }
        result.addAll(argTransformers);
        return ArgumentResolver.of(result);
    }
}
