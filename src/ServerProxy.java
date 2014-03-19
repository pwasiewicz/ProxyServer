import skj.serverproxy.core.DefaultServerProxyShell;
import skj.serverproxy.core.IServerProxyConfiguration;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.arguments.ArgumentResolver;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import skj.serverproxy.core.filters.defaultFilters.ConnectionCloseFilter;
import skj.serverproxy.core.filters.defaultFilters.TextResponseOnlyFilter;
import skj.serverproxy.core.models.ServerMode;

import java.io.IOException;

/**
 * Main server class
 * Created by pwasiewicz on 09.03.14.
 */
public class ServerProxy {

    public static void main(String... args) throws MissingArgumentException, IOException {

        System.out.println("Starting server...");

        ArgumentResolver argsResolver = new ArgumentResolver();
        argsResolver.resolve(args);

        IServerProxyConfiguration configuration = DefaultServerProxyShell
                                        .initialize()
                                        .onPort(argsResolver.getPort());

        if (argsResolver.getMode() == ServerMode.LIGHT) {
             configuration.registerResponseFilter(new TextResponseOnlyFilter());
        }

        configuration.registerResponseFilter(new ConnectionCloseFilter());

        IServerProxyCore server = configuration.start();

        System.out.println("Server started. Press any key to exit.");
        System.in.read();

        server.stop();
    }
}
