package skj.serverproxy.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import skj.serverproxy.core.arguments.*;
import skj.serverproxy.core.implementations.DefaultServerProxyCore;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyShell extends AbstractModule {

    private DefaultServerProxyShell() {
    }

    public static IServerProxyCore initialize(){
        Injector shellInjection = Guice.createInjector(new DefaultServerProxyShell());

        return shellInjection.getInstance(IServerProxyCore.class);
    }

    @Override
    protected void configure() {
        // add dependencies here
        bind(IServerProxyCore.class).to(DefaultServerProxyCore.class);
        bind(IArgumentResolver.class).to(ArgumentResolver.class);
    }
}
