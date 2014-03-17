package skj.serverproxy.core.filters;

import skj.serverproxy.core.collections.HeaderValuesCollection;

import java.io.InputStream;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public abstract class AbstractFilter {

    public abstract InputStream filterRequest(HeaderValuesCollection header, final InputStream bodyRequest);

    public abstract float getPriority();
}
