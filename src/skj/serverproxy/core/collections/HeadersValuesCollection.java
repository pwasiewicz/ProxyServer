package skj.serverproxy.core.collections;

import java.util.*;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public class HeadersValuesCollection {
    private HashMap<String, List<String>> headers;

    public HeadersValuesCollection() {
        this.headers = new HashMap<String, List<String>>();
    }

    public void put(String key, String value) {

        if (!this.headers.containsKey(key)) {
            this.headers.put(key, new ArrayList<String>());
        }

        this.headers.get(key).add(value);
    }

    public List<String> getValues(String key) {
        return this.headers.get(key);
    }

    public boolean containsKey(String key) {
        return this.headers.containsKey(key);
    }

    public void clear() {
        this.headers.clear();
    }

    public Set<String> keys() {
        return this.headers.keySet();
    }

    public void removeKey(String key) {
        this.headers.remove(key);
    }
}
