package skj.serverproxy.core;

import skj.serverproxy.core.implementations.ServerProxyConfiguration;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public interface ServerProxyRunnable {
    IServerProxyCore run(ServerProxyConfiguration configuration);
}
