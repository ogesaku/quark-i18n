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
trait UsesInMemFs {
    private FileSystem inMemFs = Jimfs.newFileSystem(Configuration.unix())

    void writeInMemFsFile(String fileName, String content) {
        setupInMemFs()
        Path path = inMemFs.getPath(fileName)
        println "Writing in-mem file: " + path.isAbsolute() + " " + path.toAbsolutePath()
        if (!Files.exists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        String trimmed = content.stripIndent().trim()
        Files.writeString(path, trimmed)
    }

    I18nMessagePack scanInMemFs(String firstPattern, String... others) {
        setupInMemFs()
        return I18nMessagePackFactory.scanFileSystem(inMemFs, firstPattern, others)
    }

    private void setupInMemFs() {
        if (inMemFs == null) {
            inMemFs = Jimfs.newFileSystem(Configuration.unix())
        }
    }
}