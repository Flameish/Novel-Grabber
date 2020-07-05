package grabber;

import gui.GUI;
import gui.manSetMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManNovel extends Novel {
    private GUI gui;

    public ManNovel(GUI myGUI) {
        gui = myGUI;
        host = new HostSettings("no_domain");
        metadata = new Metadata(this);
        options = new NovelOptions();
        chapters = new ArrayList();
    }

    /**
     * Stores all hyperlinks from the given URL and displays them on the gui.
     */
    public void retrieveLinks() throws IllegalArgumentException, IOException {
        gui.appendText("manual", "Retrieving links from: " + novelLink);
        // Fetch webpage
        if (options.headless) {
            headless = new Driver(this);
            headless.driver.navigate().to(novelLink);
            String baseUrl = headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(headless.driver.getCurrentUrl(), "/", 3) + 1);
            tableOfContent = Jsoup.parse(headless.driver.getPageSource(), baseUrl);
            headless.close();
        } else {
            tableOfContent = Jsoup.connect(novelLink).get();
        }
        gui.manLinkListModel.removeAllElements();
        // Add every link as a new chapter and add to gui
        Elements links = tableOfContent.select("a[href]");
        for (Element chapterLink : links) {
            if (chapterLink.attr("abs:href").startsWith("http") && !chapterLink.text().isEmpty()) {
                Chapter chapter = new Chapter(chapterLink.text(),chapterLink.attr("abs:href"));
                gui.manLinkListModel.addElement(chapter);
            }
        }
        if (!chapters.isEmpty()) gui.appendText("manual", "[INFO]"+chapters.size() + " links retrieved.");
    }

    /**
     * Handles downloading chapters of a provided list.
     */
    public void processChaptersFromList() throws Exception {
        gui.setMaxProgress(options.window, chapters.size());
        if (options.invertOrder) Collections.reverse(chapters);
        if (options.headless) headless = new Driver(this);

        // Add chapters from listModel
        chapters = new ArrayList<>();
        for (int i = 0; i < gui.manLinkListModel.size(); i++) {
            chapters.add(gui.manLinkListModel.get(i));
        }

        for (Chapter chapter : chapters) {
            if(killTask) {
                // Remove already downloaded images and chapters
                try {
                    Path chaptersFolder = Paths.get(options.saveLocation + "/chapters");
                    Path imagesFolder = Paths.get(options.saveLocation + "/images");
                    if (Files.exists(imagesFolder)) GrabberUtils.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) GrabberUtils.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(options.window, e.getMessage());
                    e.printStackTrace();
                }
                throw new Exception("Grabbing stopped.");
            }
            chapter.saveChapter(this);
            gui.updateProgress(options.window);
            GrabberUtils.sleep(options.waitTime);
        }

        // Since chapter links are not getting cleared, they need to be re-inversed.
        if (options.invertOrder) Collections.reverse(chapters);
        if (options.headless) headless.close();
    }

    /**
     * Handles downloading chapter to chapter.
     */
    public void processChaptersToChapters(String[] args) throws Exception {
        gui.appendText(options.window, "[INFO]Connecting...");
        gui.setMaxProgress(options.window, 9001);
        nextChapterURL = args[0];
        String lastChapterURL = args[1];
        nextChapterBtn = args[2];
        // This chapter number is optional and is used to start the numbering at higher chapters
        int chapterNumber = GUI.chapterToChapterNumber;
        // Driver is used by each Chapter to visit the webpage
        if (options.headless) headless = new Driver(this);
        while (true) {
            if(killTask) {
                // Remove already downloaded images and chapters
                try {
                    Path chaptersFolder = Paths.get(options.saveLocation + "/chapters");
                    Path imagesFolder = Paths.get(options.saveLocation + "/images");
                    if (Files.exists(imagesFolder)) GrabberUtils.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) GrabberUtils.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(options.window, e.getMessage());
                    e.printStackTrace();
                }
                throw new Exception("Grabbing stopped.");
            }
            Chapter currentChapter = new Chapter("Chapter " + chapterNumber++, nextChapterURL);
            chapters.add(currentChapter);
            currentChapter.saveChapter(this);
            gui.updateProgress(options.window);
            // Reached final chapter
            if (nextChapterURL.equals(lastChapterURL) || (nextChapterURL + "/").equals(lastChapterURL)) {
                GrabberUtils.sleep(options.waitTime);
                nextChapterBtn = "NOT_SET";
                currentChapter = new Chapter("Chapter " + chapterNumber++, nextChapterURL);
                chapters.add(currentChapter);
                currentChapter.saveChapter(this);
                gui.updateProgress(options.window);
                break;
            }
            GrabberUtils.sleep(options.waitTime);
        }
    }

    /**
     * Set metadata specified in metadata GUI dialog
     */
    public void manGetMetadata() {
        if (manSetMetadata.manMetadataInfo[0] != null && !manSetMetadata.manMetadataInfo[0].isEmpty()) {
            metadata.bookTitle = manSetMetadata.manMetadataInfo[0].replaceAll("[\\\\/:*?\"<>|]", "");
        } else metadata.bookTitle = "Unknown";
        if (manSetMetadata.manMetadataInfo[1] != null && !manSetMetadata.manMetadataInfo[1].isEmpty()) {
            metadata.bookAuthor = manSetMetadata.manMetadataInfo[1];
        } else metadata.bookAuthor = "Unknown";
        if (manSetMetadata.manMetadataInfo[2] != null && !manSetMetadata.manMetadataInfo[2].isEmpty()) {
            metadata.bookCover = manSetMetadata.manMetadataInfo[2];
        }
        if (manSetMetadata.manMetadataInfo[3] != null && !manSetMetadata.manMetadataInfo[3].isEmpty()) {
            metadata.bookDesc.add(manSetMetadata.manMetadataInfo[3]);
        } else {
            metadata.bookDesc.add(0, "");
        }
        if (manSetMetadata.manMetadataTags != null && !manSetMetadata.manMetadataTags.isEmpty()) {
            metadata.bookSubjects = manSetMetadata.manMetadataTags;
        }
    }
}
