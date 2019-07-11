import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    static List<String> blacklistedTag = new ArrayList<>();
    static final String textEncoding = "UTF-8";
    static final String NL = System.getProperty("line.separator");
    static String tocFileName = "Table Of Contents";
    private static LocalTime time = LocalTime.now();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    static long startTime;
    static String htmlHead = "<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL
            + "<meta charset=\"utf-8\" />" + NL + "</head>" + NL + "<body>" + NL;
    static String htmlFoot = "</body>" + NL + "</html>";

    /**
     * Processes a successful chapter.
     */
    static void successfulChapter(String fileName, String window) {
        successfulChapterNames.add(fileName);
        NovelGrabberGUI.appendText(window, fileName + " saved.");
        NovelGrabberGUI.updateProgress(window);
    }

    /**
     * Logs elapsed time and potential failed chapters after chapter grabs.
     */
    static void report(int chapterNumber, String logWindow) {
        long endTime = System.nanoTime();
        long elapsedTime = TimeUnit.SECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        NovelGrabberGUI.appendText(logWindow, "Finished! " + successfulChapterNames.size() + " of "
                + chapterNumber + " chapters successfully grabbed in " + elapsedTime + " seconds.");
        if (!failedChapters.isEmpty()) {
            NovelGrabberGUI.appendText(logWindow, "Failed to grab the following chapters:");
            for (String failedChapter : failedChapters) {
                NovelGrabberGUI.appendText(logWindow, failedChapter);
            }
        }
    }

    /**
     * Creates a 'Table of Contents' file of successfully grabbed chapters.
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
                        out.println("<img src=\"images/" + getImageName(image) + "\" style=\"display:none;\" /><br/>");
                    }
                }
                out.print("</p>" + NL + htmlFoot);
            }
            NovelGrabberGUI.appendText(logWindow, fileName + " created.");
        }
    }

    /**
     * Sleep for selected wait time.
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

    static String time() {
        time = LocalTime.now();
        return "[" + time.format(formatter) + "] ";
    }

    static String getImageName(String src) {
        String imageName;
        int indexname = src.lastIndexOf("/");
        if (indexname == src.length()) {
            src = src.substring(1, indexname);
        }
        indexname = src.lastIndexOf("/");
        imageName = src.substring(indexname + 1);
        if (imageName.contains(".png")) imageName = imageName.replaceAll("\\.png(.*)", ".png");
        else if (imageName.contains(".jpg")) imageName = imageName.replaceAll("\\.jpg(.*)", ".jpg");
        else if (imageName.contains(".gif")) imageName = imageName.replaceAll("\\.gif(.*)", ".gif");
        else {
            return "could_not_rename_image";
        }
        return imageName;
    }

    static void downloadImage(String src, String fileLocation, String logWindow) throws Throwable {
        if (!images.contains(src)) {
            String filepath = fileLocation + File.separator + "images";
            File dir = new File(filepath);
            if (!dir.exists()) dir.mkdirs();
            String name = getImageName(src);
            URL url = new URL(src);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream input = http.getInputStream();
            byte[] buffer = new byte[4096];
            int n;
            OutputStream output = new FileOutputStream(new File(filepath + File.separator + name));
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
            output.close();
            images.add(src);
            NovelGrabberGUI.appendText(logWindow, name + " saved.");
        }
    }

    //TODO: Shorten the arguments
    static void saveChapterParagraphTag(String url, int chapterNumber, String fileName, String saveLocation,
                                        String chapterContainer, boolean chapterNumeration, String logWindow,
                                        String fileType) throws IllegalArgumentException, IOException {
        Document doc = Jsoup.connect(url).get();
        doc.outputSettings().prettyPrint(false);
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        if (chapterNumeration) {
            fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-");
        } else {
            fileName = fileName.replaceAll("[^\\w]+", "-");
        }
        try {
            Element content = doc.select(chapterContainer).first();
            Elements p = content.select("p"); //paragraph "p" tag
            if (p.isEmpty()) {
                NovelGrabberGUI.appendText(logWindow,
                        "[ERROR] Could not detect sentence wrapper for chapter " + chapterNumber + "(" + url + ")");
                Shared.failedChapters.add(fileName);
                return;
            } else {
                File dir = new File(saveLocation + File.separator + "chapters");
                if (!dir.exists()) dir.mkdirs();
                try (PrintStream out = new PrintStream(dir.getPath() + File.separator + fileName + fileType, Shared.textEncoding)) {
                    if (fileType.equals(".txt")) {
                        for (Element x : p) {
                            out.print(x.text() + Shared.NL);
                        }
                    }
                    if (fileType.equals(".html")) {
                        out.print(Shared.htmlHead);
                        for (Element x : p) {
                            out.print("<p>" + x.text() + "</p>" + Shared.NL);
                        }
                        out.print(Shared.htmlFoot);
                    }
                }
                Shared.successfulChapter(fileName, logWindow);
            }
        } catch (Exception noSelectors) {
            Shared.failedChapters.add(fileName);
            noSelectors.printStackTrace();
        }
    }

    /**
     * Connects to given URL and saves content from selected html container to desired file output.
     */
    static void saveChapterPureText(String url, int chapterNumber, String fileName, String saveLocation,
                                    String chapterContainer, boolean chapterNumeration, String logWindow,
                                    String fileType) throws IllegalArgumentException, IOException {
        Document doc = Jsoup.connect(url).get();
        doc.outputSettings().prettyPrint(false);
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        if (chapterNumeration) {
            fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-");
        } else {
            fileName = fileName.replaceAll("[^\\w]+", "-");
        }
        try {
            Element content = doc.select(chapterContainer).first();
            content.select("br").append("\\n");
            content.select("p").prepend("\\n\\n");
            String chapterText = content.text().replaceAll("\\\\n", "\n");
            File dir = new File(saveLocation + File.separator + "chapters");
            if (!dir.exists()) dir.mkdirs();
            try (PrintStream out = new PrintStream(dir.getPath() + File.separator + fileName + fileType, Shared.textEncoding)) {
                if (fileType.equals(".txt")) {
                    out.print(chapterText);
                }
                if (fileType.equals(".html")) {
                    out.print(Shared.htmlHead);
                    try (BufferedReader reader = new BufferedReader(new StringReader(chapterText))) {
                        String line = reader.readLine();
                        while (line != null) {
                            if (!line.isEmpty()) {
                                out.append("<p>").append(line).append("</p>").append(Shared.NL);
                            }
                            line = reader.readLine();
                        }
                    }
                    out.print(Shared.htmlFoot);
                }
            }
            Shared.successfulChapter(fileName, logWindow);
        } catch (Exception noSelectors) {
            Shared.failedChapters.add(fileName);
            noSelectors.printStackTrace();
        }
    }

    static void saveChapterWithHTML(String url, int chapterNumber, String fileName, String saveLocation,
                                    String chapterContainer, boolean chapterNumeration, String logWindow,
                                    String fileType) {
        //Adjust chapter names for chapter numeration.
        if (chapterNumeration) {
            fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-");
        } else {
            fileName = fileName.replaceAll("[^\\w]+", "-");
        }
        try {
            Document doc = Jsoup.connect(url).get();
            //Download images of chapter container.
            Element contentImages = doc.select(chapterContainer).first();
            Elements images = contentImages.select("img");
            for (Element image : images) {
                Shared.downloadImage(image.absUrl("src"), saveLocation, logWindow);
            }
            //Remove unwanted tags from whole doc.
            for (String tag : Shared.blacklistedTag) {
                doc.select(tag).remove();
            }
            //Write chapter content to file. Attention: Gets all divs with the same selection container as well.
            Elements chapterContent = doc.select(chapterContainer);
            File dir = new File(saveLocation + File.separator + "chapters");
            if (!dir.exists()) dir.mkdirs();
            try (PrintStream out = new PrintStream(dir.getPath() + File.separator + fileName + fileType, Shared.textEncoding)) {
                for (Element a : chapterContent) {
                    //Replace href for images
                    a.select("img").attr("src", Shared.getImageName(a.select("img").attr("src")));
                    //Print line to file
                    out.println(a);
                }
            }
            Shared.successfulChapter(fileName, logWindow);
        } catch (Throwable e) {
            Shared.failedChapters.add(fileName);
            e.printStackTrace();
        }
    }
}