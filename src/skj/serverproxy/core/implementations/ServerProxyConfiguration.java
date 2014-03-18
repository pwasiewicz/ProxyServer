package skj.serverproxy.core.implementations;

import com.google.inject.Inject;
import skj.serverproxy.core.*;
import skj.serverproxy.core.models.ServerMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public class ServerProxyConfiguration implements IServerProxyConfiguration {

    private final ServerProxyRunnable serverRunnable;

    private int port = 13000;

    private  int sslPort = 13001;

    private List<AbstractResponseFilter> responseFilterList;

    private List<AbstractRequestFilter> requestFilterList;

    @Inject
    public ServerProxyConfiguration(ServerProxyRunnable serverRunnable){
        this.serverRunnable = serverRunnable;
        this.responseFilterList = new ArrayList<AbstractResponseFilter>();
        this.requestFilterList = new ArrayList<AbstractRequestFilter>();
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
    public IServerProxyConfiguration registerRequestFilter(AbstractRequestFilter... filters) {

        if (filters == null) {
            throw new IllegalArgumentException("Filters cannot be null.");
        }

        for (AbstractRequestFilter filter: filters) {
            this.requestFilterList.add(filter);
        }

        return this;
    }

    @Override
    public IServerProxyConfiguration registerResponseFilter(AbstractResponseFilter... filters) {

        if (filters == null) {
            throw new IllegalArgumentException("Filters cannot be null.");
        }

        for (AbstractResponseFilter filter: filters) {
            this.responseFilterList.add(filter);
        }

        return this;
    }

    @Override
    public IServerProxyCore start() {
        return this.serverRunnable.run(this);
    }

    public List<AbstractResponseFilter> getResponeFilter() {
        return this.responseFilterList;
    }

    public List<AbstractRequestFilter> getRequestFilter() {
        return this.requestFilterList;
    }

    public int getPort() {
        return this.port;
    }

    public int getSSLPort() {
        return this.sslPort;
    }
}
