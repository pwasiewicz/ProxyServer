package skj.serverproxy.core.implementations;


import com.sun.java.swing.plaf.windows.resources.windows;
import com.sun.jndi.toolkit.url.Uri;
import skj.serverproxy.core.ISocketHandler;
import skj.serverproxy.core.helpers.InputStreamHelper;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pwasiewicz on 14.03.14.
 */
public class ClientHandler implements ISocketHandler {

    private static final int serverPort = 80;

    @Override
    public void handle(Socket socket) throws IOException, URISyntaxException {

        /* CLIENT -> SERVER */
        final InputStream clientInput = socket.getInputStream();
        final OutputStream clientWriter = socket.getOutputStream();

        ProxyRequestHelper proxyRequest = new ProxyRequestHelper(clientInput);
        if (!proxyRequest.parseHeaders()) {
            return;
        }

        Socket server = new Socket(proxyRequest.getHost(), serverPort);

        final InputStream serverReader = server.getInputStream();
        final OutputStream serverWriter = server.getOutputStream();

        proxyRequest.proxyTo(serverWriter);

        byte[] buff = new byte[4096];
        int bytesRead;

        while ((bytesRead = serverReader.read(buff)) != -1) {
            clientWriter.write(buff, 0, bytesRead);
            clientWriter.flush();
        }

        clientInput.close();
        clientWriter.close();
        serverReader.close();
        serverWriter.close();
    }

    private class ProxyServerResponse extends RequestProxyBase {

        private Charset responseCharset;

        public ProxyServerResponse(InputStream inputStream) {
            super(inputStream);
        }
    }

    private class ProxyRequestHelper extends RequestProxyBase {

        private String host;

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

                    this.tryExtractContentLength(bufferLine);
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

        private String handleContractLine(String contractLine) {

            String[] tokens = contractLine.split(" ");

            if (tokens.length != 3) {
                return null;
            }

            URI uri = URI.create(tokens[1]);
            this.host = uri.getHost();

            String path = uri.getPath();


            return tokens[0] + " " + path + " " + tokens[2];
        }
    }
}
