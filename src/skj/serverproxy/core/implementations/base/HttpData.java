package skj.serverproxy.core.implementations.base;

import skj.serverproxy.core.collections.HeadersValuesCollection;

import java.io.InputStream;

/**
 * Created by pwasiewicz on 18.03.14.
 */
public class HttpData {

    private HeadersValuesCollection headers;

    private InputStream body;

    private String contract;

    public HttpData(String contract, HeadersValuesCollection headers, InputStream body) {
        this.headers = headers;
        this.contract = contract;
        this.body = body;
    }

    public HeadersValuesCollection getHeaders() {
        return this.headers;
    }

    public void setHeaders(HeadersValuesCollection headers) {
        this.headers = headers;
    }

    public InputStream getBody() {
        return this.body;
    }

    public void setBody(InputStream body) {
         this.body = body;
    }

    public String getContract() {
        return this.contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}
