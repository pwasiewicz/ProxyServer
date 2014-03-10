package skj.serverproxy.core.proxy.implementations;

import skj.serverproxy.core.proxy.IClientProxy;

import java.io.*;
import java.net.Socket;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public final class ClientProxy implements IClientProxy {
    @Override
    public void proxyConnection(Socket client) throws IOException {

        InputStreamReader clientInputReader = new InputStreamReader(client.getInputStream());
        OutputStreamWriter clientOutputWriter = new OutputStreamWriter(client.getOutputStream());
    }
}
