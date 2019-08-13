import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chapter download handling of the automatic tab.
 */
class autoFetchChapters {

    static void grabChapters(Download currGrab) {
        try {
            NovelGrabberGUI.appendText(currGrab.window, "[INFO]Connecting...");
            Document doc = Jsoup.connect(currGrab.currHostSettings.url).get();
            // Get chapter links and names.
            Elements chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
            Elements links = chapterItems.select("a[href]");
            for (Element chapterLink : links) {
                currGrab.chapterLinks.add(chapterLink.attr("abs:href"));
                currGrab.chaptersNames.add(chapterLink.text());
            }
            // Reverse link order if selected.
            if (currGrab.invertOrder) {
                Collections.reverse(currGrab.chapterLinks);
                Collections.reverse(currGrab.chaptersNames);
            }
            // To latest chapter.
            if (NovelGrabberGUI.toLastChapter.isSelected()) {
                currGrab.lastChapter = currGrab.chapterLinks.size();
            }
            // Grab all chapters.
            if (currGrab.allChapters) {
                processAllChapters(doc, links, currGrab);
                // Grab chapters from specific range.
            } else {
                if (currGrab.lastChapter > currGrab.chapterLinks.size()) {
                    NovelGrabberGUI.appendText(currGrab.window, "[ERROR] Novel does not have that many chapters. " +
                            "(" + currGrab.chapterLinks.size() + " detected.)");
                    return;
                }
                processSpecificChapters(currGrab);
            }
        } catch (IllegalArgumentException | IOException e) {
            NovelGrabberGUI.appendText(currGrab.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processSpecificChapters(Download currGrab) {
        currGrab.tocFileName = "Table of Contents " + currGrab.firstChapter + "-" + currGrab.lastChapter;
        NovelGrabberGUI.setMaxProgress(currGrab.window, (currGrab.lastChapter - currGrab.firstChapter) + 1);
        NovelGrabberGUI.progressBar.setStringPainted(true);
        for (int i = currGrab.firstChapter; i <= currGrab.lastChapter; i++) {
            Shared.saveChapterWithHTML(currGrab.chapterLinks.get(i - 1), i, currGrab.chaptersNames.get(i - 1),
                    currGrab.currHostSettings.chapterContainer, currGrab);
            Shared.sleep(currGrab.window);
        }
    }

    private static void processAllChapters(Document doc, Elements links, Download currGrab) {
        currGrab.tocFileName = (doc.title().replaceAll("[^\\w]+", "-").replace(currGrab.currHostSettings.titleHostName, ""));
        NovelGrabberGUI.setMaxProgress(currGrab.window, currGrab.chapterLinks.size());
        NovelGrabberGUI.progressBar.setStringPainted(true);
        for (int i = 1; i <= links.size(); i++) {
            Shared.saveChapterWithHTML(currGrab.chapterLinks.get(i - 1), i, currGrab.chaptersNames.get(i - 1),
                    currGrab.currHostSettings.chapterContainer, currGrab);
            Shared.sleep(currGrab.window);
        }
    }

    /**
     * Displays chapter name and chapter number.
     */
    static void getChapterNumber(String host, String chapterURL) {
        List<String> chapterLinks = new ArrayList<>();
        List<String> chaptersNames = new ArrayList<>();
        try {
            HostSettings tempHostSettings = new HostSettings(host, "");
            String novelLink = chapterURL.substring(0, Shared.ordinalIndexOf(chapterURL, "/", tempHostSettings.ordinalIndexForBaseNovel));
            tempHostSettings = new HostSettings(host, novelLink);
            if (tempHostSettings.host.equals("http://gravitytales.com/")) novelLink = novelLink + "/chapters";
            Document doc = Jsoup.connect(novelLink).get();
            // Get chapter links and names.
            Elements chapterItems = doc.select(tempHostSettings.chapterLinkSelecter);
            Elements links = chapterItems.select("a[href]");
            for (Element chapterLink : links) {
                chapterLinks.add(chapterLink.attr("abs:href"));
                chaptersNames.add(chapterLink.text());
            }
            int chapterNumber = chapterLinks.indexOf(chapterURL);
            if (chapterNumber == -1)
                chapterNumber = chapterLinks.indexOf(chapterURL.substring(0, chapterURL.lastIndexOf("/")));
            if (chapterNumber == -1) NovelGrabberGUI.showPopup("Could not find chapter number.", "error");
            else {
                NovelGrabberGUI.appendText("auto", "[INFO]Chapter name: " + chaptersNames.get(chapterNumber));
                NovelGrabberGUI.appendText("auto", "[INFO]Chapter number: " + (chapterNumber + 1));
            }
        } catch (IllegalArgumentException | IOException e) {
            NovelGrabberGUI.appendText("auto", "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }
}
