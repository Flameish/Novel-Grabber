package grabber;

import grabber.sources.Source;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import system.init;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Collection of shared utility methods
 */
public class GrabberUtils {

    public static String getFilenameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String urlPath = url.getPath();
            return urlPath.substring(urlPath.lastIndexOf('/') + 1);
        } catch (MalformedURLException e) {
           err(e.getMessage(), e);
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
            err("Image URL malformed: " + e.getMessage(), e);
        } catch (IOException e) {
            err("Could not read image: " + e.getMessage(), e);
        }

        return image;
    }

    /**
     * Tries to find the container which contains chapter links.
     */
    public static List<Chapter> getMostLikelyChapters(Document doc) {
        info("Trying to detect chapters.");
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
            if(domain != null) {
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            }
        } catch (URISyntaxException e) {
            err("Malformed URL: " +e.getReason(), e);
        }
        return null;
    }

    public static void info(String msg) {
        System.out.println("[INFO]" + msg);
    }

    public static void info(String window, String msg) {
        System.out.println("[INFO]" + msg);
        if(init.gui != null && window != null) {
            init.gui.appendText(window,"[INFO]" + msg);
        }
    }

    public static void err(String msg) {
        log(msg);
        System.err.println("[ERROR]" + msg);
    }
    public static void err(String window, String msg) {
        System.err.println("[ERROR]" + msg);
        log(msg);
        if(init.gui != null && window != null) {
            init.gui.appendText(window,"[ERROR]" + msg);
        }
    }

    public static void err(String msg, Exception e) {
        System.err.println("[ERROR]" + msg);
        e.printStackTrace();
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        log(errors.toString());
    }

    public static void err(String window, String msg, Exception e) {
        System.err.println("[ERROR]" + msg);
        e.printStackTrace();
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        log(errors.toString());
        if(init.gui != null && window != null) {
            init.gui.appendText(window,"[ERROR]" + msg);
        }
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                GrabberUtils.err(e.getMessage(), e);
            }
        }
    }
    public static String getHTMLErrMsg(HttpStatusException httpEr) {
        String errorMsg = "";
        int errorCode = httpEr.getStatusCode();
        switch (errorCode) {
            case 403:
                errorMsg = "Forbidden! (403)";
                break;
            case 404:
                errorMsg = "Page not found! (404)";
                break;
            case 500:
                errorMsg = "Server error! (500)";
                break;
            case 503:
                errorMsg = "Service Unavailable! (503)";
                break;
            case 504:
                errorMsg = "Gateway Timeout! (504)";
                break;
            default:
                errorMsg = "Could not connect to webpage!";
        }
        return errorMsg;
    }

    /**
     * Returns found sources.
     * @return {@code ArrayList<Source>} or empty list
     */
    public static List<Source> getSources() {
        List<Source> sources = new ArrayList<>();
        try {
            String curPath = getCurrentPath();
            // Create ClassLoader
            File dir = new File(curPath + "/sources");
            URL loadPath = dir.toURI().toURL();
            URL[] urls = new URL[]{loadPath};
            URLClassLoader classLoader = new URLClassLoader(urls);
            // Loop through class files in source folder and load them via ClassLoader
            File[] sourceFiles = getSourceFiles(curPath + "/sources/grabber/sources");
            if (sourceFiles == null) {
                err("No source files found!");
                return sources;
            }
            for (File file: sourceFiles) {
                // Ignore source interface and manual files
                if(file.getName().equals("Source.class") || file.getName().equals("example_com.class")) continue;
                // Create a temporary Source object of interface to get name of host/source
                String className = "grabber.sources." + file.getName().replace(".class","");
                sources.add((Source) classLoader.loadClass(className).getConstructor().newInstance());
            }
            sources.sort(Comparator.comparing(Source::getName));
            return sources;
        } catch (Exception e) {
            err(e.getMessage(), e);
            return sources;
        }
    }

    /**
     * Returns Source based on domain name.
     * @return {@code Source} or null
     */
    public static Source getSource(String domain) throws ClassNotFoundException, IOException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        String curPath = getCurrentPath();
        // Create ClassLoader
        File dir = new File(curPath + "/sources");
        URL loadPath = dir.toURI().toURL();
        URL[] urls = new URL[]{loadPath};
        URLClassLoader classLoader = new URLClassLoader(urls);

        // Convert url to filename format
        domain = domain.replaceAll("[^A-Za-z0-9]", "_");
        // Supported sources have their domain name as their class name and java does not allow class names
        // to start with digits, which is possible for domain names, we need to add a 'n' for number in front.
        if (domain.substring(0, 1).matches("\\d")) domain = "n" + domain;

        File sourceFile = new File(curPath + "/sources/grabber/sources/" + domain + ".class");
        if (!sourceFile.exists()) {
            err("No source file found for: " + domain);
            return null;
        } else {
            String className = "grabber.sources." + sourceFile.getName().replace(".class", "");
            return (Source) classLoader.loadClass(className).getConstructor().newInstance();
        }
    }

    /**
     * Returns source files inside a directory.
     * @param pathname A pathname String
     * @return {@code File[]} with .class files or empty array
     */
    private static File[] getSourceFiles(String pathname) {
        try {
            File dir = new File(pathname);
            FilenameFilter filter = (file, name) -> name.endsWith(".class");
            return dir.listFiles(filter);
        } catch (Exception e) {
            err(e.getMessage(), e);
            return new File[0];
        }
    }

    /**
     * Returns the current working directory.
     * @return Absolute path as {@code String} or "."
     */
    public static String getCurrentPath() {
        try {
            return new File(init.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .getParentFile()
                    .getPath();
        } catch (URISyntaxException e) {
            err(e.getMessage(), e);
            return ".";
        }
    }

    public static void createDir(String filepath) {
        File dir = new File(filepath);
        if (!dir.exists()) dir.mkdirs();
    }

    public static void sleep(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void loadFontsFromFolder() {
        File fontFolder =  new File(getCurrentPath() + "/fonts/");
        if (!fontFolder.exists()) return;

        FilenameFilter filter = (f, name) -> name.endsWith(".ttf");
        File[] fontFiles = fontFolder.listFiles(filter);
        for (File fontFile: fontFiles) {
            try {
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));
            } catch (FontFormatException | IOException e) {
                GrabberUtils.err(e.getMessage(), e);
            }
        }
    }

    public static void log(String msg) {
        String time = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getCurrentPath() + "/log.txt", true))) {
            writer.write("[" + time + "] " + msg);
            writer.write("\n");
        } catch (IOException e) {
            err(e.getMessage(), e);
        }
    }

}
