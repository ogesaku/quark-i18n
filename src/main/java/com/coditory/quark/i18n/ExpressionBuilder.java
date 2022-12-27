package com.coditory.quark.i18n;

import com.coditory.quark.i18n.filter.I18nFilter;

import java.util.ArrayList;
import java.util.List;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class ExpressionBuilder {
    public static ExpressionBuilder argument(FilterResolver filterResolver) {
        return new ExpressionBuilder(false, filterResolver);
    }

    public static ExpressionBuilder reference(FilterResolver filterResolver) {
        return new ExpressionBuilder(true, filterResolver);
    }

    private final FilterResolver filterResolver;
    private final List<ExpressionFilter> filters = new ArrayList<>();
    private final boolean reference;
    private String value;
    private boolean separated = false;

    private ExpressionBuilder(boolean reference, FilterResolver filterResolver) {
        expectNonNull(filterResolver, "filterResolver");
        this.reference = reference;
        this.filterResolver = filterResolver;
    }

    public void addToken(StringBuilder sb) {
        if (!sb.isEmpty()) {
            addToken(StaticExpression.parse(sb));
        }
    }

    public void addToken(Expression expression) {
        if (value == null) {
            value = getStaticValue(expression);
        } else if (separated) {
            addNewFilter(expression);
        } else if (!filters.isEmpty()) {
            getLastFilter().addArgument(expression);
        }
        separated = false;
    }

    private void addNewFilter(Expression expression) {
        String name = getStaticValue(expression);
        I18nFilter filter = filterResolver.getFilterByName(name);
        filters.add(new ExpressionFilter(name, filter));
    }

    private String getStaticValue(Expression expression) {
        if (expression instanceof StaticExpression staticExpression) {
            return staticExpression.getValue();
        }
        throw new IllegalArgumentException("Expected static value. Got: " + expression);
    }

    private ExpressionFilter getLastFilter() {
        if (filters.isEmpty()) {
            throw new IllegalStateException("No filter defined");
        }
        int lastIndex = filters.size() - 1;
        return filters.get(lastIndex);
    }

    public void pipe() {
        if (value == null) {
            throw new IllegalArgumentException("Expected value before '|'. Example: \"{ 0 | uppercase }\"");
        }
        if (separated) {
            throw new IllegalArgumentException("Invalid expression syntax. Detected two filter separators \"|\" next to each other.");
        }
        separated = true;
    }

    public Expression build() {
        return reference
                ? ReferenceExpression.parse(value, filters)
                : ArgExpression.parse(value, filters);
    }
}
