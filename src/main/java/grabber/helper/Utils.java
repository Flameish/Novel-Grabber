package grabber.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;

public class Utils {

    public static void createDir(String filepath) {
        File dir = new File(filepath);
        if (!dir.exists()) dir.mkdirs();
    }

    public static byte[] downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0");
        conn.setRequestProperty("Accept", "image/*");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try(InputStream inputStream = conn.getInputStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream() {
                // toByteArray() normally creates a copy
                @Override
                public synchronized byte[] toByteArray() {
                    return this.buf;
                }
            };
            byte[] byteChunk = new byte[4096];
            int n;

            while ( (n = inputStream.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        }
    }

    /**
     * Clean HTML to proper xml/Epub HTML
     */
    public static String cleanHTMLString(String htmlString) {
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.syntax(Document.OutputSettings.Syntax.xml);
        settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        settings.charset("UTF-8");
        return Jsoup.clean(htmlString,
                "http://base.uri",
                Safelist.relaxed().preserveRelativeLinks(true),
                settings);
    }

    public static String getFilenameFromUrl(String url) throws URISyntaxException {
        return Paths.get(new URI(url).getPath()).getFileName().toString();
    }


    public static String getDomainName(String url)  {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if(domain != null) {
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
