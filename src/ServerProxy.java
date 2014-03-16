import skj.serverproxy.core.DefaultServerProxyShell;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.arguments.ArgumentResolver;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;

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

        IServerProxyCore server = DefaultServerProxyShell
                                        .initialize()
                                        .onPort(argsResolver.getPort())
                                        .setMode(argsResolver.getMode())
                                        .start();

        System.out.println("Server started. Press any key to exit.");
        System.in.read();

        server.stop();
    }
}
