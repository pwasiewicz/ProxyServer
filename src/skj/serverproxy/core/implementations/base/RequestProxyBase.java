package skj.serverproxy.core.implementations.base;

import skj.serverproxy.core.collections.HeaderValuesCollection;
import skj.serverproxy.core.exceptions.InvalidHeaderException;

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

    protected RequestProxyBase(InputStream inputStream) {
        this.inputStream = inputStream;
    }


    public boolean parseHeaders() {
        this.rawHeaders = new ArrayList<String>();

        return true;
    }

    public boolean proxyTo(OutputStream os) {
        this.writeHeaders(os);
        return this.writeBody(os);
    }

    public long getContentLength () {

        try {
            HeaderValuesCollection headers = this.getHeaders();
            if (!headers.containsKey("Content-Length")) {
                return 0;
            }

            String value = headers.getValues("Content-Length").get(0);

            return Long.parseLong(value);

        } catch (InvalidHeaderException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getContentType() {
        try {
            HeaderValuesCollection headers = this.getHeaders();
            if (!headers.containsKey("Content-Type")) {
                return null;
            }

            String value = headers.getValues("Content-Type").get(0);

            return value.trim();

        } catch (InvalidHeaderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HeaderValuesCollection getHeaders() throws InvalidHeaderException {
        HeaderValuesCollection output = new HeaderValuesCollection();

        for (String header: this.rawHeaders) {
            String key = this.getHeaderKey(header);
            if (key == null) {
                throw new InvalidHeaderException();
            }

            String value = this.getHeaderValue(key, header);
            output.put(key, value.trim());
        }

        return output;
    }

    public final void overrideHeaders(HeaderValuesCollection newHeaders) {

        if (newHeaders == null) {
            throw new IllegalArgumentException("New headers cannot be null.");
        }

        this.rawHeaders = new ArrayList<String>();

        for (String key: newHeaders.keys()) {
            List<String> values = newHeaders.getValues(key);

            for (String value: values) {
                this.rawHeaders.add(String.format("%s: %s", key, value));
            }
        }
    }

    protected boolean writeBody(OutputStream os) {

        if (this.getContentLength() == 0) {
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


    private String getHeaderValue(String key, String bufferLine) {
        if (!bufferLine.startsWith(key + ":")) {
            return null;
        }

        int index = bufferLine.indexOf(":");

        return bufferLine.substring(index + 1);
    }

    private String getHeaderKey(String rawHeader) {

        assert rawHeader != null;
        assert rawHeader.length() > 0;

        int index = rawHeader.indexOf(":");
        if (index < 1) {
            return null;
        }

        return rawHeader.substring(0, index -1);
    }
}
