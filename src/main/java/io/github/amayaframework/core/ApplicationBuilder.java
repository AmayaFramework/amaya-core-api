package io.github.amayaframework.core;

import com.github.romanqed.jconv.PipelineBuilder;
import io.github.amayaframework.context.HttpContext;
import io.github.amayaframework.di.ServiceProviderBuilder;
import io.github.amayaframework.environment.EnvironmentFactory;
import io.github.amayaframework.options.GroupOptionSet;
import io.github.amayaframework.server.HttpServerFactory;

import java.net.InetSocketAddress;

public interface ApplicationBuilder extends Resettable<ApplicationBuilder> {

    GroupOptionSet getOptions();

    ApplicationBuilder setOptions(GroupOptionSet options);

    ApplicationBuilder configure(OptionSetConsumer action);

    ApplicationBuilder setEnvironmentFactory(EnvironmentFactory factory);

    ApplicationBuilder setEnvironmentName(String name);

    ServiceManagerBuilder getManagerBuilder();

    ApplicationBuilder configure(ManagerBuilderConsumer action);

    PipelineBuilder<HttpContext> getHandlerBuilder();

    ApplicationBuilder configure(HandlerBuilderConsumer action);

    ServiceProviderBuilder getProviderBuilder();

    ApplicationBuilder configure(ProviderBuilderConsumer action);

    ApplicationBuilder configure(ProviderConsumer action);

    ApplicationBuilder setServerFactory(HttpServerFactory factory);

    ApplicationBuilder configure(HttpConfigConsumer action);

    ApplicationBuilder bind(InetSocketAddress address);

    ApplicationBuilder bind(int port);

    Application build();
}
