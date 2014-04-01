package skj.serverproxy.core.helpers;

import skj.serverproxy.core.exceptions.InvalidHeaderException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import java.util.Scanner;

/**
 * Created by pwasiewicz on 16.03.14.
 */
public class InputStreamHelper {

    private static final int CR = 13;
    private static final int LF = 10;

    private static final int MAX_HEADER_LENGTH = 16*1024;

    public static String readLine(InputStream stream) throws IOException, InvalidHeaderException {

        int ch;   // currently read char

        StringBuffer sb = new StringBuffer("");

        ch = stream.read();
        while(ch != CR && ch != LF)
        {
            if (sb.length() >= MAX_HEADER_LENGTH) {
                System.out.println("Too big header: " + sb.toString());
                throw new InvalidHeaderException();
            }


            sb.append((char) ch);
            ch = stream.read();
        }

        if (ch == CR) {
            stream.read();
        }
        return(new String(sb));
    }

    public static String inputStream2String(final InputStream is,
                                            final Charset charset,
                                            final int maxBytes) throws IOException {
        Scanner s = new java.util.Scanner(is, charset.name()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
