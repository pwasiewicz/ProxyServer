package skj.serverproxy.core.implementations;

import com.google.inject.Inject;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.arguments.IArgumentResolver;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import skj.serverproxy.core.proxy.IClientProxy;
import skj.serverproxy.core.proxy.IClientProxyFactory;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyCore implements IServerProxyCore {

    private final IArgumentResolver argsResolver;

    private final IClientProxyFactory clientThreadFactory;

    private boolean areArgsResolved = false;

    @Inject
    public DefaultServerProxyCore(
                    IArgumentResolver argsResolver,
                    IClientProxyFactory clientThreadFactory) {
        this.argsResolver = argsResolver;
        this.clientThreadFactory = clientThreadFactory;
    }

    @Override
    public void resolveArgs(String... args) throws MissingArgumentException {
        this.argsResolver.resolve(args);
        this.areArgsResolved = true;
    }

    @Override
    public void run() throws IOException {
        if (!this.areArgsResolved){
            throw new InvalidStateException("Arguments are not resolved.");
        }

        this.runServerLoop();
    }

    private void runServerLoop() throws IOException {

        ServerSocket serverSocket = this.produceServerSocket();

        while (true){
            final Socket clientSocket = serverSocket.accept();
            this.handleClient(clientSocket);
        }
    }

    private void handleClient(final Socket client) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                IClientProxy clientThread = clientThreadFactory.newClientProxy();

                try{
                    clientThread.proxyConnection(client);
                } catch (IOException e){
                    // TODO handle exception
                    e.printStackTrace();
                }
            }
        }).run();
    }

    private ServerSocket produceServerSocket() throws IOException {
        return new ServerSocket(this.argsResolver.getPort());
    }
}
