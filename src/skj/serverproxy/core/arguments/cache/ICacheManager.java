package skj.serverproxy.core.arguments.cache;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pwasiewicz on 15.03.14.
 */
public interface ICacheManager {

    boolean shouldCache();

    boolean isInCache(URL url);

    void writeCacheToStream(URL proxiedServerURL, OutputStream responseBody);
}
