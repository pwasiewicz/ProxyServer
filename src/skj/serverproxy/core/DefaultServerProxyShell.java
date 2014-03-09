package skj.serverproxy.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import skj.serverproxy.core.arguments.*;
import skj.serverproxy.core.implementations.DefaultServerProxyCore;
import skj.serverproxy.core.proxy.IClientProxyFactory;
import skj.serverproxy.core.proxy.implementations.DefaultClientProxyFactory;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyShell extends AbstractModule {

    public DefaultServerProxyShell() {
    }

    public static IServerProxyCore initialize(){
        Injector shellInjection = Guice.createInjector(new DefaultServerProxyShell());
        return shellInjection.getInstance(IServerProxyCore.class);
    }

    @Override
    protected void configure() {
        bind(IServerProxyCore.class).to(DefaultServerProxyCore.class);
        bind(IArgumentResolver.class).to(ArgumentResolver.class).in(Scopes.SINGLETON);
        bind(IClientProxyFactory.class).to(DefaultClientProxyFactory.class);
    }
}
