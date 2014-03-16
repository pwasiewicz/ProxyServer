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
import java.net.*;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyCore implements IServerProxyCore {

    private final ISocketHandler httpHandler;

    private ServerProxyConfiguration configuration;

    private Thread mainLoopThread;

    private ServerSocket mainServerSocket;

    @Inject
    public DefaultServerProxyCore(
                    ISocketHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public void run() throws IOException {

        if (!this.isConfigured()) {
            throw new RuntimeException("Server proxy is not configured.");
        }

        this.runServerLoop();
    }

    @Override
    public void stop() {
        if (!this.isConfigured()) {
            throw new  RuntimeException("Cannot stop not running server.");
        }

        this.mainLoopThread.interrupt();

        try {
            this.mainServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.mainLoopThread = null;
    }

    @Override
    public void setConfiguration(ServerProxyConfiguration serverProxyConfiguration) {

        if (this.isConfigured()) {
            throw new RuntimeException("Cannot configure server while running.");
        }

        this.configuration = serverProxyConfiguration;
    }

    private void runServerLoop() throws IOException {

        final ServerSocket server = procduceServerSocket();

        this.mainLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {

                        final Socket clientSocket = server.accept();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    httpHandler.handle(clientSocket);
                                } catch (IOException e) {
                                    // TODO: log
                                    e.printStackTrace();
                                } catch (URISyntaxException e) {
                                    // TODO: log
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } catch (SocketException e) {
                    // TODO: log;
                }
                catch (IOException e) {
                    // TODO: log
                    e.printStackTrace();
                }
            }
        });

        this.mainServerSocket = server;
        this.mainLoopThread.start();
    }

    private ServerSocket procduceServerSocket() throws IOException {
        return new ServerSocket(13000);

    }

    private boolean isConfigured() {
        return this.configuration != null;
    }

    private boolean isRunning() {
        return this.mainLoopThread != null;
    }
}
