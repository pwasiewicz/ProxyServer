package skj.serverproxy.core.arguments;

import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import skj.serverproxy.core.models.ServerMode;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class ArgumentResolver implements IArgumentResolver {

    private final String LIGHT_MODE_ARG = "--LIGHT";

    private final ServerMode DEFAULT_SERVER_MODE = ServerMode.HEAVY;

    private int port;

    private int sslPort;

    private ServerMode mode;

    @Override
    public void resolve(String... args) throws MissingArgumentException {

        if (args == null){
            throw new IllegalArgumentException("Program args cannot be null.");
        }

        if (args.length < 3){
            throw new MissingArgumentException("At least two arguments must be provided: port and ssl port");
        }

        this.port = Integer.parseInt(args[0]);
        this.sslPort = Integer.parseInt(args[1]);
        this.mode = DEFAULT_SERVER_MODE;

        this.readServerMode(args);
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public int getSSLPort() {
        return this.sslPort;
    }


    @Override
    public ServerMode getMode() {
        return this.mode;
    }

    private void readServerMode(String[] args) {
        if (this.contains(args, LIGHT_MODE_ARG)){
            this.mode = ServerMode.LIGHT;
        }
    }

    private boolean contains(String[] args, String element){
        for (String arrayElement : args){
            if (arrayElement == element){
                return true;
            }
        }

        return false;
    }
}
