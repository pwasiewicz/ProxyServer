import skj.serverproxy.core.DefaultServerProxyShell;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;

/**
 * Main server class
 * Created by pwasiewicz on 09.03.14.
 */
public class ServerProxy {

    public static void main(String... args) throws MissingArgumentException {
        IServerProxyCore proxyCore = DefaultServerProxyShell.initialize();

        proxyCore.resolveArgs(args);
        proxyCore.run();
    }
}
