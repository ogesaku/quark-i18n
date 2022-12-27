package com.coditory.quark.i18n.formatter;

import com.coditory.quark.i18n.I18nFilterContext;
import com.coditory.quark.i18n.filter.I18nFilter;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class NumberI18nFilter implements I18nFilter {
    public static final String FILTER = "number";
    private final I18nFilter intFilter = new IntI18nFilter();
    private final I18nFilter floatFilter = new FloatI18nFilter();

    @Override
    @NotNull
    public Object filter(@NotNull I18nFilterContext context) {
        requireNonNull(context);
        Object value = context.value();
        return value instanceof Integer || value instanceof Long || value instanceof Byte
                ? intFilter.filter(context)
                : floatFilter.filter(context);
    }
}
