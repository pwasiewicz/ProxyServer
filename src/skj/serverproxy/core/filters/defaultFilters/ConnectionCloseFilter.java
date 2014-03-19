package skj.serverproxy.core.filters.defaultFilters;

import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.collections.HeadersValuesCollection;
import skj.serverproxy.core.implementations.base.HttpData;

/**
 * Created by pwasiewicz on 19.03.14.
 */
public class ConnectionCloseFilter extends AbstractResponseFilter {
    @Override
    public void filterRequest(HttpData httpData) {

        HeadersValuesCollection headers = httpData.getHeaders();

        if (headers.containsKey("connection")) {
            headers.removeKey("connection");
        }

        headers.put("connection", "Close");

        httpData.setHeaders(headers);
    }

    @Override
    public float getPriority() {
        return 1.1f;
    }
}
