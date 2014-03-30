package skj.serverproxy.core.filters;

import skj.serverproxy.core.implementations.base.HttpData;

import java.io.IOException;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public abstract class AbstractFilter {

    public abstract void filterRequest(HttpData httpData) throws IOException;

    public abstract float getPriority();
}
