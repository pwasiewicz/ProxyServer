package skj.serverproxy.core.arguments;

import com.sun.corba.se.spi.activation.Server;
import skj.serverproxy.core.arguments.exceptions.MissingArgumentException;
import skj.serverproxy.core.models.ServerMode;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class ArgumentResolver implements IArgumentResolver {

    private int port;

    private String wordsFilePath;

    private boolean cache;

    private ServerMode mode;

    @Override
    public void resolve(String... args) throws MissingArgumentException {

        if (args.length < 2){
            throw new MissingArgumentException("filePath");
        }

        this.port = Integer.parseInt(args[0]);
        this.wordsFilePath = args[1];
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getWordsFilePath() {
        return this.wordsFilePath;
    }

    @Override
    public boolean shouldChache() {
        return this.cache;
    }

    @Override
    public ServerMode getMode() {
        return this.mode;
    }
}
