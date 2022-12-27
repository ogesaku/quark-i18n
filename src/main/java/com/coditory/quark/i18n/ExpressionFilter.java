package com.coditory.quark.i18n;

import com.coditory.quark.i18n.filter.I18nFilter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

final class ExpressionFilter {
    private final String name;
    private final I18nFilter filter;
    private final List<Expression> arguments = new ArrayList<>();

    public ExpressionFilter(String name, I18nFilter filter) {
        this.name = requireNonNull(name);
        this.filter = requireNonNull(filter);
    }

    void addArgument(Expression argument) {
        arguments.add(argument);
    }

    void validate() {
        try {
            filter.validate(arguments);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid arguments for expression filter: " + name, e);
        }
    }
}
