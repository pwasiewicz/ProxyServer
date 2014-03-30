package skj.serverproxy.core.helpers;

import skj.serverproxy.core.exceptions.InvalidHeaderException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

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
        try{
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            byte[] savedBytes = new byte[1];
            boolean hasSavedBytes = false;
            CharsetDecoder decoder = charset.newDecoder();
            for (int n; (n = is.read(b)) != -1;) {

                if (hasSavedBytes) {
                    byte[] bTmp = new byte[savedBytes.length + b.length];
                    System.arraycopy(savedBytes, 0, bTmp, 0,
                            savedBytes.length);
                    System.arraycopy(b, 0, bTmp, savedBytes.length, b.length);
                    b = bTmp;
                    hasSavedBytes = false;
                    n = n + savedBytes.length;
                }

                CharBuffer charBuffer = decodeHelper(b, n, charset);
                if (charBuffer == null) {
                    int nrOfChars = 0;
                    while (charBuffer == null) {
                        nrOfChars++;
                        charBuffer = decodeHelper(b, n - nrOfChars, charset);
                        if (nrOfChars > 20 && nrOfChars < n) {
                            try {
                                charBuffer = decoder.decode(ByteBuffer.wrap(b,
                                        0, n));
                            } catch (MalformedInputException ex) {
                                throw new IOException(
                                        "File not in supported encoding (" +
                                                charset.displayName() + ")", ex);
                            }
                        }
                    }
                    savedBytes = new byte[nrOfChars];
                    hasSavedBytes = true;
                    for (int i = 0; i < nrOfChars; i++) {
                        savedBytes[i] = b[n - nrOfChars + i];
                    }
                }

                charBuffer.rewind(); // Bring the buffer's pointer to 0
                out.append(charBuffer.toString());
            }
            if (hasSavedBytes) {
                try {
                    CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(savedBytes, 0, savedBytes.length));
                    out.append(charBuffer.toString());
                } catch (MalformedInputException ex) {
                    throw new IOException(
                            "File not in supported encoding (" + charset.displayName() + ")",
                            ex);
                }
            }
            return out.toString();
        }
        finally{
            if(is != null){
                is.close();
            }
        }
    }


    private static CharBuffer decodeHelper(byte[] byteArray, int numberOfBytes, java.nio.charset.Charset charset) throws IOException {
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = null;
        try {
            charBuffer = decoder.decode(ByteBuffer.wrap(byteArray, 0,
                    numberOfBytes));
        } catch (MalformedInputException ex) {
            charBuffer = null;
        }
        return charBuffer;


    }
}
