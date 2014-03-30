import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import skj.serverproxy.core.DefaultServerProxyShell;
import skj.serverproxy.core.IServerProxyConfiguration;
import skj.serverproxy.core.IServerProxyCore;
import skj.serverproxy.core.filters.defaultFilters.TextResponseOnlyFilter;
import skj.serverproxy.core.filters.defaultFilters.WordMarkerFilter;

import java.io.File;
import java.io.IOException;

/**
 * Main server class
 * Created by pwasiewicz on 09.03.14.
 */
public class ServerProxy {

    @Option(name = "-p", aliases = { "--port" }, usage = "port on which proxy server will be running.", required = true)
    private int port = 13000;

    @Option(name = "-l", aliases = { "--light" }, required = false)
    private boolean lightMode = false;

    @Option(name = "-mw", aliases = { "--markword" }, usage = "path to file that holds words for mark in response.", required = false)
    private File wordMarkerFile = null;

    public static void main(String... args)  {
        new ServerProxy().doMain(args);
    }

    public void doMain(String... args) {
        System.out.println("Starting server...");

        CmdLineParser cmdParser = new CmdLineParser(this);

        try {
            cmdParser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();

            System.out.println("Cannot resolve arguments.");
            cmdParser.printUsage(System.out);

            return;
        }


        IServerProxyConfiguration configuration = DefaultServerProxyShell
                .initialize()
                .onPort(this.port);

        if (this.lightMode) {
            configuration.registerResponseFilter(new TextResponseOnlyFilter());
        }

        if (this.wordMarkerFile != null) {
            try {
                configuration.registerResponseFilter(new WordMarkerFilter(this.wordMarkerFile));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error while adding word marker filter: " + e.getMessage());
            }
        }

        IServerProxyCore server = configuration.start();

        System.out.println("Server started. Press any key to exit.");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while waiting for user input.");
        }

        server.stop();
    }
}
