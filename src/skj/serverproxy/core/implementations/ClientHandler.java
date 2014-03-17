package skj.serverproxy.core.implementations;


import skj.serverproxy.core.AbstractRequestFilter;
import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.ISocketHandler;
import skj.serverproxy.core.filters.comparators.FilterPriorityComparator;
import skj.serverproxy.core.helpers.InputStreamHelper;
import skj.serverproxy.core.implementations.base.RequestProxyBase;
import skj.serverproxy.core.logger.NullLogger;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

        /* CLIENT -> SERVER */
        final InputStream clientInput = socket.getInputStream();
        final OutputStream clientWriter = socket.getOutputStream();

        this.writeInfo("Started parsing client stream.");

        ProxyRequestHelper proxyRequest = new ProxyRequestHelper(clientInput);
        if (!proxyRequest.parseHeaders()) {
            this.logger.severe(this.attachThreadId("Unable to parse client request."));
            // TODO: write bad request

            clientInput.close();
            clientWriter.close();
            return;
        }

        this.writeInfo(String.format("Parsed client request. Target: %s/%s", proxyRequest.getHost(), proxyRequest.getPath()));

        Socket server = new Socket(proxyRequest.getHost(), serverPort);

        final InputStream serverReader = server.getInputStream();
        final OutputStream serverWriter = server.getOutputStream();

        proxyRequest.proxyTo(serverWriter);

        ProxyServerResponse proxyServerResponse = new ProxyServerResponse(serverReader);

        if (!proxyServerResponse.parseHeaders()) {
            this.logger.severe(this.attachThreadId("Unable to parse server response."));

            clientInput.close();
            clientWriter.close();
            serverReader.close();
            serverWriter.close();

            return;
        }

        proxyServerResponse.proxyTo(clientWriter);

        clientInput.close();
        clientWriter.close();
        serverReader.close();
        serverWriter.close();
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
        return String.format("Thread %l: %s", Thread.currentThread().getId(), msg);
    }

    private class ProxyServerResponse extends RequestProxyBase {

        public ProxyServerResponse(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public boolean parseHeaders() {

            if(!super.parseHeaders()) {
                return false;
            }

            String bufferLine;

            try {
                while ((bufferLine = InputStreamHelper.readLine(this.inputStream)) != null) {

                    if (bufferLine.length() == 0) {
                        break;
                    }

                    this.rawHeaders.add(bufferLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    private class ProxyRequestHelper extends RequestProxyBase {

        private String host;

        private String path;

        public ProxyRequestHelper(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public boolean parseHeaders() {

            if(!super.parseHeaders()) {
                return false;
            }

            try{
                String contractLine = InputStreamHelper.readLine(this.inputStream);
                if (contractLine.length() == 0) {
                    return false;
                }

                contractLine = this.handleContractLine(contractLine);
                if (contractLine == null) {
                    return false;
                }

                this.rawHeaders.add(contractLine);

                String bufferLine;
                while ((bufferLine = InputStreamHelper.readLine(this.inputStream)) != null) {
                    if (bufferLine.length() == 0) {
                        break;
                    }

                    this.rawHeaders.add(bufferLine);
                }

                return true;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }

        }

        public String getHost(){
            return this.host;
        }

        public String getPath() { return this.path; }

        private String handleContractLine(String contractLine) {

            String[] tokens = contractLine.split(" ");

            if (tokens.length != 3) {
                return null;
            }

            URI uri = URI.create(tokens[1]);
            this.host = uri.getHost();

            String path = uri.getPath();
            this.path = path;

            return tokens[0] + " " + path + " " + tokens[2];
        }
    }
}
