package skj.serverproxy.core.proxy.implementations;

import skj.serverproxy.core.proxy.IClientProxy;
import skj.serverproxy.core.proxy.IClientProxyFactory;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultClientProxyFactory implements IClientProxyFactory {
    @Override
    public IClientProxy newClientProxy() {
        return new ClientProxy();
    }
}
