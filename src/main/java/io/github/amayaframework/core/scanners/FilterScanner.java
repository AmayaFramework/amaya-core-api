package io.github.amayaframework.core.scanners;

import io.github.amayaframework.core.util.ReflectUtil;
import io.github.amayaframework.filters.Filter;
import io.github.amayaframework.filters.NamedFilter;
import io.github.amayaframework.filters.NamedFilters;

import java.util.Map;
import java.util.Objects;

public class FilterScanner<T extends Filter> implements Scanner<Map<String, T>> {
    private final Class<T> clazz;

    public FilterScanner(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
    }

    @Override
    public Map<String, T> find() throws Exception {
        Map<NamedFilter[], T> multiple = ReflectUtil.
                findAnnotatedWithValue(NamedFilters.class, clazz, NamedFilter[].class);
        Map<String, T> single = ReflectUtil.findAnnotatedWithValue(NamedFilter.class, clazz, String.class);
        multiple.forEach((key, value) -> {
            for (NamedFilter filter : key) {
                single.put(filter.value(), value);
            }
        });
        return single;
    }
}
