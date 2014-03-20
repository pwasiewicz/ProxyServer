package skj.serverproxy.core.implementations.base;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by pwasiewicz on 18.03.14.
 */
public class HttpData {

    private Properties header;

    private InputStream body;

    private String contract;

    public HttpData(String contract, Properties header, InputStream body) {
        this.header = header;
        this.contract = contract;
        this.body = body;
    }

    public Properties getHeaders() {
        return this.header;
    }

    public void setHeaders(Properties headers) {
        this.header = headers;
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
