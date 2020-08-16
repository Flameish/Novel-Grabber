package grabber;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import system.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

/**
 * Collection of shared utility methods
 */
public class GrabberUtils {
    static void createDir(String filepath) {
        File dir = new File(filepath);
        if (!dir.exists()) dir.mkdirs();
    }

    public static String getFilenameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String urlPath = url.getPath();
            return urlPath.substring(urlPath.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage getImage(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
        return ImageIO.read(connection.getInputStream());
    }

    static int getWordCount(String html) {
        Document dom = Jsoup.parse(html);
        String text = dom.text();
        return text.split(" ").length;
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public static String getFileExtension(String filename) {
        String fileName = new File(filename).getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    public static void sleep(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    static String getFileName(String imageName) {
        if (imageName != null && imageName.contains("/"))
            imageName = imageName.substring(imageName.lastIndexOf("/") + 1);
        if (imageName != null && imageName.contains("\\"))
            imageName = imageName.substring(imageName.lastIndexOf("\\") + 1);
        return imageName;
    }

    public static String getDomain(String url) {
        for(Object key: Config.getInstance().siteSelectorsJSON.keySet()) {
            Object keyvalue = Config.getInstance().siteSelectorsJSON.get(key);
            String keyUrl = ((JSONObject) keyvalue).get("url").toString();
            if(!keyUrl.trim().isEmpty()) {
                if(getDomainName(url).equals(getDomainName(keyUrl)))
                    return key.toString();
            }
        }
        return null;
    }

    public static String getDomainName(String url)  {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            System.out.println(url);
            e.printStackTrace();
        }
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
