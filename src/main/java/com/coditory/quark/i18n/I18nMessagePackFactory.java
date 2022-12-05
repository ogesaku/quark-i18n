package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;

import static com.coditory.quark.i18n.Preconditions.expectNonBlank;
import static com.coditory.quark.i18n.Preconditions.expectNonNull;

public final class I18nMessagePackFactory {
    @NotNull
    public static I18nMessagePack scanFileSystem(@NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        return I18nMessagePack.builder()
                .scanFileSystem(firstPattern, others)
                .build();
    }

    @NotNull
    public static I18nMessagePack scanFileSystem(@NotNull FileSystem fileSystem, @NotNull String firstPattern, String... others) {
        expectNonBlank(firstPattern, "firstPattern");
        return I18nMessagePack.builder()
                .scanFileSystem(fileSystem, firstPattern, others)
                .build();
    }

    @NotNull
    public static I18nMessagePack scanClassPath(@NotNull String firstPattern, String... others) {
        return scanClassPath(Thread.currentThread().getContextClassLoader(), firstPattern, others);
    }

    @NotNull
    public static I18nMessagePack scanClassPath(@NotNull ClassLoader classLoader, @NotNull String firstPattern, String... others) {
        expectNonNull(classLoader, "classLoader");
        expectNonBlank(firstPattern, "firstPattern");
        return I18nMessagePack.builder()
                .scanClassPath(classLoader, firstPattern, others)
                .build();
    }
}
