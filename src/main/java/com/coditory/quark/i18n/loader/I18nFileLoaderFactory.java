package com.coditory.quark.i18n.loader;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import static java.util.Objects.requireNonNull;

public final class I18nFileLoaderFactory {
    @NotNull
    public static I18nLoader scanClassPath(@NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        return scanClassPath(Thread.currentThread().getContextClassLoader(), firstPattern, others);
    }

    @NotNull
    public static I18nLoader scanClassPath(@NotNull ClassLoader classLoader, @NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        requireNonNull(classLoader);
        I18nFileLoaderBuilder builder = new I18nFileLoaderBuilder()
                .classLoader(classLoader)
                .scanPathPattern(firstPattern);
        for (String other : others) {
            builder.scanPathPattern(other);
        }
        return builder.build();
    }

    @NotNull
    public static I18nLoader scanFileSystem(@NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        return scanFileSystem(FileSystems.getDefault(), firstPattern, others);
    }

    @NotNull
    public static I18nLoader scanFileSystem(@NotNull FileSystem fileSystem, @NotNull String firstPattern, String... others) {
        requireNonNull(firstPattern);
        requireNonNull(fileSystem);
        I18nFileLoaderBuilder builder = new I18nFileLoaderBuilder()
                .scanFileSystem(fileSystem)
                .scanPathPattern(firstPattern);
        for (String other : others) {
            builder.scanPathPattern(other);
        }
        return builder.build();
    }
}
