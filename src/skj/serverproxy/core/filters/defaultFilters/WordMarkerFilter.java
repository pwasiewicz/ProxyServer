package skj.serverproxy.core.filters.defaultFilters;

import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.implementations.base.HttpData;

/**
 * Created by pwasiewicz on 26.03.14.
 */
public class WordMarkerFilter extends AbstractResponseFilter {
    @Override
    public void filterRequest(HttpData httpData) {

    }

    @Override
    public float getPriority() {
        return 0.9f;
    }
}
