import skj.serverproxy.core.DefaultServerProxyShell;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;

import java.io.IOException;

/**
 * Main server class
 * Created by pwasiewicz on 09.03.14.
 */
public class ServerProxy {

    public static void main(String... args) throws MissingArgumentException, IOException {
        IServerProxyCore proxyCore = DefaultServerProxyShell.initialize();

        proxyCore.resolveArgs(args);
        proxyCore.run();
    }
}
