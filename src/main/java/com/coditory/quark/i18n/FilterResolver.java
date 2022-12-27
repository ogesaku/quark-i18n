package com.coditory.quark.i18n;

import com.coditory.quark.i18n.filter.I18nFilter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;

final class FilterResolver {
    private final Map<String, I18nFilter> filtersByName;
    private final TypedFormatter typedFormatter;

    FilterResolver(
            Map<String, I18nFilter> filtersByName,
            Map<Class<?>, I18nFilter> filtersByType
    ) {
        expectNonNull(filtersByName, "filtersByName");
        expectNonNull(filtersByType, "filtersByType");
        this.filtersByName = Map.copyOf(filtersByName);
        this.typedFormatter = new TypedFormatter(filtersByType);
    }

    I18nFilter getFilterByName(String name) {
        expectNonNull(name, "name");
        I18nFilter filter = filtersByName.get(name);
        if (filter == null) {
            throw new RuntimeException("Filter not found. Name: " + name);
        }
        return filter;
    }

    I18nFilter getTypedFormatter() {
        return typedFormatter;
    }
}

final class TypedFormatter implements I18nFilter {
    private static final PassingFormatter PASSING_FORMATTER = new PassingFormatter();
    private final ConcurrentMap<Class<?>, List<Class<?>>> typeHierarchy = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, I18nFilter> filtersByTypeCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, I18nFilter> filtersByType;

    TypedFormatter(Map<Class<?>, I18nFilter> filtersByType) {
        this.filtersByType = Map.copyOf(filtersByType);
    }

    @Override
    @NotNull
    public Object filter(@NotNull I18nFilterContext context) {
        Object value = context.value();
        I18nFilter filter = getFormatter(value.getClass());
        return filter.filter(context);
    }

    private I18nFilter getFormatter(Class<?> type) {
        return filtersByTypeCache.computeIfAbsent(type, (__) ->
                getTypeHierarchy(type).stream()
                        .filter(filtersByType::containsKey)
                        .map(filtersByType::get)
                        .findFirst()
                        .orElse(PASSING_FORMATTER)
        );
    }

    private List<Class<?>> getTypeHierarchy(Class<?> type) {
        return typeHierarchy.computeIfAbsent(type, Reflections::getAllInterfacesAndClasses);
    }
}

final class PassingFormatter implements I18nFilter {
    @Override
    @NotNull
    public Object filter(@NotNull I18nFilterContext context) {
        return context.value();
    }
}