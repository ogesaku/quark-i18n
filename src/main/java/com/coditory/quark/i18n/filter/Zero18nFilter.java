package com.coditory.quark.i18n.filter;

import com.coditory.quark.i18n.Expression;
import com.coditory.quark.i18n.I18nFilterContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class Zero18nFilter implements I18nFilter {
    @Override
    public void validate(@NotNull List<Expression> args) {
        requireNonNull(args);
        if (args.size() != 2) {
            throw new IllegalArgumentException("Expected two expression arguments. Got: " + args.size());
        }
    }

    @Override
    @NotNull
    public Object filter(@NotNull I18nFilterContext context) {
        requireNonNull(context);
        List<Expression> expressions = context.filterArgs();
        if (expressions.size() != 2) {
            throw new IllegalArgumentException("Expected two expressions. Got: " + expressions.size());
        }
        Expression zero = expressions.get(0);
        Expression nonZero = expressions.get(1);
        return isZero(context.value()) ? context.resolve(zero) : context.resolve(nonZero);
    }

    private boolean isZero(Object value) {
        if (value instanceof Number number) {
            return number.longValue() == 0;
        }
        throw new IllegalArgumentException("Expected numerical value. Got: " + value);
    }
}