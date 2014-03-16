package skj.serverproxy.core.implementations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public abstract class RequestProxyBase {

    // finals
    protected final InputStream inputStream;

    protected List<String> rawHeaders;

    protected long contentLength;

    protected RequestProxyBase(InputStream inputStream) {
        this.inputStream = inputStream;
        this.contentLength = 0;
    }

    protected void tryExtractContentLength(String bufferLine) {

        if (!bufferLine.startsWith("Content-Length:")) {
            return;
        }

        int index = bufferLine.indexOf(":");

        this.contentLength = Long.parseLong(bufferLine.substring(index + 1).trim());
    }

    public boolean parseHeaders() {
        this.rawHeaders = new ArrayList<String>();
        this.contentLength = 0;

        return true;
    }

    public boolean proxyTo(OutputStream os) {
        this.writeHeaders(os);
        return this.writeBody(os);
    }

    protected boolean writeBody(OutputStream os) {

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

    protected void writeHeaders(OutputStream os) {

        PrintWriter writer = new PrintWriter(os);

        for (String header: this.rawHeaders){
            writer.println(header);
        }

        writer.println("");
        writer.flush();
    }
}
