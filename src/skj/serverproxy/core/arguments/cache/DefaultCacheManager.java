package skj.serverproxy.core.arguments.cache;

import skj.serverproxy.core.arguments.IArgumentResolver;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pwasiewicz on 15.03.14.
 */
public class DefaultCacheManager implements ICacheManager {

    private IArgumentResolver argumentResolver;

    public DefaultCacheManager(IArgumentResolver argumentResolver) {
        this.argumentResolver = argumentResolver;
    }


    @Override
    public boolean shouldCache() {
        return this.argumentResolver.shouldCache();
    }

    @Override
    public boolean isInCache(URL url) {
        return false;
    }

    @Override
    public void writeCacheToStream(URL proxiedServerURL, OutputStream responseBody) {

    }
}
