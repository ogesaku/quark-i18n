package com.coditory.quark.i18n;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class Reflections {
    static List<Class<?>> getAllInterfacesAndClasses(Class<?> clazz) {
        Set<Class<?>> result = new LinkedHashSet<>();
        getAllInterfacesAndClasses(clazz, result);
        return List.copyOf(result);
    }

    private static void getAllInterfacesAndClasses(Class<?> clazz, Set<Class<?>> visited) {
        while (clazz != null && !visited.contains(clazz)) {
            visited.add(clazz);
            for (Class<?> iface : clazz.getInterfaces()) {
                getAllInterfacesAndClasses(iface, visited);
            }
            clazz = clazz.getSuperclass();
        }
    }
}
