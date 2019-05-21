import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
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
    private static boolean useSentenceSelector;
    private static String[] blacklistedWords = {"next chapter", "previous chapter", "table of contents", "twitter", "facebook", "tumblr"};

    /**
     * Opens novel's table of contents page, retrieves chapter links from selected
     * chapter range and processes them with saveChapters()
     */
    static void getChapterLinks()
            throws IllegalArgumentException, IOException {
        Shared.startTime = System.nanoTime();
        getOptions();
        int chaptersProcessed = 0;
        Novel currentNovel = new Novel(host, tocUrl);
        List<String> chapterLinks = new ArrayList<>();
        List<String> chaptersNames = new ArrayList<>();
        NovelGrabberGUI.appendText("auto", "Connecting...");
        Document doc = Jsoup.connect(currentNovel.getUrl()).get();
        Elements content = doc.select(currentNovel.getChapterLinkContainer());
        Elements chapterItem = content.select(currentNovel.getChapterLinkSelector());
        Elements links = chapterItem.select("a[href]");
        for (Element chapterLink : links) {
            chapterLinks.add(chapterLink.attr("abs:href"));
            chaptersNames.add(chapterLink.text());
        }
        if (NovelGrabberGUI.checkInvertOrder.isSelected()) {
            Collections.reverse(chapterLinks);
            Collections.reverse(chaptersNames);
        }
        //grab all chapters
        if (NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            Shared.tocFileName = "Table-of-Contents-"
                    + (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), ""));
            NovelGrabberGUI.setMaxProgress("auto", chapterLinks.size());
            for (int i = 1; i <= links.size(); i++) {
                chaptersProcessed++;
                saveChapters(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1));
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
            Shared.tocFileName = "Table-of-Contents-"
                    + (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), "")
                    + "-Chapter-" + firstChapter + "-" + lastChapter);
            NovelGrabberGUI.setMaxProgress("auto", (lastChapter - firstChapter) + 1);
            for (int i = firstChapter; i <= lastChapter; i++) {
                chaptersProcessed++;
                saveChapters(chapterLinks.get(i - 1), i, chaptersNames.get(i - 1));
            }
        }
        Shared.report(chaptersProcessed, "auto");
    }

    /**
     * Connects to given URL and saves content from selected html container to desired file output.
     */
    private static void saveChapters(String url, int chapterNumber, String fileName)
            throws IllegalArgumentException, IOException {
        Novel currentNovel = new Novel(host, url);
        Document doc = Jsoup.connect(url).get();
        doc.outputSettings().prettyPrint(false);
        String logWindow = "auto";
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        if (chapterNumeration) {
            fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-") + fileType;
        } else {
            fileName = fileName.replaceAll("[^\\w]+", "-") + fileType;
        }
        try {
            Element content = doc.select(currentNovel.getChapterContainer()).first();
            // sentence selector function
            if (!useSentenceSelector) {
                Elements p = content.select(currentNovel.getSentenceSelector());
                if (p.isEmpty()) {
                    NovelGrabberGUI.appendText(logWindow,
                            "[ERROR] Could not detect sentence wrapper for chapter " + chapterNumber + "(" + url + ")");
                    Shared.failedChapters.add(chapterNumber);
                    return;
                } else {
                    File dir = new File(saveLocation);
                    if (!dir.exists())
                        dir.mkdirs();
                    try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName, Shared.textEncoding)) {
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
                //all text function
            } else {
                content.select("br").append("\\n");
                content.select("p").prepend("\\n\\n");
                String chapterText = content.text().replaceAll("\\\\n", "\n");
                File dir = new File(saveLocation);
                if (!dir.exists()) {
                    dir.mkdirs();
                } else {
                    try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName, Shared.textEncoding)) {
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
                }
                Shared.successfulChapter(fileName, logWindow);
            }
        } catch (Exception noSelectors) {
            NovelGrabberGUI.appendText(logWindow, "Could not detect selectors on: " + url);
        }
        Shared.sleep(logWindow);
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
            Elements p = content.select(currentNovel.getSentenceSelector());
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
        useSentenceSelector = NovelGrabberGUI.useSentenceSelector.isSelected();
    }
}
