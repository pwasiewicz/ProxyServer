package skj.serverproxy.core.implementations;

import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.ISocketHandler;
import skj.serverproxy.core.arguments.IArgumentResolver;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyCore implements IServerProxyCore {

    private final ISocketHandler httpHandler;

    private ServerProxyConfiguration configuration;

    private boolean running;

    @Inject
    public DefaultServerProxyCore(
                    ISocketHandler httpHandler) {
        this.httpHandler = httpHandler;
        this.running = false;
    }

    @Override
    public void run() throws IOException {

        if (!this.isConfigured()) {
            throw new RuntimeException("Server proxy is not configured.");
        }

        this.running = true;
        this.runServerLoop();
    }

    @Override
    public void setConfiguration(ServerProxyConfiguration serverProxyConfiguration) {

        if (this.running) {
            throw new RuntimeException("Cannot configure server while running.");
        }

        this.configuration = serverProxyConfiguration;
    }

    private void runServerLoop() throws IOException {

        ServerSocket server = this.procduceServerSocket();

        while (this.running) {

            final Socket clientSocket = server.accept();

            new Thread(new Runnable() {
                @Override
               public void run() {
                    try{
                        httpHandler.handle(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private ServerSocket procduceServerSocket() throws IOException {
        return new ServerSocket(13000);

    }

    private boolean isConfigured() {
        return this.configuration != null;
    }
}
