package skj.serverproxy.core.filters.defaultFilters;

import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.collections.HeaderValuesCollection;

import java.io.InputStream;

/**
 * Created by pwasiewicz on 18.03.14.
 */
public class TextResponseOnlyFilter extends AbstractResponseFilter {
    @Override
    public InputStream filterRequest(HeaderValuesCollection header, InputStream bodyRequest) {
        return null;
    }

    @Override
    public float getPriority() {
        return 1;
    }
}
