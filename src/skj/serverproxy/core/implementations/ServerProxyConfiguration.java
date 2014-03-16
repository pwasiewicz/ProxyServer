package skj.serverproxy.core.implementations;

import skj.serverproxy.core.IServerProxyConfiguration;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.ServerProxyRunnable;
import skj.serverproxy.core.models.ServerMode;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public class ServerProxyConfiguration implements IServerProxyConfiguration {

    private final ServerProxyRunnable serverRunnable;

    private int port = 13000;

    private  int sslPort = 13001;

    private ServerMode serverMode = ServerMode.HEAVY;

    public ServerProxyConfiguration(ServerProxyRunnable serverRunnable){
        this.serverRunnable = serverRunnable;
    }

    @Override
    public IServerProxyConfiguration onPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public IServerProxyConfiguration onSSLPort(int port) {
        this.sslPort = port;
        return  this;
    }

    @Override
    public IServerProxyConfiguration setMode(ServerMode mode) {
        this.serverMode = mode;
        return this;
    }

    @Override
    public IServerProxyCore start() {
        return this.serverRunnable.run(this);
    }
}
