package skj.serverproxy.core.implementations;


import com.sun.java.swing.plaf.windows.resources.windows;
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
        InputStream clientInput = socket.getInputStream();

        List<String> clientHeaders = new ArrayList<String>();

        String line;
        String targetUrl = null;

        boolean firstLine = true;

        while ((line = InputStreamHelper.readLine(clientInput)) != null) {

            if (line.length() <= 0) {
                break;
            }

            if (firstLine) {

                String[] tokens = line.split(" ");
                targetUrl = tokens[1];

                line = tokens[0] + " " + this.extractPath(tokens[1]) + " " + tokens[2];

                firstLine = false;
            }

            clientHeaders.add(line);
        }


        Socket server = new Socket(this.extractHostName(targetUrl), 80);
        PrintWriter serverPrint = new PrintWriter(server.getOutputStream());

        for (String header: clientHeaders) {
            serverPrint.println(header);
        }

        serverPrint.println("");
        serverPrint.flush();

        /* SERVER -> CLIENT */
        /* Scanner serverScanner = new Scanner(server.getInputStream());
        PrintWriter clientPrinter = new PrintWriter(socket.getOutputStream());

        List<String> serverHeaders = new ArrayList<String>();
        int serverContentLength = 0;

        while ((line = serverScanner.nextLine()) != null) {

            if (line.length() <= 0) {
                break;
            }

            serverHeaders.add(line);

            if (line.startsWith("Content-Length: ")) {
                // content-length
                int index = line.indexOf(':') + 1;
                String len = line.substring(index).trim();
                serverContentLength = Integer.parseInt(len);
            }
        }

        for (String header: serverHeaders) {
            clientPrinter.println(header);
        }

        clientPrinter.println("");
        clientPrinter.flush(); */

        // if (serverContentLength > 0) {

            InputStream serverReader = server.getInputStream();
            OutputStream clientWriter = socket.getOutputStream();

            byte[] buff = new byte[1024];
            int bytesRead;
            // int count = 0;

            while ((bytesRead = serverReader.read(buff)) != -1) {

                //if (count == serverContentLength) {
                    //break;
                // }

                clientWriter.write(buff, 0, bytesRead);
                clientWriter.flush();
                // count += bytesRead;
            }

            clientWriter.close();
            serverReader.close();
        // }

        clientInput.close();
    }

    private String extractHostName(String proxyServerUrl) throws URISyntaxException {

        URI uri = new URI(proxyServerUrl);

        return uri.getHost();
    }

    private String extractPath(String proxyServerUrl) throws URISyntaxException {

        URI uri = new URI(proxyServerUrl);

        return uri.getPath();
    }
}
