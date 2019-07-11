import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Chapter download handling
 */
class autoFetchChapters {
    private static String fileType;
    private static String tocUrl;
    private static String saveLocation;
    private static String host;
    private static boolean chapterNumeration;
    private static boolean withParagraphTags;
    private static boolean withPureText;
    private static boolean withHTML;
    private static String logWindow = "auto";

    /**
     * Opens novel's table of contents page, retrieves chapter links from selected
     * chapter range and processes them with saveChapters()
     */
    static void getChapterLinks() throws IllegalArgumentException, IOException {
        Shared.startTime = System.nanoTime();
        getOptions();
        int chaptersProcessed = 0;
        Novel currentNovel = new Novel(host, tocUrl);
        List<String> chapterLinks = new ArrayList<>();
        List<String> chaptersNames = new ArrayList<>();

        NovelGrabberGUI.appendText("auto", "Connecting...");
        Document doc = Jsoup.connect(currentNovel.getUrl()).get();
        //Get chapter links
        Elements chapterItems = doc.select(currentNovel.getChapterLinkSelector());
        Elements links = chapterItems.select("a[href]");
        for (Element chapterLink : links) {
            chapterLinks.add(chapterLink.attr("abs:href"));
            chaptersNames.add(chapterLink.text());
        }
        //Reverse link order if selected
        if (NovelGrabberGUI.checkInvertOrder.isSelected()) {
            Collections.reverse(chapterLinks);
            Collections.reverse(chaptersNames);
        }
        //grab all chapters
        if (NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            Shared.tocFileName = (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), ""));
            NovelGrabberGUI.setMaxProgress("auto", chapterLinks.size());
            //Decide what text selection to use
            if (withParagraphTags) {
                NovelGrabberGUI.progressBar.setStringPainted(true);
                for (int i = 1; i <= links.size(); i++) {
                    chaptersProcessed++;
                    Shared.saveChapterParagraphTag(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                            saveLocation, currentNovel.getChapterContainer(), chapterNumeration, logWindow, fileType);
                    Shared.sleep(logWindow);
                }
            }
            if (withPureText) {
                NovelGrabberGUI.progressBar.setStringPainted(true);
                for (int i = 1; i <= links.size(); i++) {
                    chaptersProcessed++;
                    Shared.saveChapterPureText(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                            saveLocation, currentNovel.getChapterContainer(), chapterNumeration, logWindow, fileType);
                    Shared.sleep(logWindow);
                }
            }
            if (withHTML) {
                NovelGrabberGUI.progressBar.setStringPainted(true);
                for (int i = 1; i <= links.size(); i++) {
                    chaptersProcessed++;
                    Shared.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                            saveLocation, currentNovel.getChapterContainer(), chapterNumeration, logWindow, fileType);
                    Shared.sleep(logWindow);
                }
            }

            //grab chapters from specific range
        } else {
            int firstChapter = Integer.parseInt(NovelGrabberGUI.firstChapter.getText());
            int lastChapter = Integer.parseInt(NovelGrabberGUI.lastChapter.getText());
            if (lastChapter > chapterLinks.size()) {
                NovelGrabberGUI.appendText("auto", "[ERROR] Novel does not have that many chapters. " +
                        "(" + chapterLinks.size() + " detected.)");
                return;
            }
            //Set Table of Content file name
            Shared.tocFileName = "Table of Contents " + firstChapter + "-" + lastChapter;
            NovelGrabberGUI.setMaxProgress("auto", (lastChapter - firstChapter) + 1);

            //Decide what text selection to use
            if (withParagraphTags) {
                NovelGrabberGUI.progressBar.setStringPainted(true);
                for (int i = firstChapter; i <= lastChapter; i++) {
                    chaptersProcessed++;
                    Shared.saveChapterParagraphTag(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                            saveLocation, currentNovel.getChapterContainer(), chapterNumeration, logWindow, fileType);
                    Shared.sleep(logWindow);
                }
            }
            if (withPureText) {
                NovelGrabberGUI.progressBar.setStringPainted(true);
                for (int i = firstChapter; i <= lastChapter; i++) {
                    chaptersProcessed++;
                    Shared.saveChapterPureText(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                            saveLocation, currentNovel.getChapterContainer(), chapterNumeration, logWindow, fileType);
                    Shared.sleep(logWindow);
                }
            }
            if (withHTML) {
                NovelGrabberGUI.progressBar.setStringPainted(true);
                for (int i = firstChapter; i <= lastChapter; i++) {
                    chaptersProcessed++;
                    Shared.saveChapterWithHTML(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1),
                            saveLocation, currentNovel.getChapterContainer(), chapterNumeration, logWindow, fileType);
                    Shared.sleep(logWindow);
                }
            }
        }
        Shared.report(chaptersProcessed, "auto");
    }

    /**
     * Opens chapter link and tries to save it's content in current directory.
     */
    static void saveSingleChapter()
            throws IllegalArgumentException, IOException {
        String url = NovelGrabberGUI.singleChapterLink.getText();
        String host = NovelGrabberGUI.singleChapterHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", "");
        Novel currentNovel = new Novel(host, url);
        NovelGrabberGUI.appendText("auto", "Connecting...");
        Document doc = Jsoup.connect(url).get();
        String fileName = doc.title().replaceAll("[^\\w]+", "-") + ".html";
        try {
            Element content = doc.select(currentNovel.getChapterContainer()).first();
            Elements p = content.select("p");
            try (PrintStream out = new PrintStream(fileName, Shared.textEncoding)) {
                out.print(Shared.htmlHead);
                for (Element x : p) {
                    out.print("<p>" + x.text() + "</p>" + Shared.NL);
                }
                out.print(Shared.htmlFoot);
            }
            NovelGrabberGUI.appendText("auto", fileName + " saved.");
        } catch (Exception noSelectors) {
            NovelGrabberGUI.appendText("auto", "Could not detect selectors on: " + url);
        }
    }

    /**
     * Saves user input from the 'Automatic' window tab to local field variables for better readability.
     */
    private static void getOptions() {
        tocUrl = NovelGrabberGUI.chapterListURL.getText();
        fileType = NovelGrabberGUI.fileType.getSelectedItem().toString();
        saveLocation = NovelGrabberGUI.saveLocation.getText();
        host = NovelGrabberGUI.allChapterHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", "");
        chapterNumeration = NovelGrabberGUI.useNumeration.isSelected();
        withParagraphTags = NovelGrabberGUI.paragraphTextSelect.isSelected();
        withPureText = NovelGrabberGUI.pureTextSelect.isSelected();
        withHTML = NovelGrabberGUI.getImages.isSelected();
    }
}
