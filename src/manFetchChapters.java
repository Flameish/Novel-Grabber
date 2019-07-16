import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manual tab chapter download handling.
 */
class manFetchChapters {
    static final List<String> chapterURLs = new ArrayList<>();
    private static String chapterContainer;
    private static String saveLocation;
    private static boolean chapterNumeration;
    private static boolean invertedOrder;
    private static String logWindow = "manual";

    /**
     * Stores all hyperlinks from the given URL.
     */
    static void retrieveLinks()
            throws IllegalArgumentException, IOException {
        String url = NovelGrabberGUI.manChapterListURL.getText();
        NovelGrabberGUI.appendText(logWindow, "Retrieving links from: " + url);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        String currChapterLink;
        chapterURLs.clear();
        NovelGrabberGUI.listModelChapterLinks.removeAllElements();
        for (Element chapterLink : links) {
            currChapterLink = chapterLink.attr("abs:href");
            if (currChapterLink.startsWith("http") && !chapterLink.text().isEmpty()) {
                chapterURLs.add(currChapterLink);
                NovelGrabberGUI.listModelChapterLinks.addElement(chapterLink.text());
            }
        }
        if (!chapterURLs.isEmpty()) NovelGrabberGUI.appendText(logWindow, chapterURLs.size() + " links retrieved.");
    }

    /**
     * Handles downloading each chapter.
     */
    static void manSaveChaptersFromList() throws IllegalArgumentException {
        Shared.startTime = System.nanoTime();
        getOptions();
        String fileName;
        int chapterNumber = 0;
        NovelGrabberGUI.setMaxProgress(logWindow, chapterURLs.size());
        if (invertedOrder) Collections.reverse(chapterURLs);
        // Loop through all remaining chapter links and save them to file.
        for (String chapter : chapterURLs) {
            chapterNumber++;
            fileName = manSetFileName(chapterNumber);
            Shared.saveChapterWithHTML(chapter, chapterNumber, fileName, saveLocation, chapterContainer, chapterNumeration, logWindow);
            Shared.sleep(logWindow);
        }
        Shared.report(chapterNumber, logWindow);
        // Since chapter links are not getting cleared, they need to be re-inversed.
        if (invertedOrder) {
            Collections.reverse(chapterURLs);
        }
    }

    static void saveChaptersLinkToLink(String[] args) {
        Shared.startTime = System.nanoTime();
        NovelGrabberGUI.appendText(logWindow, "[INFO]Connecting...");
        getOptions();
        Shared.nextChapterBtn = args[2];
        String nextChapter = args[0];
        int chapterNumber = 0;
        while (true) {
            chapterNumber++;
            Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, saveLocation, chapterContainer, chapterNumeration, logWindow);
            nextChapter = Shared.nextChapterURL;
            if (nextChapter.equals(args[1]) || (nextChapter + "/").equals(args[1])) {
                chapterNumber++;
                Shared.sleep("manual");
                Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, saveLocation, chapterContainer, chapterNumeration, logWindow);
                break;
            }
            Shared.sleep("manual");
        }
        // "Resetting" the nextChapterBtn
        Shared.nextChapterBtn = "NOT_SET";
        Shared.report(chapterNumber, logWindow);
    }

    /**
     * Checks if chapter numeration is selected and set the file name accordingly.
     */
    private static String manSetFileName(int chapterNumber) {
        String fileName;
        if (!chapterNumeration) {
            if (invertedOrder) {
                fileName = NovelGrabberGUI.listModelChapterLinks
                        .get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber)
                        .replaceAll("[^\\w]+", "-");
            } else {
                fileName = NovelGrabberGUI.listModelChapterLinks.get(chapterNumber - 1)
                        .replaceAll("[^\\w]+", "-");
            }
        } else {
            if (invertedOrder) {
                fileName = "Ch-" + chapterNumber + "-"
                        + NovelGrabberGUI.listModelChapterLinks
                        .get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber)
                        .replaceAll("[^\\w]+", "-");
            } else {
                fileName = "Ch-" + chapterNumber + "-" + NovelGrabberGUI.listModelChapterLinks
                        .get(chapterNumber - 1).replaceAll("[^\\w]+", "-");
            }
        }
        return fileName;
    }

    /**
     * Saves user input from the 'Manual' window tab to local field variables for better readability.
     */
    private static void getOptions() {
        chapterContainer = NovelGrabberGUI.manChapterContainer.getText();
        saveLocation = NovelGrabberGUI.manSaveLocation.getText();
        chapterNumeration = NovelGrabberGUI.manUseNumeration.isSelected();
        invertedOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
    }
}
