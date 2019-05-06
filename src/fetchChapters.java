import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Chapter download handling
 */
class fetchChapters {
    static final List<String> chapterFileNames = new ArrayList<>();
    static final List<String> chapterURLs = new ArrayList<>();
    static final List<Integer> failedChapters = new ArrayList<>();
    private static final String NL = System.getProperty("line.separator");
    private static final String textEncoding = "UTF-8";
    private static String tocFileName = "Table Of Contents";
    private static long startTime;
    private static String tocUrl;
    private static String fileType;
    private static String saveLocation;
    private static String host;
    private static boolean chapterNumeration;
    private static boolean useSentenceSelector;
    private static String manChapterContainer;
    private static String manSaveLocation;
    private static String manFileType;
    private static String manSentenceSelector;
    private static boolean manUseNumeration;
    private static boolean manCheckInvertOrder;
    private static boolean manUseSentenceSelector;
    private static String htmlHead = "<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL
            + "<meta charset=\"utf-8\" />" + NL + "</head>" + NL + "<body>" + NL;
    private static String htmlFoot = "</body>" + NL + "</html>";

    /**
     * Opens novel's table of contents page, retrieves chapter links from selected
     * chapter range and processes them with saveChapters()
     */
    static void getChapterLinks()
            throws IllegalArgumentException, IOException {
        startTime = System.nanoTime();
        int chapterNumber = 0;
        getOptions("auto");
        Novel currentNovel = new Novel(host, tocUrl);
        ArrayList<String> chapters = new ArrayList<>();
        ArrayList<String> chaptersNames = new ArrayList<>();
        NovelGrabberGUI.appendText("auto", "Connecting...");
        Document doc = Jsoup.connect(currentNovel.getUrl()).get();
        Elements content = doc.select(currentNovel.getChapterLinkContainer());
        Elements chapterItem = content.select(currentNovel.getChapterLinkSelector());
        Elements links = chapterItem.select("a[href]");
        //adds chapter links to array
        for (Element chapterLink : links) {
            chapters.add(chapterLink.attr("abs:href"));
            chaptersNames.add(chapterLink.text());
        }
        if (NovelGrabberGUI.checkInvertOrder.isSelected()) {
            Collections.reverse(chapters);
            Collections.reverse(chaptersNames);
        }
        //Check if "grab all chapter" is selected and grab chapters from specific range if not
        if (!NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            int firstChapter = Integer.parseInt(NovelGrabberGUI.firstChapter.getText());
            int lastChapter = Integer.parseInt(NovelGrabberGUI.lastChapter.getText());
            tocFileName = "Table-of-Contents-"
                    + (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), "") + "-Chapter-"
                    + firstChapter + "-" + lastChapter);
            if (lastChapter > chapters.size()) {
                NovelGrabberGUI.appendText("auto", "[ERROR] Novel does not have that many chapters. (" + chapters.size() + " detected.)");
                return;
            } else {
                NovelGrabberGUI.setMaxProgress("auto", (lastChapter - firstChapter) + 1);
                for (int i = firstChapter - 1; i <= lastChapter - 1; i++) {
                    chapterNumber++;
                    saveChapters(chapters.get(i), chapterNumber, chaptersNames.get(i));
                }
            }
            //grab all chapters
        } else {
            for (int i = 0; i <= links.size() - 1; i++) {
                chapterNumber++;
                saveChapters(chapters.get(i), chapterNumber, chaptersNames.get(i));
            }
        }
        report(chapterNumber, "auto");
    }

    /**
     * Saves input from GUI into field variables.
     */
    private static void getOptions(String window) {
        switch (window) {
            case ("auto"):
                tocUrl = NovelGrabberGUI.chapterListURL.getText();
                fileType = NovelGrabberGUI.fileType.getSelectedItem().toString();
                saveLocation = NovelGrabberGUI.saveLocation.getText();
                host = NovelGrabberGUI.allChapterHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", "");
                chapterNumeration = NovelGrabberGUI.useNumeration.isSelected();
                useSentenceSelector = NovelGrabberGUI.useSentenceSelector.isSelected();
            case ("manual"):
                manSaveLocation = NovelGrabberGUI.manSaveLocation.getText();
                manFileType = NovelGrabberGUI.manFileType.getSelectedItem().toString();
                manUseNumeration = NovelGrabberGUI.manUseNumeration.isSelected();
                manChapterContainer = NovelGrabberGUI.manChapterContainer.getText();
                manSentenceSelector = NovelGrabberGUI.manSentenceSelector.getText();
                manCheckInvertOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
                manUseSentenceSelector = NovelGrabberGUI.manUseSentenceSelector.isSelected();
                if (manSentenceSelector.isEmpty() && !manUseSentenceSelector) {
                    manUseSentenceSelector = true;
                }
        }
    }

    private static void saveChapters(String url, int chapterNumber, String fileName)
            throws IllegalArgumentException, IOException {
        Novel currentNovel = new Novel(host, url);
        Document doc = Jsoup.connect(url).get();
        if (!chapterNumeration) {
            fileName = fileName.replaceAll("[^\\w]+", "-") + fileType;
        } else {
            fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-") + fileType;
        }
        try {
            Element content = doc.select(currentNovel.getChapterContainer()).first();
            // sentence selector function
            if (!useSentenceSelector) {
                Elements p = content.select(currentNovel.getSentenceSelector());
                if (p.isEmpty()) {
                    NovelGrabberGUI.appendText("auto",
                            "[ERROR] Could not detect sentence wrapper for chapter " + chapterNumber + "(" + url + ")");
                    failedChapters.add(chapterNumber);
                    return;
                } else {
                    File dir = new File(saveLocation);
                    if (!dir.exists())
                        dir.mkdirs();
                    try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName, textEncoding)) {
                        if (fileType.equals(".txt")) {
                            for (Element x : p) {
                                out.print(x.text() + NL);
                            }
                        }
                        if (fileType.equals(".html")) {
                            out.print(htmlHead);
                            for (Element x : p) {
                                out.print("<p>" + x.text() + "</p>" + NL);
                            }
                            out.print(htmlFoot);
                        }
                    }
                    successfulChapter(fileName, "auto");
                }

            } else {
                String chapterText = content.wholeText();
                File dir = new File(saveLocation);
                if (!dir.exists()) {
                    dir.mkdirs();
                } else {
                    try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName, textEncoding)) {
                        if (fileType.equals(".txt")) {
                            out.print(chapterText);
                        }
                        if (fileType.equals(".html")) {
                            out.print(htmlHead);
                            try (BufferedReader reader = new BufferedReader(new StringReader(chapterText))) {
                                String line = reader.readLine();
                                while (line != null) {
                                    if (!line.isEmpty()) {
                                        out.append("<p>").append(line).append("</p>");
                                    }
                                    line = reader.readLine();
                                }
                            } catch (IOException exc) {
                                // quit
                            }
                            out.print(htmlFoot);
                        }
                    }
                }
                successfulChapter(fileName, "auto");
            }
        } catch (Exception noSelectors) {
            NovelGrabberGUI.appendText("auto", "Could not detect selectors on: " + url);
        }
        sleep("auto");
    }

    //Processes a successful chapter
    private static void successfulChapter(String fileName, String window) {
        chapterFileNames.add(fileName);
        NovelGrabberGUI.appendText(window, fileName + " saved.");
        NovelGrabberGUI.updateProgress(window);
    }

    /**
     * Opens chapter link and tries to save it's content in current directory
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
            try (PrintStream out = new PrintStream(fileName, textEncoding)) {
                out.print(htmlHead);
                for (Element x : p) {
                    out.print("<p>" + x.text() + "</p>" + NL);
                }
                out.print(htmlFoot);
            }
            NovelGrabberGUI.appendText("auto", fileName + " saved.");
        } catch (Exception noSelectors) {
            NovelGrabberGUI.appendText("auto", "Could not detect selectors on: " + url);
        }
    }

    //Gets all hyperlinks from an URL
    static void retrieveLinks()
            throws IllegalArgumentException, IOException {
        String url = NovelGrabberGUI.manChapterListURL.getText();
        NovelGrabberGUI.appendText("manual", "Retrieving links from: " + url);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        String currChapterLink;
        int numberOfLinks = 0;
        for (Element chapterLink : links) {
            currChapterLink = chapterLink.attr("abs:href");
            if (currChapterLink.startsWith("http") && !chapterLink.text().isEmpty()) {
                chapterURLs.add(currChapterLink);
                NovelGrabberGUI.listModelChapterLinks.addElement(chapterLink.text());
                numberOfLinks++;
            }
        }
        if (!chapterURLs.isEmpty()) {
            NovelGrabberGUI.appendText("manual", numberOfLinks + " links retrieved.");
        }

    }

    /*
    Save selected chapters to file from manual tab
    TODO: combine with saveChapters()
    */
    static void manSaveChapters()
            throws IllegalArgumentException, IOException {
        startTime = System.nanoTime();
        getOptions("manual");
        String fileName;
        int chapterNumber = 0;
        String logWindow = "manual";
        NovelGrabberGUI.setMaxProgress("manual", chapterURLs.size());
        if (manCheckInvertOrder) {
            Collections.reverse(chapterURLs);
        }
        for (String chapter : chapterURLs) {
            chapterNumber++;
            Document doc = Jsoup.connect(chapter).get();
            fileName = manSetFileName(chapterNumber);
            try {
                Element content = doc.select(manChapterContainer).first();
                // sentence selector function
                if (!manUseSentenceSelector) {
                    Elements p = content.select(manSentenceSelector);
                    if (p.isEmpty()) {
                        NovelGrabberGUI.appendText("manual", "[ERROR] Could not detect sentence wrapper for chapter "
                                + chapterNumber + "(" + chapter + ")");
                        failedChapters.add(chapterNumber);
                    } else {
                        File dir = new File(manSaveLocation);
                        if (!dir.exists())
                            dir.mkdirs();
                        try (PrintStream out = new PrintStream(manSaveLocation + File.separator + fileName,
                                textEncoding)) {
                            if (manFileType.equals(".txt")) {
                                for (Element x : p) {
                                    out.println(x.text() + NL);
                                }
                            }
                            if (manFileType.equals(".html")) {
                                out.print(htmlHead);
                                for (Element x : p) {
                                    out.print("<p>" + x.text() + "</p>" + NL);
                                }
                                out.print(htmlFoot);
                            }
                        }
                        successfulChapter(fileName, "manual");
                    }
                } else { // grab all text function
                    String chapterText = content.wholeText();
                    File dir = new File(manSaveLocation);
                    if (!dir.exists())
                        dir.mkdirs();
                    try (PrintStream out = new PrintStream(manSaveLocation + File.separator + fileName, textEncoding)) {
                        if (manFileType.equals(".txt")) {
                            out.print(chapterText);
                        }
                        if (manFileType.equals(".html")) {
                            out.print(htmlHead);
                            try (BufferedReader reader = new BufferedReader(new StringReader(chapterText))) {
                                String line = reader.readLine();
                                while (line != null) {
                                    if (!line.isEmpty()) {
                                        out.append("<p>").append(line).append("</p>").append(NL);
                                    }
                                    line = reader.readLine();
                                }
                            } catch (IOException exc) {
                                failedChapters.add(chapterNumber);
                            }
                            out.print(htmlFoot);
                        }
                    }
                    successfulChapter(fileName, "manual");
                }

            } catch (Exception noSelectors) {
                NovelGrabberGUI.appendText("manual",
                        "[ERROR] Could not detect sentence wrapper for chapter " + chapterNumber + "(" + chapter + ")");
                failedChapters.add(chapterNumber);
            }
            sleep("manual");
        }
        report(chapterNumber, logWindow);
        if (manCheckInvertOrder) {
            Collections.reverse(chapterURLs);
        }
    }

    //Checks if chapter numeration is selected and set the file name accordingly
    //TODO: make more compact
    private static String manSetFileName(int chapterNumber) {
        String fileName;
        if (!manUseNumeration) {
            if (manCheckInvertOrder) {
                fileName = NovelGrabberGUI.listModelChapterLinks
                        .get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber)
                        .replaceAll("[^\\w]+", "-") + manFileType;
            } else {
                fileName = NovelGrabberGUI.listModelChapterLinks.get(chapterNumber - 1)
                        .replaceAll("[^\\w]+", "-") + manFileType;
            }

        } else {
            if (manCheckInvertOrder) {
                fileName = "Ch-" + chapterNumber + "-"
                        + NovelGrabberGUI.listModelChapterLinks
                        .get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber)
                        .replaceAll("[^\\w]+", "-")
                        + manFileType;
            } else {
                fileName = "Ch-" + chapterNumber + "-" + NovelGrabberGUI.listModelChapterLinks
                        .get(chapterNumber - 1).replaceAll("[^\\w]+", "-") + manFileType;
            }

        }
        return fileName;
    }

    //Logs elapsed time and potential failed chapters after chapter grabs
    private static void report(int chapterNumber, String logWindow) {
        long endTime = System.nanoTime();
        long elapsedTime = TimeUnit.SECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        NovelGrabberGUI.appendText(logWindow, "Finished! " + (chapterNumber - failedChapters.size()) + " of "
                + chapterNumber + " chapters successfully grabbed in " + elapsedTime + " seconds.");
        if (!failedChapters.isEmpty()) {
            NovelGrabberGUI.appendText(logWindow, "Failed to grab the following chapters:");
            for (Integer num : failedChapters) {
                NovelGrabberGUI.appendText(logWindow, "Chapter " + num);
            }
        }
    }

    //Creates a table of contents file of successfully grabbed chapters
    static void createToc(String saveLocation) throws FileNotFoundException, UnsupportedEncodingException {
        if (!chapterFileNames.isEmpty()) {
            String fileName = tocFileName + ".html";
            try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName, textEncoding)) {
                out.print("<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL
                        + "<meta charset=\"UTF-8\" />" + NL + "</head>" + NL + "<body>" + NL
                        + "<h1>Table of Contents</h1>" + NL + "<p style=\"text-indent:0pt\">" + NL);
                for (String chapterFileName : chapterFileNames) {
                    out.print("<a href=\"" + chapterFileName + "\">" + chapterFileName.replace(".html", "")
                            + "</a><br/>" + NL);
                }
                out.print("</p>" + NL + "</body>" + NL + "</html>" + NL);
            }
            NovelGrabberGUI.appendText("manual", fileName + " created.");
        }

    }

    //Sleep for selected wait time
    private static void sleep(String window) {
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
}
