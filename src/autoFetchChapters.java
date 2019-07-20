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
        this.saveLocation = NovelGrabberGUI.saveLocation.getText();
        this.chapterNumeration = NovelGrabberGUI.useNumeration.isSelected();
        this.allChapters = NovelGrabberGUI.chapterAllCheckBox.isSelected();
        this.invertOrder = NovelGrabberGUI.checkInvertOrder.isSelected();
        if (!NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            this.firstChapter = Integer.parseInt(NovelGrabberGUI.firstChapter.getText());
            this.lastChapter = Integer.parseInt(NovelGrabberGUI.lastChapter.getText());
        }
        this.getImages = NovelGrabberGUI.getImages.isSelected();

        String tocUrl = NovelGrabberGUI.chapterListURL.getText();
        String host = Objects.requireNonNull(NovelGrabberGUI.allChapterHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");
        this.currentNovel = new Novel(host, tocUrl);
        long startTime = System.nanoTime();
        a = new Shared(currentNovel.getBlacklistedTags());
        getChapterLinks();
        if (NovelGrabberGUI.createTocCheckBox.isSelected()) a.createToc(saveLocation, window);
        a.report(chaptersProcessed, window, startTime);
    }

    /**
     * Opens chapter link and tries to save it's content in current directory.
     */
    static void saveSingleChapter() {
        String url = NovelGrabberGUI.singleChapterLink.getText();
        String host = Objects.requireNonNull(NovelGrabberGUI.singleChapterHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");
        Novel currentNovel = new Novel(host, url);
        NovelGrabberGUI.appendText(window, "Connecting...");
        Shared a = new Shared(currentNovel.getBlacklistedTags());
        a.saveChapterWithHTML(url, 1, "Chapter", "./", currentNovel.getChapterContainer(), false, window, NovelGrabberGUI.getImages.isSelected());
    }

    private void getChapterLinks() {
        List<String> chapterLinks = new ArrayList<>();
        List<String> chaptersNames = new ArrayList<>();
        try {
            NovelGrabberGUI.appendText(window, "[INFO]Connecting...");
            Document doc = Jsoup.connect(currentNovel.getUrl()).get();
            //Get chapter links
            Elements chapterItems = doc.select(currentNovel.getChapterLinkSelector());
            Elements links = chapterItems.select("a[href]");
            for (Element chapterLink : links) {
                chapterLinks.add(chapterLink.attr("abs:href"));
                chaptersNames.add(chapterLink.text());
            }
            // Reverse link order if selected
            if (invertOrder) {
                Collections.reverse(chapterLinks);
                Collections.reverse(chaptersNames);
            }
            // Grab all chapters
            if (allChapters) {
                processAllChapters(chapterLinks, chaptersNames, doc, links);
                // Grab chapters from specific range
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

    private void processSpecificChapters(List<String> chapterLinks, List<String> chaptersNames) {
        Shared.tocFileName = "Table of Contents " + firstChapter + "-" + lastChapter;
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
        Shared.tocFileName = (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), ""));
        NovelGrabberGUI.setMaxProgress(window, chapterLinks.size());
        //Decide what text selection to use
        NovelGrabberGUI.progressBar.setStringPainted(true);
        for (int i = 1; i <= links.size(); i++) {
            chaptersProcessed++;
            a.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                    saveLocation, currentNovel.getChapterContainer(), chapterNumeration, window, getImages);
            Shared.sleep(window);
        }
    }
}
