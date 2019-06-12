import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
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
    private static String sentenceSelector;
    private static boolean chapterNumeration;
    private static boolean invertedOrder;
    private static boolean useSentenceSelector;

    /**
     * Stores all hyperlinks from the given URL.
     */
    static void retrieveLinks()
            throws IllegalArgumentException, IOException {
        String url = NovelGrabberGUI.manChapterListURL.getText();
        NovelGrabberGUI.appendText("manual", "Retrieving links from: " + url);
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
        if (!chapterURLs.isEmpty()) NovelGrabberGUI.appendText("manual", chapterURLs.size() + " links retrieved.");
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
        String logWindow = "manual";
        NovelGrabberGUI.setMaxProgress(logWindow, chapterURLs.size());
        if (sentenceSelector.isEmpty() && !useSentenceSelector) useSentenceSelector = true;
        if (invertedOrder) Collections.reverse(chapterURLs);
        //loops through all remaining chapter links and save them to file
        for (String chapter : chapterURLs) {
            chapterNumber++;
            Document doc = Jsoup.connect(chapter).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            fileName = manSetFileName(chapterNumber);
            try {
                Element content = doc.select(chapterContainer).first();
                // sentence selector function
                if (!useSentenceSelector) {
                    Elements p = content.select(sentenceSelector);
                    if (p.isEmpty()) {
                        NovelGrabberGUI.appendText(logWindow, "[ERROR] Could not detect sentence wrapper for chapter "
                                + chapterNumber + "(" + chapter + ")");
                        Shared.failedChapters.add(chapterNumber);

                    } else {
                        File dir = new File(saveLocation);
                        if (!dir.exists())
                            dir.mkdirs();
                        try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName,
                                Shared.textEncoding)) {
                            if (fileType.equals(".txt")) {
                                for (Element x : p) {
                                    out.println(x.text() + Shared.NL);
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
                    if (!dir.exists())
                        dir.mkdirs();
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
                            } catch (IOException exc) {
                                Shared.failedChapters.add(chapterNumber);
                            }
                            out.print(Shared.htmlFoot);
                        }
                    }
                    Shared.successfulChapter(fileName, logWindow);
                }

            } catch (Exception noSelectors) {
                NovelGrabberGUI.appendText(logWindow,
                        "[ERROR] Could not detect sentence wrapper for chapter " + chapterNumber + "(" + chapter + ")");
                Shared.failedChapters.add(chapterNumber);
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
                        .replaceAll("[^\\w]+", "-") + fileType;
            } else {
                fileName = NovelGrabberGUI.listModelChapterLinks.get(chapterNumber - 1)
                        .replaceAll("[^\\w]+", "-") + fileType;
            }

        } else {
            if (invertedOrder) {
                fileName = "Ch-" + chapterNumber + "-"
                        + NovelGrabberGUI.listModelChapterLinks
                        .get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber)
                        .replaceAll("[^\\w]+", "-")
                        + fileType;
            } else {
                fileName = "Ch-" + chapterNumber + "-" + NovelGrabberGUI.listModelChapterLinks
                        .get(chapterNumber - 1).replaceAll("[^\\w]+", "-") + fileType;
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
        sentenceSelector = NovelGrabberGUI.manSentenceSelector.getText();
        chapterNumeration = NovelGrabberGUI.manUseNumeration.isSelected();
        invertedOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
        useSentenceSelector = NovelGrabberGUI.manUseSentenceSelector.isSelected();
    }
}
