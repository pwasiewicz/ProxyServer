package skj.serverproxy.core.implementations;


import com.sun.java.swing.plaf.windows.resources.windows;
import com.sun.jndi.toolkit.url.Uri;
import skj.serverproxy.core.ISocketHandler;
import skj.serverproxy.core.helpers.InputStreamHelper;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pwasiewicz on 14.03.14.
 */
public class ClientHandler implements ISocketHandler {
    @Override
    public void handle(Socket socket) throws IOException, URISyntaxException {

        /* CLIENT -> SERVER */
        final InputStream clientInput = socket.getInputStream();
        final OutputStream clientWriter = socket.getOutputStream();

        ProxyRequestHelper proxyRequest = new ProxyRequestHelper(clientInput);
        if (!proxyRequest.parseHeaders()) {
            return;
        }

        Socket server = new Socket(proxyRequest.getHost(), 80);

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

    private class ProxyRequestHelper {

        // finals
        private final InputStream inputStream;

        // conn metadata
        private List<String> rawHeaders;

        private long contentLength;

        private String host;

        public ProxyRequestHelper(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public boolean parseHeaders() {

            this.rawHeaders = new ArrayList<String>();
            this.contentLength = 0;

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

        public boolean proxyTo(OutputStream os) {
            this.writeHeaders(os);
            return this.writeBody(os);
        }

        public String getHost(){
            return this.host;
        }

        private boolean writeBody(OutputStream os) {

            if (this.contentLength == 0) {
                return true;
            }

            int buffer;
            long totalCount = 0;
            try {
                while ((buffer = inputStream.read()) != -1) {

                    totalCount += 1;
                    os.write(buffer);

                    if (totalCount % 1024 == 0){
                        os.flush();
                    }
                }
                os.flush();
                return  true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }


        private void writeHeaders(OutputStream os) {

            PrintWriter writer = new PrintWriter(os);

            for (String header: this.rawHeaders){
                writer.println(header);
            }

            writer.println("");
            writer.flush();
        }

        private void tryExtractContentLength(String bufferLine) {

            if (!bufferLine.startsWith("Content-Length:")) {
                return;
            }

            int index = bufferLine.indexOf(":");

            this.contentLength = Long.parseLong(bufferLine.substring(index + 1).trim());
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
