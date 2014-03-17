package skj.serverproxy.core;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

/**
 * Created by pwasiewicz on 15.03.14.
 */
public interface ISocketHandler {
    void handle(Socket socket) throws IOException, URISyntaxException;

    void setResponseFilters(List<AbstractResponseFilter> filters);
    void setRequestFilters(List<AbstractRequestFilter> filters);
}
