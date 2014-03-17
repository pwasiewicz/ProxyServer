package skj.serverproxy.core.filters.comparators;

import skj.serverproxy.core.filters.AbstractFilter;

import java.util.Comparator;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public class FilterPriorityComparator implements Comparator<AbstractFilter> {
    @Override
    public int compare(AbstractFilter o1, AbstractFilter o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }

        if (o1 != null && o2 == null) {
            return  1;
        }

        if (o1 == null && o2 != null) {
            return  -1;
        }

        if (o1.getPriority() == o2.getPriority()) {
            return 0;
        }

        if (o1.getPriority() < o2.getPriority()) {
            return 1;
        }

        return -1;
    }
}
