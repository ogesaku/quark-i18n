package com.coditory.quark.i18n.filter;

import com.coditory.quark.i18n.Expression;
import com.coditory.quark.i18n.I18nFilterContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface I18nFilter {
    @NotNull
    Object filter(@NotNull I18nFilterContext context);

    default void validate(@NotNull List<Expression> arguments) {
        // executed when expressions are parsed to fail fast
        // override to fail fast when expression is incorrect
        // by default all filters are valid, hence method is empty
    }
}