import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Collection of shared functions.
 */
class Shared {
    static final List<String> failedChapters = new ArrayList<>();
    static final List<String> successfulChapterNames = new ArrayList<>();
    static List<String> images = new ArrayList<>();
    static List<String> blacklistedTags = new ArrayList<>();
    static final String textEncoding = "UTF-8";
    static final String NL = System.getProperty("line.separator");
    static String tocFileName = "Table Of Contents";
    static long startTime;
    static boolean getImages;
    static String nextChapterBtn = "NOT_SET";
    static String nextChapterURL;
    static String htmlHead = "<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL
            + "<meta charset=\"utf-8\" />" + NL + "</head>" + NL + "<body>" + NL;
    static String htmlFoot = "</body>" + NL + "</html>";

    /**
     * Processes a successful chapter.
     */
    private static void successfulChapter(String fileName, String window) {
        successfulChapterNames.add(fileName);
        NovelGrabberGUI.appendText(window, "[INFO]" + fileName + " saved.");
        NovelGrabberGUI.updateProgress(window);
    }

    /**
     * Logs elapsed process time and prints potential failed chapters after chapter grabs.
     */
    static void report(int chapterNumber, String logWindow) {
        long endTime = System.nanoTime();
        long elapsedTime = TimeUnit.SECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        NovelGrabberGUI.appendText(logWindow, "[INFO]Finished! " + successfulChapterNames.size() + " of "
                + chapterNumber + " chapters successfully grabbed in " + elapsedTime + " seconds.");
        if (!failedChapters.isEmpty()) {
            NovelGrabberGUI.appendText(logWindow, "[ERROR]Failed to grab the following chapters:");
            for (String failedChapter : failedChapters) {
                NovelGrabberGUI.appendText(logWindow, failedChapter);
            }
        }
    }

    /**
     * Creates a 'Table of Contents' file of successfully grabbed chapters and images.
     * (Calibre needs links to the images to display them)
     */
    static void createToc(String saveLocation, String logWindow) throws FileNotFoundException, UnsupportedEncodingException {
        if (!successfulChapterNames.isEmpty()) {
            String fileName = tocFileName + ".html";
            try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName, textEncoding)) {
                out.print(htmlHead + "<h1>Table of Contents</h1>" + NL + "<p style=\"text-indent:0pt\">" + NL);
                //Print chapter links
                for (String chapterFileName : successfulChapterNames) {
                    out.println("<a href=\"chapters/" + chapterFileName + ".html\">" + chapterFileName + "</a><br/>");
                }
                //Print image links (for calibre)
                if (!images.isEmpty()) {
                    for (String image : images) {
                        // Use hashCode of src + .jpg as the image name if renaming wasn't successful.
                        String imageName = getImageName(image);
                        if (imageName.equals("could_not_rename_image")) {
                            imageName = image.hashCode() + ".jpg";
                        }
                        out.println("<img src=\"images/" + imageName + "\" style=\"display:none;\" /><br/>");
                    }
                }
                out.print("</p>" + NL + htmlFoot);
            }
            NovelGrabberGUI.appendText(logWindow, "[INFO]" + fileName + " created.");
        }
    }

    /**
     * Freeze thread for selected wait time.
     */
    static void sleep(String window) {
        try {
            switch (window) {
                case "auto":
                    Thread.sleep(Integer.parseInt(NovelGrabberGUI.waitTime.getText()));
                case "manual":
                    Thread.sleep(Integer.parseInt(NovelGrabberGUI.manWaitTime.getText()));
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
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
        return imageName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    static void downloadImage(String src, String fileLocation, String logWindow) {
        if (!images.contains(src)) {
            // Try to set the image name
            String name = getImageName(src);
            // If image could not be renamed correctly, the hashCode of the source + .jpg
            // will be set as the image name.
            if (name.equals("could_not_rename_image")) {
                name = src.hashCode() + ".jpg";
            }

            try {
                // Connect to image source
                URL url = new URL(src);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                http.connect();
                InputStream input = http.getInputStream();
                byte[] buffer = new byte[4096];
                // Create images folder
                String filepath = fileLocation + File.separator + "images";
                File dir = new File(filepath);
                if (!dir.exists()) dir.mkdirs();
                // Save image to file
                try (OutputStream output = new FileOutputStream(new File(filepath + File.separator + name))) {
                    int n;
                    while ((n = input.read(buffer)) != -1) {
                        output.write(buffer, 0, n);
                    }
                }
                images.add(src);
                NovelGrabberGUI.appendText(logWindow, "[INFO]" + name + " saved.");
                //General catch
            } catch (Throwable e) {
                e.printStackTrace();
                NovelGrabberGUI.appendText(logWindow, "[ERROR]Failed to save " + name);
            }
        }
    }

    /**
     * Main method to save chapter content.
     */
    static void saveChapterWithHTML(String url, int chapterNumber, String fileName, String saveLocation,
                                    String chapterContainer, boolean chapterNumeration, String logWindow) {
        //Manual grabbing got it's own file naming method
        if (logWindow.equals("auto")) fileName = setFilename(chapterNumber, fileName, chapterNumeration);

        try {
            Document doc = Jsoup.connect(url).get();
            // Getting the next chapter URL from the "nextChapterBtn" href for Chapter-To-Chapter.
            if (!nextChapterBtn.equals("NOT_SET")) nextChapterURL = doc.select(nextChapterBtn).first().absUrl("href");

            Element chapterContent = doc.select(chapterContainer).first();
            // Remove unwanted tags from chapter container.
            for (String tag : blacklistedTags) {
                chapterContent.select(tag).remove();
            }
            //Download images of chapter container.
            if (getImages) {
                for (Element image : chapterContent.select("img")) {
                    Shared.downloadImage(image.absUrl("src"), saveLocation, logWindow);
                }
            }
            // Create chapters folder if it doesn't exist.
            File dir = new File(saveLocation + File.separator + "chapters");
            if (!dir.exists()) dir.mkdirs();
            // Write chapter content to file.
            try (PrintStream out = new PrintStream(dir.getPath() + File.separator + fileName + ".html", Shared.textEncoding)) {
                if (chapterContent.select("img").size() > 0) {
                    // Iterate each image in chapter content.
                    for (Element image : chapterContent.select("img")) {
                        // Check if the image was successfully downloaded.
                        String src = image.absUrl("src");
                        if (images.contains(src)) {
                            // Use hashCode of src + .jpg as the image name if renaming wasn't successful.
                            String imageName = getImageName(image.attr("src"));
                            if (imageName.equals("could_not_rename_image")) {
                                imageName = src.hashCode() + ".jpg";
                            }
                            // Replace href for image to point to local path.
                            image.attr("src", imageName);
                            // Remove the img tag if image wasn't downloaded.
                        } else chapterContent.select("img").remove();
                    }
                }
                // Write content to file.
                out.println(chapterContent);
            }
            Shared.successfulChapter(fileName, logWindow);
        } catch (Throwable e) {
            Shared.failedChapters.add(fileName);
            e.printStackTrace();
        }
    }

    /**
     * Adjust chapter names for chapter numeration.
     */
    private static String setFilename(int chapterNumber, String fileName, boolean chapterNumeration) {
        if (chapterNumeration) fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-");
        else fileName = fileName.replaceAll("[^\\w]+", "-");
        return fileName;
    }
}