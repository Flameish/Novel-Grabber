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
    private static String fileType;
    private static boolean chapterNumeration;
    private static boolean invertedOrder;
    private static boolean withParagraphTags;
    private static boolean withPureText;
    private static boolean withHTML;
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
     * Save selected chapters to file from manual tab
     */
    static void manSaveChapters()
            throws IllegalArgumentException, IOException {
        Shared.startTime = System.nanoTime();
        getOptions();
        String fileName;
        int chapterNumber = 0;
        NovelGrabberGUI.setMaxProgress(logWindow, chapterURLs.size());
        if (invertedOrder) Collections.reverse(chapterURLs);
        //loops through all remaining chapter links and save them to file
        for (String chapter : chapterURLs) {
            chapterNumber++;
            fileName = manSetFileName(chapterNumber);
            if (withParagraphTags) {
                Shared.saveChapterParagraphTag(chapter, chapterNumber, fileName, saveLocation, chapterContainer, chapterNumeration, logWindow, fileType);
            }
            if (withPureText) {
                Shared.saveChapterPureText(chapter, chapterNumber, fileName, saveLocation, chapterContainer, chapterNumeration, logWindow, fileType);
            }
            if (withHTML) {
                Shared.saveChapterWithHTML(chapter, chapterNumber, fileName, saveLocation, chapterContainer, chapterNumeration, logWindow, fileType);
            }
            Shared.sleep(logWindow);
        }
        Shared.report(chapterNumber, logWindow);
        if (invertedOrder) {
            Collections.reverse(chapterURLs);
        }
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
        fileType = NovelGrabberGUI.manFileType.getSelectedItem().toString();
        chapterNumeration = NovelGrabberGUI.manUseNumeration.isSelected();
        invertedOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
        withParagraphTags = NovelGrabberGUI.manParagraphTextSelect.isSelected();
        withPureText = NovelGrabberGUI.manPureTextSelect.isSelected();
        withHTML = NovelGrabberGUI.manGetImages.isSelected();
    }
}
