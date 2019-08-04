import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chapter download handling of the manual tab.
 */
class manFetchChapters {
    static List<String> chapterURLs = new ArrayList<>();
    private static String window = "manual";
    private String tocFileName = "Table-of-Contents";
    private String chapterContainer;
    private String saveLocation;
    private boolean chapterNumeration;
    private boolean invertedOrder;
    private boolean getImages;
    private int chapterNumber;
    private Shared a;

    manFetchChapters(String method, List<String> blacklistedTags) {
        this.chapterContainer = NovelGrabberGUI.manChapterContainer.getText();
        this.saveLocation = NovelGrabberGUI.manSaveLocation.getText();
        this.chapterNumeration = NovelGrabberGUI.manUseNumeration.isSelected();
        this.invertedOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
        this.getImages = NovelGrabberGUI.manGetImages.isSelected();

        long startTime = System.nanoTime();
        a = new Shared(blacklistedTags);
        switch (method) {
            case "chapterToChapter":
                processChaptersToChapters(NovelGrabberGUI.chapterToChapterArgs);
                break;
            case "chaptersFromList":
                processChapersFromList();
                break;
        }
        if (NovelGrabberGUI.manCreateToc.isSelected()) a.createToc(saveLocation, window, tocFileName);
        a.report(chapterNumber, window, startTime);
    }

    /**
     * Stores all hyperlinks from the given URL and displays them on the GUI.
     */
    static void retrieveLinks()
            throws IllegalArgumentException, IOException {
        String url = NovelGrabberGUI.manChapterListURL.getText();
        NovelGrabberGUI.appendText(window, "Retrieving links from: " + url);
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
        if (!chapterURLs.isEmpty()) NovelGrabberGUI.appendText(window, chapterURLs.size() + " links retrieved.");
    }

    /**
     * Handles downloading chapters from provided list.
     */
    private void processChapersFromList() throws IllegalArgumentException {
        String fileName;
        chapterNumber = 0;
        NovelGrabberGUI.setMaxProgress(window, chapterURLs.size());
        if (invertedOrder) Collections.reverse(chapterURLs);
        // Loop through all remaining chapter links and save them to file.
        for (String chapter : chapterURLs) {
            chapterNumber++;
            fileName = manSetFileName(chapterNumber);
            a.saveChapterWithHTML(chapter, chapterNumber, fileName, saveLocation, chapterContainer, chapterNumeration, window, getImages);
            Shared.sleep(window);
        }
        // Since chapter links are not getting cleared, they need to be re-inversed.
        if (invertedOrder) {
            Collections.reverse(chapterURLs);
        }
    }

    /**
     * Handles downloading chapter to chapter.
     */
    private void processChaptersToChapters(String[] args) {
        NovelGrabberGUI.appendText(window, "[INFO]Connecting...");
        String nextChapter = args[0];
        String lastChapter = args[1];
        a.nextChapterBtn = args[2];
        int chapterNumber = 0;
        while (true) {
            chapterNumber++;
            a.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, saveLocation, chapterContainer, chapterNumeration, window, getImages);
            nextChapter = a.nextChapterURL;
            if (nextChapter.equals(lastChapter) || (nextChapter + "/").equals(lastChapter)) {
                chapterNumber++;
                Shared.sleep(window);
                a.nextChapterBtn = "NOT_SET";
                a.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, saveLocation, chapterContainer, chapterNumeration, window, getImages);
                break;
            }
            Shared.sleep("manual");
        }
    }

    /**
     * Checks if chapter numeration is selected and set the file name accordingly.
     */
    private String manSetFileName(int chapterNumber) {
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
}
