package skj.serverproxy.core;

import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public interface IServerProxyCore {
    void resolveArgs(String... args) throws MissingArgumentException;
    void run();
}
