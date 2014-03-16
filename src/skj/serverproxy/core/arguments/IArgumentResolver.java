package skj.serverproxy.core.arguments;

import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import skj.serverproxy.core.models.*;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public interface IArgumentResolver {
    void resolve(String... args) throws MissingArgumentException;
    int getPort();
    int getSSLPort();
    ServerMode getMode();
}
