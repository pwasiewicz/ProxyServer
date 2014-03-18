package skj.serverproxy.core.implementations.base;

import skj.serverproxy.core.collections.HeadersValuesCollection;
import skj.serverproxy.core.exceptions.InvalidHeaderException;
import skj.serverproxy.core.filters.AbstractFilter;

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
    protected InputStream inputStream;

    protected final List<? extends AbstractFilter> filters;

    protected List<String> rawHeaders;

    protected RequestProxyBase(InputStream inputStream, List<? extends AbstractFilter> filters) {
        this.inputStream = inputStream;
        this.filters = filters;
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
            HeadersValuesCollection headers = this.getHeaders();
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

    public HeadersValuesCollection getHeaders() throws InvalidHeaderException {
        HeadersValuesCollection output = new HeadersValuesCollection();

        if (this.rawHeaders.size() < 2) {
            return output;
        }

        List<String> onlyHeaders = this.rawHeaders.subList(1, this.rawHeaders.size());

        for (String header: onlyHeaders) {
            String key = this.getHeaderKey(header);
            if (key == null) {
                throw new InvalidHeaderException();
            }

            String value = this.getHeaderValue(key, header);
            if (value == null) {
                continue;
            }

            output.put(key, value.trim());
        }

        return output;
    }

    public final void applyFilters() throws InvalidHeaderException {

        if (this.filters == null) {
            return;
        }

        final HttpData httpData = new HttpData(
                                this.getContract(),
                                this.getHeaders(),
                                this.inputStream);

        for (AbstractFilter filter: this.filters) {
            filter.filterRequest(httpData);
        }

        this.overrideHeaders(httpData.getContract(), httpData.getHeaders());
        this.overrideInputStream(httpData.getBody());
    }

    private String getContract() {

        if (this.rawHeaders.size() < 1) {
            return null;
        }

        return this.rawHeaders.get(0);
    }

    protected final void overrideInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    protected final void overrideHeaders(String contract, HeadersValuesCollection newHeaders) {

        if (newHeaders == null) {
            throw new IllegalArgumentException("New headers cannot be null.");
        }

        if (contract == null) {
            throw new IllegalArgumentException("New contract cannot be null.");
        }

        this.rawHeaders = new ArrayList<String>();
        this.rawHeaders.add(contract);

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
        if (index < 0) {
            return null;
        }

        return rawHeader.substring(0, index);
    }
}
