package com.coditory.quark.i18n.filter;

import com.coditory.quark.i18n.Expression;
import com.coditory.quark.i18n.I18nFilterContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Objects.requireNonNull;

public final class IntervalI18nFilter implements I18nFilter {
    public static final String FILTER = "interval";

    @Override
    @NotNull
    public Object filter(@NotNull I18nFilterContext context) {
        requireNonNull(context);
        int value = context.valueAsInt();
        List<Expression> args = context.filterArgs();
        if (args.size() < 3) {
            throw new RuntimeException("Expected at least 3 filter arguments got: " + args);
        }
        if (args.size() % 2 != 1) {
            throw new RuntimeException("Expected odd number of filter arguments got: " + args);
        }
        Integer lower = null;
        for (int i = 0; i < args.size(); i += 2) {
            Integer upper = i + 1 < args.size()
                    ? context.resolveToInt(args.get(i + 1))
                    : null;
            if ((lower == null || lower <= value) && (upper == null || value < upper)) {
                return context.resolveToString(args.get(i));
            }
            if (lower != null && lower > upper) {
                throw new RuntimeException("Expected increasing interval values");
            }
            lower = upper;
        }
        return context.resolveToString(args.get(args.size() - 1));
    }
}
