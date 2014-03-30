package skj.serverproxy.core.implementations;


import skj.serverproxy.core.AbstractRequestFilter;
import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.ISocketHandler;
import skj.serverproxy.core.exceptions.InvalidHeaderException;
import skj.serverproxy.core.filters.comparators.FilterPriorityComparator;
import skj.serverproxy.core.helpers.InputStreamHelper;
import skj.serverproxy.core.implementations.base.HttpConnectionProxyBase;
import skj.serverproxy.core.logger.NullLogger;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by pwasiewicz on 14.03.14.
 */
public class ClientHandler implements ISocketHandler {

    private static final int serverPort = 80;

    public Logger logger;

    private List<AbstractResponseFilter> responseFilters;

    private List<AbstractRequestFilter> requestFilters;

    public ClientHandler() {
        this.logger = NullLogger.instance();
    }

    @Override
    public void handle(Socket socket) throws IOException, URISyntaxException {

        final InputStream clientInput = socket.getInputStream();
        final OutputStream clientWriter = socket.getOutputStream();

        this.writeInfo("Started parsing client stream.");

        ProxyRequestHelper proxyRequest = new ProxyRequestHelper(clientInput, this.requestFilters);
        if (!proxyRequest.parseHeaders()) {
            this.logger.severe(this.attachThreadId("Unable to parse client request."));
            // TODO: write bad request

            clientInput.close();
            clientWriter.close();
            return;
        }

        this.writeInfo(String.format("Parsed client request. Target: %s", proxyRequest.getHost()));


        try {
            proxyRequest.applyFilters();
        } catch (InvalidHeaderException e) {
            logger.severe(this.attachThreadId("Error while applying filters for request: " + e.getMessage()));
            e.printStackTrace();
            // TODO: internal server error

            clientInput.close();
            clientWriter.close();

            return;
        }

        Socket server = new Socket(proxyRequest.getHost(), serverPort);

        final InputStream serverReader = server.getInputStream();
        final OutputStream serverWriter = server.getOutputStream();

        if (!proxyRequest.proxyTo(serverWriter)) {
            this.logger.severe(this.attachThreadId("Unable to send request to target server."));

            clientInput.close();
            clientWriter.close();
            serverReader.close();
            serverWriter.close();

            return;
        }

        ProxyServerResponseHelper proxyServerResponse = new ProxyServerResponseHelper(serverReader, this.responseFilters);

        this.writeInfo("Parsing server response.");

        if (!proxyServerResponse.parseHeaders()) {
            this.logger.severe(this.attachThreadId("Unable to parse server response."));

            clientInput.close();
            clientWriter.close();
            serverReader.close();
            serverWriter.close();

            return;
        }

        try {
            proxyServerResponse.applyFilters();
        } catch (InvalidHeaderException e) {
            this.logger.severe(this.attachThreadId("Error while applying filters to server response: " + e.getMessage()));
            e.printStackTrace();

            // TODO internal server error
            return;
        }

        if (!proxyServerResponse.proxyTo(clientWriter)) {
            this.logger.severe(this.attachThreadId("Error while passing response to client."));
        } else {
            this.writeInfo("Successfully handled client request. Closing streams.");
        }

        clientInput.close();
        clientWriter.close();
        serverReader.close();
        serverWriter.close();

        this.writeInfo("Streams closed successfully.");
    }

    @Override
    public synchronized void setResponseFilters(List<AbstractResponseFilter> filters) {
        this.responseFilters = new ArrayList<AbstractResponseFilter>();

        for (AbstractResponseFilter filter: filters) {
            this.responseFilters.add(filter);
        }

        Collections.sort(this.responseFilters, new FilterPriorityComparator());
    }

    @Override
    public synchronized void setRequestFilters(List<AbstractRequestFilter> filters) {
        this.requestFilters = new ArrayList<AbstractRequestFilter>();

        for (AbstractRequestFilter filter: filters) {
            this.requestFilters.add(filter);
        }

        Collections.sort(this.requestFilters, new FilterPriorityComparator());
    }

    private void writeInfo(String message) {
        this.logger.info(this.attachThreadId(message));
    }

    private String attachThreadId(String msg) {
        return String.format("Thread %d: %s", Thread.currentThread().getId(), msg);
    }

    private class ProxyServerResponseHelper extends HttpConnectionProxyBase {

        public ProxyServerResponseHelper(InputStream inputStream, List<AbstractResponseFilter> filters) {
            super(inputStream, filters);
        }

        @Override
        public boolean parseHeaders() {

            if(!super.parseHeaders()) {
                return false;
            }

            try {
                this.contractLine = InputStreamHelper.readLine(this.inputStream);

                this.header = new Properties();
                String line = InputStreamHelper.readLine(this.inputStream);
                while (line.trim().length() > 0) {
                    int p = line.indexOf(':');
                    header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                    line = InputStreamHelper.readLine(this.inputStream);
                }

                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (InvalidHeaderException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private class ProxyRequestHelper extends HttpConnectionProxyBase {

        public ProxyRequestHelper(InputStream inputStream, List<AbstractRequestFilter> filters) {
            super(inputStream, filters);
        }

        @Override
        public boolean parseHeaders() {

            if(!super.parseHeaders()) {
                return false;
            }

            try{
                this.contractLine = InputStreamHelper.readLine(this.inputStream);
                if (contractLine.length() == 0) {
                    return false;
                }

                this.header = new Properties();
                String line = InputStreamHelper.readLine(this.inputStream);
                while (line.trim().length() > 0) {
                    int p = line.indexOf(':');
                    header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                    line = InputStreamHelper.readLine(this.inputStream);
                }

                return true;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } catch (InvalidHeaderException e) {
                e.printStackTrace();
                return false;
            }

        }

        public String getHost(){
            return this.header.getProperty("host");
        }
    }
}
