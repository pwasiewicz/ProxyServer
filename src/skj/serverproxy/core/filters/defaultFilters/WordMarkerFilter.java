package skj.serverproxy.core.filters.defaultFilters;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import skj.serverproxy.core.AbstractResponseFilter;
import skj.serverproxy.core.helpers.InputStreamHelper;
import skj.serverproxy.core.implementations.base.HttpData;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Filter that highlights specified words in response html.
 */
public class WordMarkerFilter extends AbstractResponseFilter {

    private final int MissingContentLengthStub = -1;

    private final int MaxSupportedContentLength = 1024 * 512;

    final String ContentTypeKey = "content-type";

    final String ContentLengthKey = "content-length";

    private final Collection<String> wordsToMark;

    private final Collection<String> acceptedContentTypes;

    public WordMarkerFilter(File wordMarkerFile) throws IOException {

        this.wordsToMark = new LinkedList<String>();
        this.acceptedContentTypes = new LinkedList<String>();
        this.acceptedContentTypes.add("text/html");

        if (wordMarkerFile == null) {
            return;
        }

        if (!wordMarkerFile.exists() || !wordMarkerFile.canRead()) {
            throw new FileNotFoundException("Cannot read from file to get words to mark.");
        }

        BufferedReader reader = new BufferedReader(new FileReader(wordMarkerFile.getAbsolutePath()));
        String line;
        while ((line= reader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                this.wordsToMark.add(tokenizer.nextToken());
            }
        }

        reader.close();
    }

    @Override
    public void filterRequest(HttpData httpData) throws IOException {

        Properties header = httpData.getHeaders();
        if (!this.isAcceptedContentType(header)) {
            return;
        }

        int contentLength = this.getContentLength(header);
        if (contentLength == MissingContentLengthStub
                || contentLength > MaxSupportedContentLength) {
            return;
        }

        Charset contentCharset = this.getContentCharset(header);
        if (contentCharset == null) {
            return;
        }

        Document htmlDocument = this.parseHtml(contentLength, contentCharset, httpData);

        // modify doc
        // end modifying

        String newHtml = htmlDocument.toString();
        byte[] newBytes = newHtml.getBytes(contentCharset);

        this.changeContentLength(newBytes.length, header);
        InputStream inputStream = new ByteInputStream(newBytes, newBytes.length);

        httpData.setBody(inputStream);
   }

    private void changeContentLength(int newLength, Properties header) {
        header.remove(ContentLengthKey);
        header.put(ContentLengthKey, newLength);
    }

    private Document parseHtml(int contentLength, Charset charset, HttpData httpData) throws IOException {

        InputStream stream = httpData.getBody();

        String html = InputStreamHelper.inputStream2String(stream, charset, contentLength);

        return Jsoup.parse(html);
    }

    private Charset getContentCharset(Properties header) {

        if (!header.containsKey(ContentTypeKey)) {
            return null;
        }

        String value = header.getProperty(ContentTypeKey);
        String[] values = value.split(";");

        for (String chunk: values){
            String token = chunk.trim();
            if (!token.startsWith("charset=")) {
                continue;
            }

            StringTokenizer stringTokenizer = new StringTokenizer(token, "=");

            stringTokenizer.nextToken();
            if (!stringTokenizer.hasMoreTokens()) {
                return null;
            }

            String charset = stringTokenizer.nextToken().trim();
            return Charset.forName(charset);
        }

        return null;
    }

    private int getContentLength(Properties header) {

        if (!header.containsKey(ContentLengthKey)){
            return  MissingContentLengthStub;
        }

        String value = header.getProperty(ContentLengthKey).trim();
        if (value.length() <= 0) {
            return MissingContentLengthStub;
        }

        return Integer.parseInt(value);
    }

    @Override
    public float getPriority() {
        return 0.9f;
    }

    private boolean isAcceptedContentType(Properties header) {
       if (!header.containsKey(ContentTypeKey)) {
            return false;
        }

        String value = header.getProperty(ContentTypeKey);
        String[] values = value.split(";");

        for (String chunk: values) {
            chunk = chunk.trim();

            for (String acceptedContentType: this.acceptedContentTypes) {
                if (acceptedContentType.compareToIgnoreCase(chunk) == 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
