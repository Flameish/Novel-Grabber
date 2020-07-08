package grabber;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import system.Config;
import system.init;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class GrabberUtils {

    static void downloadImage(String src, Novel currGrab) {
        if (!currGrab.imageLinks.contains(src)) {
            // Try to set the image name
            String name = getImageName(src);
            // If image could not be renamed correctly, the hashCode of the source + .jpg
            // will be set as the image name.
            if (name.equals("could_not_rename_image")) {
                name = src.hashCode() + ".jpg";
            }
            //For LiberSpark
            if (src.startsWith("//")) src = src.replace("//", "https://");
            try {
                // Connect to image source
                URL url = new URL(src);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                http.connect();
                InputStream input = http.getInputStream();
                byte[] buffer = new byte[4096];
                // Create imageLinks folder
                String filepath = currGrab.options.saveLocation + File.separator + "images";
                File dir = new File(filepath);
                if (!dir.exists()) dir.mkdirs();
                // Save image to file
                try (OutputStream output = new FileOutputStream(new File(filepath + File.separator + name))) {
                    int n;
                    while ((n = input.read(buffer)) != -1) {
                        output.write(buffer, 0, n);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currGrab.imageLinks.add(src);
                currGrab.imageNames.add(name);

                if(init.window != null && !currGrab.options.window.equals("checker")) {
                    init.window.appendText(currGrab.options.window, "[INFO]" + name + " saved.");
                }
                //General catch
            } catch (Throwable e) {
                e.printStackTrace();
                if(init.window != null && !currGrab.options.window.equals("checker")) {
                    init.window.appendText(currGrab.options.window, "[ERROR]Failed to save " + name);
                }
            }
        }
    }

    /**
     * Returns the image name without the href address/path
     */
    static String getImageName(String src) {
        String imageName;
        int indexname = src.lastIndexOf("/");
        if (indexname == src.length()) {
            src = src.substring(1, indexname);
        }
        indexname = src.lastIndexOf("/");
        imageName = src.substring(indexname + 1);
        // Check against popular formats with regex
        if (imageName.contains(".png")) imageName = imageName.replaceAll("\\.png(.*)", ".png");
        else if (imageName.contains(".jpg")) imageName = imageName.replaceAll("\\.jpg(.*)", ".jpg");
        else if (imageName.contains(".gif")) imageName = imageName.replaceAll("\\.gif(.*)", ".gif");
        else return "could_not_rename_image";
        return imageName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    /**
     * Saves the autoNovel cover into a buffered Image
     */
    static BufferedImage getBufferedCover(String src, Novel novel) {
        if (!novel.imageLinks.contains(src)) {
            // Try to set the image name
            String name = getImageName(src);
            // If image could not be renamed correctly, the hashCode of the source + .jpg
            // will be set as the image name.
            if (name.equals("could_not_rename_image")) {
                name = src.hashCode() + ".jpg";
            }
            //For LiberSpark
            if (src.startsWith("//")) src = src.replace("//", "https://");
            try {
                URL url = new URL(src);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                http.connect();
                BufferedImage imageInput = ImageIO.read(http.getInputStream());
                novel.metadata.bufferedCoverName = name;
                novel.imageLinks.add(src);
                novel.imageNames.add(name);
                return imageInput;
                //General catch
            } catch (Throwable e) {
                e.printStackTrace();
                if(init.window != null && !novel.options.window.equals("checker")) {
                    init.window.appendText("auto", "[ERROR]Failed to get" + name);
                }
            }
        }
        return null;
    }

    static int getWordCount(String html) {
        Document dom = Jsoup.parse(html);
        String text = dom.text();
        return text.split(" ").length;
    }

    public static void deleteFolderAndItsContent(final Path folder) throws IOException {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    /**
     * Freeze thread for selected wait time.
     */
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
        for(Object key: Config.siteSelectorsJSON.keySet()) {
            Object keyvalue = Config.siteSelectorsJSON.get(key);
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
}
