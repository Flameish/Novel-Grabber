package grabber;

import gui.GUI;
import gui.manSetMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Chapter download handling of the manual tab.
 */
public class manFetchChapters {
    public static List<String> chapterURLs = new ArrayList<>();
    public static boolean killTask = false;

    /**
     * Stores all hyperlinks from the given URL and displays them on the gui.
     */
    public static void retrieveLinks(GUI gui)
            throws IllegalArgumentException, IOException {
        String url = gui.manNovelURL.getText();
        gui.appendText("manual", "Retrieving links from: " + url);
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        String currChapterLink;
        chapterURLs.clear();
        GUI.listModelChapterLinks.removeAllElements();
        for (Element chapterLink : links) {
            currChapterLink = chapterLink.attr("abs:href");
            if (currChapterLink.startsWith("http") && !chapterLink.text().isEmpty()) {
                chapterURLs.add(currChapterLink);
                GUI.listModelChapterLinks.addElement(chapterLink.text());
            }
        }
        if (!chapterURLs.isEmpty()) gui.appendText("manual", chapterURLs.size() + " links retrieved.");
    }

    /**
     * Handles downloading chapters from provided list.
     */
    static void processChapersFromList(Download currGrab) throws IllegalArgumentException {
        String fileName;
        int chapterNumber = 0;
        currGrab.gui.setMaxProgress(currGrab.window, chapterURLs.size());
        if (currGrab.invertOrder) Collections.reverse(chapterURLs);
        // Loop through all remaining chapter links and save them to file.
        for (String chapter : chapterURLs) {
            chapterNumber++;
            fileName = manSetChapterName(currGrab, chapterNumber, currGrab.invertOrder);
            Shared.saveChapterWithHTML(chapter, chapterNumber, fileName, currGrab.chapterContainer, currGrab);
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
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
        currGrab.gui.appendText(currGrab.window, "[INFO]Connecting...");
        currGrab.gui.setMaxProgress(currGrab.window, 9001);
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
                Shared.sleep(currGrab.waitTime);
                currGrab.nextChapterBtn = "NOT_SET";
                Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
                break;
            }
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
        }
    }

    static void manGetMetadata(Download currGrab) {
        if (manSetMetadata.manMetadataInfo[0] != null && !manSetMetadata.manMetadataInfo[0].isEmpty()) {
            currGrab.bookTitle = manSetMetadata.manMetadataInfo[0].replaceAll("[\\\\/:*?\"<>|]", "");
        } else currGrab.bookTitle = "Unknown";
        if (manSetMetadata.manMetadataInfo[1] != null && !manSetMetadata.manMetadataInfo[1].isEmpty()) {
            currGrab.bookAuthor = manSetMetadata.manMetadataInfo[1];
        } else currGrab.bookAuthor = "Unknown";
        if (manSetMetadata.manMetadataInfo[2] != null && !manSetMetadata.manMetadataInfo[2].isEmpty()) {
            currGrab.bookCover = manSetMetadata.manMetadataInfo[2];
        }
        if (manSetMetadata.manMetadataInfo[3] != null && !manSetMetadata.manMetadataInfo[3].isEmpty()) {
            currGrab.bookDesc.add(manSetMetadata.manMetadataInfo[3]);
        } else {
            currGrab.bookDesc.add("");
        }
        if (manSetMetadata.manMetadataTags != null && !manSetMetadata.manMetadataTags.isEmpty()) {
            currGrab.bookSubjects = manSetMetadata.manMetadataTags;
        }
        currGrab.noDescription = manSetMetadata.noDescription;
    }

    /**
     * Checks if chapter numeration is selected and set the file name accordingly.
     */
    private static String manSetChapterName(Download currGrab, int chapterNumber, boolean invertOrder) {
        String fileName;
        if (invertOrder)
            fileName = GUI.listModelChapterLinks.get(GUI.listModelChapterLinks.getSize() - chapterNumber);
        else fileName = GUI.listModelChapterLinks.get(chapterNumber - 1);
        return fileName;
    }
}
