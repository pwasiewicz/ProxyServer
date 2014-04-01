package skj.serverproxy.core.filters.defaultFilters;

import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.implementations.base.HttpData;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
        this.acceptedContentTypes.add("application/javascript");
        this.acceptedContentTypes.add("text/javascript");
    }

    @Override
    public void filterRequest(HttpData httpData) {

         Properties headers = httpData.getHeaders();
        String contract = httpData.getContract();

         if (this.isAcceptedContentType(headers, contract)) {
             return;
         }

        try {
            this.forbiddenResponse(httpData, "Server settings do not allow to send content.");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public float getPriority() {
        return 1;
    }

     private boolean isAcceptedContentType(Properties header, String contract) {
         final String contentTypeKey = "content-type";

         if (contract.indexOf("OK") < 0) {
             return true;
         }

        if (!header.containsKey(contentTypeKey)) {
            return false;
        }

        String value = header.getProperty(contentTypeKey);
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
