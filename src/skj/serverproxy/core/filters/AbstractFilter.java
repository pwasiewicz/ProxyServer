package skj.serverproxy.core.filters;

import skj.serverproxy.core.implementations.base.HttpData;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public abstract class AbstractFilter {

    public abstract void filterRequest(HttpData httpData);

    public abstract float getPriority();
}
