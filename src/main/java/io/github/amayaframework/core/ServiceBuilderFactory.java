package io.github.amayaframework.core;

import io.github.amayaframework.di.ServiceProviderBuilder;
import io.github.amayaframework.options.GroupOptionSet;

import java.util.Objects;
import java.util.function.Supplier;

public final class ServiceBuilderFactory implements ApplicationBuilderFactory {
    private final Supplier<ServiceProviderBuilder> supplier;

    public ServiceBuilderFactory(Supplier<ServiceProviderBuilder> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public ApplicationBuilder create(GroupOptionSet options) {
        var ret = create();
        ret.setOptions(options);
        return ret;
    }

    @Override
    public ApplicationBuilder create() {
        var managerBuilder = new ProvidedManagerBuilder(Defaults.DEFAULT_MANAGER_FACTORY, Defaults.DEFAULT_HANDLER);
        return new ProvidedApplicationBuilder(managerBuilder, supplier, Defaults.DEFAULT_ENVIRONMENT_FACTORY);
    }
}
