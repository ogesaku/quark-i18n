package com.coditory.quark.i18n.loader;

import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

@FunctionalInterface
public interface I18nLoader {
    static I18nFileLoaderBuilder classPathLoader() {
        return classPathLoader(Thread.currentThread().getContextClassLoader());
    }

    static I18nFileLoaderBuilder classPathLoader(ClassLoader classLoader) {
        return new I18nFileLoaderBuilder()
                .scanClassPath(classLoader);
    }

    static I18nFileLoaderBuilder fileSystemLoader() {
        return fileSystemLoader(FileSystems.getDefault());
    }

    static I18nFileLoaderBuilder fileSystemLoader(FileSystem fileSystem) {
        return new I18nFileLoaderBuilder()
                .scanFileSystem(fileSystem);
    }

    @NotNull
    List<I18nMessageBundle> load();
}
