package skj.serverproxy.core.implementations.base;

import skj.serverproxy.core.exceptions.InvalidHeaderException;
import skj.serverproxy.core.filters.AbstractFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public abstract class HttpConnectionProxyBase {

    // finals
    protected InputStream inputStream;

    protected Properties header;

    protected String contractLine;

    protected final List<? extends AbstractFilter> filters;

    protected HttpConnectionProxyBase(InputStream inputStream, List<? extends AbstractFilter> filters) {
        this.inputStream = inputStream;
        this.filters = filters;
    }


    public boolean parseHeaders() {
        return true;
    }

    public boolean proxyTo(OutputStream os) {
        this.writeHeaders(os);
        return this.writeBody(os);
    }

    public long getContentLength () {

        if (!this.header.containsKey("content-length")) {
            return 0;
        }

        return Long.parseLong(this.header.get("content-length").toString().trim());
    }

    public final void applyFilters() throws InvalidHeaderException {

        if (this.filters == null) {
            return;
        }

        final HttpData httpData = new HttpData(
                                this.contractLine,
                                this.header,
                                this.inputStream);

        for (AbstractFilter filter: this.filters) {
            filter.filterRequest(httpData);
        }

        this.overrideHeader(httpData.getContract(), httpData.getHeaders());
        this.overrideInputStream(httpData.getBody());
    }

    protected final void overrideInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    protected final void overrideHeader(String contractLine, Properties header) {
        this.contractLine = contractLine;
        this.header = header;
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

        writer.println(this.contractLine);

        Enumeration enums = this.header.propertyNames();
        while (enums.hasMoreElements()) {
            String key = (String) enums.nextElement();
            String value = this.header.getProperty(key);

            writer.println(key + ": " + value);
        }

        writer.println("");
        writer.flush();
    }
}
