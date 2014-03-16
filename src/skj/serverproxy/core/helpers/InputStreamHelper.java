package skj.serverproxy.core.helpers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public class InputStreamHelper {

    private static final int CR = 13;
    private static final int LF = 10;

    public static String readLine(InputStream stream) throws IOException {

        int ch;   // currently read char

        StringBuffer sb = new StringBuffer("");

        ch = stream.read();
        while(ch != CR && ch != LF)
        {
            sb.append((char) ch);
            ch = stream.read();
        }

        if (ch == CR) {
            stream.read();
        }
        return(new String(sb));

    }

}
