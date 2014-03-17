package skj.serverproxy.core.logger;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by pwasiewicz on 17.03.14.
 */
public class NullLogger extends Logger {

    public static Logger instance(){
        return new NullLogger(null, null);
    }

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p/>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    protected NullLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);

    }

    @Override
    public void log(LogRecord record) {
        // Nothing to do in Null Logger
    }
}
