import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Collection of shared functions.
 */
class Shared {
    static final List<Integer> failedChapters = new ArrayList<>();
    static final List<String> successfulChapterNames = new ArrayList<>();
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
        NovelGrabberGUI.appendText(logWindow, "Finished! " + (successfulChapterNames.size() - failedChapters.size()) + " of "
                + chapterNumber + " chapters successfully grabbed in " + elapsedTime + " seconds.");
        if (!failedChapters.isEmpty()) {
            NovelGrabberGUI.appendText(logWindow, "Failed to grab the following chapters:");
            for (Integer num : failedChapters) {
                NovelGrabberGUI.appendText(logWindow, "Chapter " + num);
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
                for (String chapterFileName : successfulChapterNames) {
                    out.print("<a href=\"" + chapterFileName + "\">" + chapterFileName.replace(".html", "")
                            + "</a><br/>" + NL);
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
}
