package grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import system.init;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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

    public static BufferedImage getImage(String urlStr) {
        BufferedImage image = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0");
            image = ImageIO.read(connection.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Image URL malformed: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERROR] Could not read image: " + e.getMessage());
        }

        return image;
    }

    /**
     * Tries to find the container which contains chapter links.
     */
    public static List<Chapter> getMostLikelyChapters(Document doc) {
        System.out.println("[GRABBER]Trying to detect chapters.");
        Element mostLikely = doc.select("body").first().child(0);

        // Set the container with the most direct children as most likely candidate;
        for(Element el: doc.select("body").select("*")) {
            if(mostLikely.childrenSize() < el.childrenSize()) {
                if(!el.select("a").isEmpty()) mostLikely = el;
            }
        }
        List chapterList = new ArrayList<>();
        // Add links as chapters from most likely container
        for (Element chapterLink : mostLikely.select("a[href]")) {
            if (chapterLink.attr("abs:href").startsWith("http") && !chapterLink.text().isEmpty()) {
                Chapter chapter = new Chapter(chapterLink.text(),chapterLink.attr("abs:href"));
                chapterList.add(chapter);
            }
        }
        return chapterList;
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
        else return null;
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

    public static String getDomainName(String url)  {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.showPopup("Malformed URL: " +e.getReason(), "error");
            }
        }
        return null;
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
