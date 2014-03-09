package skj.serverproxy.core.proxy;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public interface IClientProxy {
    void proxyConnection(final Socket client) throws IOException;
}
