package skj.serverproxy.core.filters.defaultFilters;

import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.collections.HeadersValuesCollection;
import skj.serverproxy.core.implementations.base.HttpData;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pwasiewicz on 18.03.14.
 */
public class TextResponseOnlyFilter extends AbstractResponseFilter {

    private final List<String> acceptedContentTypes;

    public TextResponseOnlyFilter() {

        this.acceptedContentTypes = new LinkedList<String>();
        this.acceptedContentTypes.add("text/html");
        this.acceptedContentTypes.add("text/plain");
        this.acceptedContentTypes.add("text/css");
        this.acceptedContentTypes.add("application/json");
        this.acceptedContentTypes.add("application/csv");
        this.acceptedContentTypes.add("application/xml");
    }

    @Override
    public void filterRequest(HttpData httpData) {

        if (this.isAcceptedContentType(httpData.getHeaders())) {
            return;
        }

        this.clearBody(httpData.getBody());
        
        this.makeServiceUnavailable(httpData);
    }

    private void makeServiceUnavailable(HttpData httpData) {
        httpData.setContract("HTTP/1.1 503 Service Temporarily Unavailable");
        httpData.setHeaders(new HeadersValuesCollection());
        httpData.setBody(new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        });
    }

    private void clearBody(InputStream bodyRequest) {
        try {
            bodyRequest.close();
        } catch (IOException e) {
            // clear and forget
        }
    }


    @Override
    public float getPriority() {
        return 1;
    }

    private boolean isAcceptedContentType(HeadersValuesCollection header) {
         final String contentTypeKey = "Content-Type";

        if (!header.containsKey(contentTypeKey)) {
            return false;
        }

        String value = header.getValues(contentTypeKey).get(0);
        String[] values = value.split(";");

        for (String chunk: values) {
            chunk = chunk.trim();

            for (String acceptedContentType: this.acceptedContentTypes) {
                if (acceptedContentType.compareToIgnoreCase(chunk) == 0) {
                    return true;
                }
            }

        }

        return false;
    }
}
