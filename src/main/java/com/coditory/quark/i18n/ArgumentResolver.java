package com.coditory.quark.i18n;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

final class ArgumentResolver {
    private final Map<Class<?>, I18nArgTransformer<?>> transformers;

    static ArgumentResolver of(List<I18nArgTransformer<?>> transformers) {
        Map<Class<?>, I18nArgTransformer<?>> map = transformers.stream().collect(toMap(I18nArgTransformer::getArgType, it -> it));
        return new ArgumentResolver(map);
    }

    private ArgumentResolver(Map<Class<?>, I18nArgTransformer<?>> transformers) {
        this.transformers = Map.copyOf(transformers);
    }

    Object[] resolveArguments(Object[] args) {
        boolean transformable = false;
        for (Object arg : args) {
            if (arg != null && transformers.containsKey(arg.getClass())) {
                transformable = true;
                break;
            }
        }
        if (!transformable) {
            return args;
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; ++i) {
            result[i] = transformArgument(args[i]);
        }
        return result;
    }

    Map<String, Object> resolveArguments(Map<String, Object> args) {
        boolean transformable = args.values().stream()
                .anyMatch(a -> a != null && transformers.containsKey(a.getClass()));
        if (!transformable) {
            return args;
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            String key = entry.getKey();
            Object value = transformArgument(entry.getValue());
            result.put(key, value);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object transformArgument(Object argument) {
        if (argument == null) {
            return null;
        }
        I18nArgTransformer<Object> transformer = (I18nArgTransformer<Object>) transformers.get(argument.getClass());
        if (transformer == null) {
            return argument;
        }
        try {
            return transformer.transform(argument);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Could not transform argument: " + argument, e);
        }
    }
}
