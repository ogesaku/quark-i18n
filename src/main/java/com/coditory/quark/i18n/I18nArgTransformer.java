package com.coditory.quark.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

interface I18nArgTransformer<T> {
    static <T> I18nArgTransformer<T> of(Class<T> type, Function<T, Object> transform) {
        return new SimpleI18nArgTransformer<>(type, transform);
    }

    Class<T> getArgType();

    @NotNull
    Object transform(@NotNull T value);
}

class SimpleI18nArgTransformer<T> implements I18nArgTransformer<T> {
    private final Class<T> type;
    private final Function<T, Object> transform;

    public SimpleI18nArgTransformer(Class<T> type, Function<T, Object> transform) {
        this.type = requireNonNull(type);
        this.transform = requireNonNull(transform);
    }

    @Override
    public Class<T> getArgType() {
        return type;
    }

    @Override
    public @NotNull Object transform(@NotNull T value) {
        return transform.apply(value);
    }
}