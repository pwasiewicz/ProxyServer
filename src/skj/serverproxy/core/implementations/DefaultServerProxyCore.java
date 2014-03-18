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
import skj.serverproxy.core.logger.NullLogger;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyCore implements IServerProxyCore {

    private final ISocketHandler httpHandler;

    private ServerProxyConfiguration configuration;

    private Thread mainLoopThread;

    private ServerSocket mainServerSocket;

    public Logger logger;

    @Inject
    public DefaultServerProxyCore(
                    ISocketHandler httpHandler) {
        this.httpHandler = httpHandler;
        this.logger = NullLogger.instance();
    }

    @Override
    public void run() throws IOException {

        this.logger.info("Running proxy server...");

        if (!this.isConfigured()) {
            this.logger.severe("Server not configured.");
            throw new RuntimeException("Server proxy is not configured.");
        }

        this.runServerLoop();
        this.logger.info("Proxy server run properly.");
    }

    @Override
    public void stop() {
        if (!this.isRunning()) {
            this.logger.severe("Cannot stop server that is not running.");
            throw new  RuntimeException("Cannot stop not running server.");
        }

        this.mainLoopThread.interrupt();

        try {
            this.mainServerSocket.close();
        } catch (IOException e) {
            this.logger.severe("An error occurred while stopping server: " + e.getMessage());
        }

        this.mainLoopThread = null;
    }

    @Override
    public void setConfiguration(ServerProxyConfiguration serverProxyConfiguration) {

        if (this.isRunning()) {
            this.logger.severe("Trying to set server configuration while running.");
            throw new RuntimeException("Cannot configure server while running.");
        }

        this.configuration = serverProxyConfiguration;

        this.httpHandler.setRequestFilters(this.configuration.getRequestFilter());
        this.httpHandler.setResponseFilters(this.configuration.getResponeFilter());
    }

    @Override
    public boolean isRunning() {
        return this.mainLoopThread != null;
    }

    private void runServerLoop() throws IOException {

        final ServerSocket server = procduceServerSocket();

        this.mainLoopThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {

                        logger.info("Waiting for request.");
                        final Socket clientSocket = server.accept();
                        logger.info("Received request.");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    logger.info("Handling request in thread " + Thread.currentThread().getId());
                                    httpHandler.handle(clientSocket);
                                    logger.info("Exiting client's thread.");
                                } catch (IOException e) {
                                    logger.severe(String.format("Thread %d: Error while handling client's request: %s",
                                                                    Thread.currentThread().getId(),
                                                                    e.getMessage()));
                                    e.printStackTrace();
                                } catch (URISyntaxException e) {
                                    logger.severe("Error while handling client's request: Bad target url.");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } catch (SocketException e) {
                    logger.severe("Error while waiting for client's request: " + e.getMessage());
                    e.printStackTrace();
                }
                catch (IOException e) {
                    logger.severe("Error while reading client's request data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        this.logger.info("Running main server loop.");

        this.mainServerSocket = server;
        this.mainLoopThread.start();

        this.logger.info("Main server loop run.");
    }

    private ServerSocket procduceServerSocket() throws IOException {
        return new ServerSocket(this.configuration.getPort());

    }

    private boolean isConfigured() {
        return this.configuration != null;
    }
}
