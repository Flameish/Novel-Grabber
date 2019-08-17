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

    /**
     * Stores all hyperlinks from the given URL and displays them on the GUI.
     */
    static void retrieveLinks()
            throws IllegalArgumentException, IOException {
        String url = NovelGrabberGUI.manChapterListURL.getText();
        NovelGrabberGUI.appendText("manual", "Retrieving links from: " + url);
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
        if (!chapterURLs.isEmpty()) NovelGrabberGUI.appendText("manual", chapterURLs.size() + " links retrieved.");
    }

    /**
     * Handles downloading chapters from provided list.
     */
    static void processChapersFromList(Download currGrab) throws IllegalArgumentException {
        String fileName;
        int chapterNumber = 0;
        NovelGrabberGUI.setMaxProgress(currGrab.window, chapterURLs.size());
        if (currGrab.invertOrder) Collections.reverse(chapterURLs);
        // Loop through all remaining chapter links and save them to file.
        for (String chapter : chapterURLs) {
            chapterNumber++;
            fileName = manSetChapterName(chapterNumber, currGrab.invertOrder);
            Shared.saveChapterWithHTML(chapter, chapterNumber, fileName, currGrab.chapterContainer, currGrab);
            Shared.sleep(currGrab.window);
        }
        // Since chapter links are not getting cleared, they need to be re-inversed.
        if (currGrab.invertOrder) {
            Collections.reverse(chapterURLs);
        }
    }

    /**
     * Handles downloading chapter to chapter.
     */
    static void processChaptersToChapters(String[] args, Download currGrab) {
        NovelGrabberGUI.appendText(currGrab.window, "[INFO]Connecting...");
        NovelGrabberGUI.setMaxProgress(currGrab.window, 9001);
        String nextChapter = args[0];
        String lastChapter = args[1];
        currGrab.nextChapterBtn = args[2];
        int chapterNumber = 0;
        while (true) {
            chapterNumber++;
            Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
            nextChapter = currGrab.nextChapterURL;
            if (nextChapter.equals(lastChapter) || (nextChapter + "/").equals(lastChapter)) {
                chapterNumber++;
                Shared.sleep(currGrab.window);
                currGrab.nextChapterBtn = "NOT_SET";
                Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
                break;
            }
            Shared.sleep("manual");
        }
    }

    static void manGetMetadata(Download currGrab) {
        if (NovelGrabberGUI.manMetadata[0] != null && !NovelGrabberGUI.manMetadata[0].isEmpty()) {
            currGrab.bookTitle = NovelGrabberGUI.manMetadata[0].replaceAll("[^a-zA-Z0-9.\\-]", " ");
        } else currGrab.bookTitle = "Unknown";
        if (NovelGrabberGUI.manMetadata[1] != null && !NovelGrabberGUI.manMetadata[1].isEmpty()) {
            currGrab.bookAuthor = NovelGrabberGUI.manMetadata[1].replaceAll("[^a-zA-Z0-9.\\-]", " ");
        } else currGrab.bookAuthor = "Unknown";
        if (NovelGrabberGUI.manMetadata[2] != null && !NovelGrabberGUI.manMetadata[2].isEmpty()) {
            currGrab.bookCover = NovelGrabberGUI.manMetadata[2];
        }
    }

    /**
     * Checks if chapter numeration is selected and set the file name accordingly.
     */
    private static String manSetChapterName(int chapterNumber, boolean invertOrder) {
        String fileName;
        if (invertOrder)
            fileName = NovelGrabberGUI.listModelChapterLinks.get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber);
        else fileName = NovelGrabberGUI.listModelChapterLinks.get(chapterNumber - 1);
        return fileName;
    }
}
