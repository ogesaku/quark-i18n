package com.coditory.quark.i18n.base

import groovy.transform.CompileStatic

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

@CompileStatic
class ClassLoaderStub extends ClassLoader {
    private final Map<String, URL> overrides = new ConcurrentHashMap<>()

    void add(String name, URL url) {
        String normalized = normalizeName(name)
        println "CLASSPATH ADD: ${normalized} ${url}"
        overrides.put(normalized, url)
    }

    void add(String name, File file) {
        try {
            add(name, file.toURI().toURL())
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid file", e)
        }
    }

    void add(String name, String filePath) {
        add(name, Path.of(filePath).toFile())
    }

    public <T> T setupInThreadContext(Supplier<T> supplier) {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader()
        Thread.currentThread().setContextClassLoader(this)
        try {
            return supplier.get()
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader)
        }
    }

    @Override
    URL getResource(String name) {
        Objects.requireNonNull(name)
        URL url = overrides.get(normalizeName(name))
        return url != null
                ? url
                : super.getResource(name)
    }

    @Override
    Enumeration<URL> getResources(String name) throws IOException {
        Objects.requireNonNull(name)
        URL resource = getResource(normalizeName(name))
        return resource != null
                ? Collections.enumeration([resource])
                : super.getResources(name)
    }

    private String normalizeName(String name) {
        return name.endsWith("/")
                ? name.substring(0, name.length())
                : name
    }
}
