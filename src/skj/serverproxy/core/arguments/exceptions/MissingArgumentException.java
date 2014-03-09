package skj.serverproxy.core.arguments.exceptions;

/**
 * Created by pwasiewicz on 09.03.14.
 */
public class MissingArgumentException extends Exception {
    public MissingArgumentException(String argName){
        super("Required argument " + argName + " is missing. Check program parameters.");
    }
}
