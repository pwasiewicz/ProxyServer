package skj.serverproxy.core;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.sun.net.httpserver.HttpHandler;
import skj.serverproxy.core.arguments.*;
import skj.serverproxy.core.arguments.cache.DefaultCacheManager;
import skj.serverproxy.core.arguments.cache.ICacheManager;
import skj.serverproxy.core.implementations.ClientHandler;
import skj.serverproxy.core.implementations.DefaultServerProxyCore;
import skj.serverproxy.core.implementations.ServerProxyConfiguration;

import java.io.IOException;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyShell extends AbstractModule {

    public DefaultServerProxyShell() {
    }

    public static IServerProxyConfiguration initialize() {

        ServerProxyConfiguration configurable = new ServerProxyConfiguration(new ServerProxyRunnable() {
            @Override
            public IServerProxyCore run(ServerProxyConfiguration configuration) {
                Injector shellInjection = Guice.createInjector(new DefaultServerProxyShell());
                IServerProxyCore server = shellInjection.getInstance(IServerProxyCore.class);

                server.setConfiguration(configuration);
                try {
                    server.run();
                } catch (IOException e) {
                    // TODO: handle error
                    e.printStackTrace();
                    return null;
                }

                return server;
            }
        });

        return configurable;
    }

    @Override
    protected void configure() {
        bind(IServerProxyCore.class).to(DefaultServerProxyCore.class);
        bind(ISocketHandler.class).to(ClientHandler.class);
    }
}
