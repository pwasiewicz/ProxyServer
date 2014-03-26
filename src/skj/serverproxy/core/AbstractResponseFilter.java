package skj.serverproxy.core;

import com.sun.istack.internal.NotNull;
import skj.serverproxy.core.filters.AbstractFilter;
import skj.serverproxy.core.implementations.base.HttpData;

import java.io.*;
import java.util.Properties;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public abstract class AbstractResponseFilter extends AbstractFilter {

    protected void forbiddenResponse(HttpData httpData, @NotNull String casue) throws UnsupportedEncodingException {

        if (casue == null || casue.length() == 0) {
            throw new IllegalArgumentException();
        }

        httpData.setContract("HTTP/1.1 403 Forbidden");

        Properties headers = httpData.getHeaders();
        headers.clear();
        headers.put("content-type", "text/html; charset=utf-8");
        headers.put("content-lenght", casue.length());
        httpData.setHeaders(headers);

        InputStream stream = new ByteArrayInputStream(casue.getBytes("UTF-8"));
        httpData.setBody(stream);
    }

}
