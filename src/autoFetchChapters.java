import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Chapter download handling of the automatic tab.
 */
class autoFetchChapters {
    private static String window = "auto";
    private String tocFileName;
    private String saveLocation;
    private boolean chapterNumeration;
    private boolean allChapters;
    private boolean invertOrder;
    private boolean getImages;
    private int firstChapter;
    private int lastChapter;
    private Novel currentNovel;
    private int chaptersProcessed;
    private Shared a;

    autoFetchChapters() {
        long startTime = System.nanoTime();
        this.saveLocation = NovelGrabberGUI.saveLocation.getText();
        this.chapterNumeration = NovelGrabberGUI.useNumeration.isSelected();
        this.allChapters = NovelGrabberGUI.chapterAllCheckBox.isSelected();
        this.invertOrder = NovelGrabberGUI.checkInvertOrder.isSelected();
        if (!NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            this.firstChapter = Integer.parseInt(NovelGrabberGUI.firstChapter.getText());
            if (!NovelGrabberGUI.toLastChapter.isSelected()) {
                this.lastChapter = Integer.parseInt(NovelGrabberGUI.lastChapter.getText());
            }
        }
        this.getImages = NovelGrabberGUI.getImages.isSelected();
        String tocUrl = NovelGrabberGUI.chapterListURL.getText();
        String host = Objects.requireNonNull(NovelGrabberGUI.allChapterHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");

        this.currentNovel = new Novel(host, tocUrl);
        a = new Shared(currentNovel.getBlacklistedTags());
        getChapterLinks();
        if (NovelGrabberGUI.createTocCheckBox.isSelected()) a.createToc(saveLocation, window, tocFileName);
        a.report(chaptersProcessed, window, startTime);
    }

    /**
     * Displays chapter name and chapter number.
     */
    static void getChapterNumber(String host, String chapterURL) {
        List<String> chapterLinks = new ArrayList<>();
        List<String> chaptersNames = new ArrayList<>();
        Novel tempNovel = new Novel(host, "");
        String novelLink = chapterURL.substring(0, ordinalIndexOf(chapterURL, "/", tempNovel.getordinalIndexForBaseNovel()));
        tempNovel = new Novel(host, novelLink);
        if (tempNovel.getHost().equals("http://gravitytales.com/")) novelLink = novelLink + "/chapters";
        try {
            Document doc = Jsoup.connect(novelLink).get();
            // Get chapter links and names.
            Elements chapterItems = doc.select(tempNovel.getChapterLinkSelector());
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
                NovelGrabberGUI.appendText(window, "[INFO]Chapter name: " + chaptersNames.get(chapterNumber));
                NovelGrabberGUI.appendText(window, "[INFO]Chapter number: " + (chapterNumber + 1));
            }
        } catch (IllegalArgumentException | IOException e) {
            NovelGrabberGUI.appendText(window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processSpecificChapters(List<String> chapterLinks, List<String> chaptersNames) {
        tocFileName = "Table of Contents " + firstChapter + "-" + lastChapter;
        NovelGrabberGUI.setMaxProgress(window, (lastChapter - firstChapter) + 1);

        NovelGrabberGUI.progressBar.setStringPainted(true);
        for (int i = firstChapter; i <= lastChapter; i++) {
            chaptersProcessed++;
            a.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                    saveLocation, currentNovel.getChapterContainer(), chapterNumeration, window, getImages);
            Shared.sleep(window);
        }
    }

    private void processAllChapters(List<String> chapterLinks, List<String> chaptersNames, Document doc, Elements links) {
        tocFileName = (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), ""));
        NovelGrabberGUI.setMaxProgress(window, chapterLinks.size());
        NovelGrabberGUI.progressBar.setStringPainted(true);
        for (int i = 1; i <= links.size(); i++) {
            chaptersProcessed++;
            a.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                    saveLocation, currentNovel.getChapterContainer(), chapterNumeration, window, getImages);
            Shared.sleep(window);
        }
    }

    // Utility
    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    private void getChapterLinks() {
        List<String> chapterLinks = new ArrayList<>();
        List<String> chaptersNames = new ArrayList<>();
        try {
            NovelGrabberGUI.appendText(window, "[INFO]Connecting...");
            Document doc = Jsoup.connect(currentNovel.getUrl()).get();
            // Get chapter links and names.
            Elements chapterItems = doc.select(currentNovel.getChapterLinkSelector());
            Elements links = chapterItems.select("a[href]");
            for (Element chapterLink : links) {
                chapterLinks.add(chapterLink.attr("abs:href"));
                chaptersNames.add(chapterLink.text());
            }
            // Reverse link order if selected.
            if (invertOrder) {
                Collections.reverse(chapterLinks);
                Collections.reverse(chaptersNames);
            }
            // To latest chapter.
            if (NovelGrabberGUI.toLastChapter.isSelected()) {
                lastChapter = chapterLinks.size();
            }
            // Grab all chapters.
            if (allChapters) {
                processAllChapters(chapterLinks, chaptersNames, doc, links);
                // Grab chapters from specific range.
            } else {
                if (lastChapter > chapterLinks.size()) {
                    NovelGrabberGUI.appendText(window, "[ERROR] Novel does not have that many chapters. " +
                            "(" + chapterLinks.size() + " detected.)");
                    return;
                }
                processSpecificChapters(chapterLinks, chaptersNames);
            }
        } catch (IllegalArgumentException | IOException e) {
            NovelGrabberGUI.appendText(window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }
}
