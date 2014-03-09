package skj.serverproxy.core.implementations;

import com.google.inject.Inject;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.arguments.IArgumentResolver;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class DefaultServerProxyCore implements IServerProxyCore {

    private final IArgumentResolver argsResolver;

    @Inject
    public DefaultServerProxyCore(IArgumentResolver argsResolver) {
        this.argsResolver = argsResolver;
    }

    @Override
    public void resolveArgs(String... args) throws MissingArgumentException {
        this.argsResolver.resolve(args);
    }

    @Override
    public void run() {

    }
}
