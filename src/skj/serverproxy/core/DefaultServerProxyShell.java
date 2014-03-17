package skj.serverproxy.core;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import skj.serverproxy.core.implementations.ClientHandler;
import skj.serverproxy.core.implementations.DefaultServerProxyCore;
import skj.serverproxy.core.implementations.ServerProxyConfiguration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Logger;

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
        bindListener(Matchers.any(), new StockLoggerListener());
    }

    private class StockLoggerListener implements TypeListener {

        @Override
        public <I> void hear(TypeLiteral<I> iTypeLiteral, TypeEncounter<I> iTypeEncounter) {
            for (Field field : iTypeLiteral.getRawType().getDeclaredFields()) {
                if (field.getType() == Logger.class) {
                    iTypeEncounter.register(new StockLoggerInjection<I>(field, iTypeLiteral.getRawType()));
                }
            }
        }
    }

    private class StockLoggerInjection<I> implements MembersInjector<I> {

        final Field field;
        final Logger logger;

        public StockLoggerInjection(Field field, Class<? super I> rawType) {

            this.field = field;
            this.logger = Logger.getLogger(rawType.getCanonicalName());

        }

        @Override
        public void injectMembers(Object o) {
            try {
                field.set(o, this.logger);
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
    }
}
