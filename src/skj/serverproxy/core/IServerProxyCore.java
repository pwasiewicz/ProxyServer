package skj.serverproxy.core;

import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import skj.serverproxy.core.implementations.ServerProxyConfiguration;

import java.io.IOException;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public interface IServerProxyCore {
    void run() throws IOException;
    void stop();
    boolean isRunning();
    void setConfiguration(ServerProxyConfiguration serverProxyConfiguration);
}
