package com.coditory.quark.i18n.base

import com.coditory.quark.i18n.I18nMessagePack
import com.coditory.quark.i18n.I18nMessagePackFactory
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import groovy.transform.CompileStatic

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

@CompileStatic
trait UsesInMemClassLoader {
    private FileSystem inMemFs
    private Path classLoaderPath
    private ClassLoader classLoader

    void writeInMemClassPathFile(String fileName, String content) {
        setupClassLoaderStub()
        Path path = classLoaderPath.resolve(fileName)
        if (!Files.exists(path.parent)) {
            println "Writing classpath file: " + path
            Files.createDirectories(path.parent)
        }
        String trimmed = content.stripIndent().trim()
        Files.writeString(path, trimmed)
    }

    I18nMessagePack scanInMemClassPath(String firstPattern, String... others) {
        setupClassLoaderStub()
        return I18nMessagePackFactory.scanClassPath(classLoader, firstPattern, others)
    }

    private void setupClassLoaderStub() {
        if (classLoader != null) {
            return
        }
        inMemFs = Jimfs.newFileSystem(Configuration.unix())
        classLoaderPath = inMemFs.getPath("")
        classLoader = new URLClassLoader(
                new URL[]{classLoaderPath.toUri().toURL()},
                Thread.currentThread().getContextClassLoader()
        )
    }
}