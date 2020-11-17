package grabber;

import grabber.sources.Source;
import org.jsoup.nodes.Document;
import system.init;
import java.awt.image.BufferedImage;
import java.util.*;

public class Novel {
    public Source source;
    public Driver headlessDriver;
    public Document tableOfContent;
    public Map<String, String> cookies;
    public List<Chapter> chapterList;
    public NovelMetadata metadata;
    public List<String> blacklistedTags;
    public HashMap<String, BufferedImage> images = new HashMap<>();
    public boolean killTask;
    public boolean reGrab = false;
    public boolean removeStyling = false;
    public boolean getImages = false;
    public boolean displayChapterTitle = false;
    public boolean noDescription = false;
    public boolean reverseOrder = false;
    public boolean useHeadless = false;
    public boolean headlessGUI = false;
    public boolean useAccount = false;
    public boolean autoDetectContainer = false;
    public int waitTime = 0;
    public int firstChapter;
    public int lastChapter;
    public int wordCount = 0;
    public String saveLocation;
    public String window;
    public String browser;
    public String hostname;
    public String novelLink;
    public String nextChapterBtn = "NOT_SET";
    public String nextChapterURL;
    public String epubFilename;

    /**
     * Main novel download handling object.
     * Fetches and stores novel information.
     * Downloads chapters and creates EPUB.
     */
    public Novel() { }

    /**
     * Builder methods to create a novel object from various download settings.
     */
    public static NovelBuilder builder() {
        return new NovelBuilder();
    }

    /**
     * Modifies an existing Novel object without creating a new one.
     */
    public static NovelBuilder modifier(Novel novel) {
        return new NovelBuilder(novel);
    }

    /**
     * Fetches metadata, blacklisted tags, chapter list.
     */
    public void check() {
        if(source != null) {
            if(useAccount) {
                try {
                    cookies = source.getLoginCookies();
                } catch (UnsupportedOperationException e) {
                    System.err.println("[ERROR]Source does not support login.");
                    if(init.gui != null) {
                        init.gui.appendText(window,"[ERROR]Source does not support login.");
                    }
                }
            }
            chapterList = source.getChapterList();
            // Are created in GUI for manual
            if(!window.equals("manual")) {
                blacklistedTags = source.getBlacklistedTags();
                metadata = source.getMetadata();
            }
        }
    }

    /**
     * Downloads chapters from list.
     * @throws Exception on stopped grabbing.
     */
    public void downloadChapters() throws Exception {
        System.out.println("[GRABBER]Starting download...");
        // Preparation
        if(init.gui != null) {
            init.gui.setMaxProgress(window, lastChapter-firstChapter+1);
        }
        if(reGrab) {
            wordCount = 0;
            for(Chapter chapter: chapterList) chapter.status = 0; // Reset download status of chapters
        }
        if(reverseOrder) Collections.reverse(chapterList);
        // Download handling
        for(int i = firstChapter-1; i < lastChapter; i++) { // -1 since chapter numbers start at 1
            if(killTask) {
                throw new Exception("[GRABBER]Download stopped.");
            }
            chapterList.get(i).saveChapter(this);
            if(init.gui != null) {
                init.gui.updateProgress(window);
            }
            GrabberUtils.sleep(waitTime);
        }
        reGrab = true;
    }

    /**
     * Follows the chapters via a "next chapter button".
     */
    public void processChaptersToChapters(String firstChapterURL,
                                          String lastChapterURL,
                                          String nextChapterBtn,
                                          String chapterNumberString) throws Exception {
        init.gui.appendText(window, "[GRABBER]Connecting...");
        init.gui.setMaxProgress(window, 9001);

        nextChapterURL = firstChapterURL;
        this.nextChapterBtn = nextChapterBtn;
        int chapterNumber = 1;
        if(chapterNumberString != null && !chapterNumberString.isEmpty()) chapterNumber = Integer.parseInt(chapterNumberString);

        if (useHeadless) headlessDriver = new Driver(window, browser);

        chapterList = new ArrayList<>();
        while (true) {
            if(killTask) {
                throw new Exception("Grabbing stopped.");
            }

            Chapter currentChapter = new Chapter("Chapter " + chapterNumber++, nextChapterURL);
            chapterList.add(currentChapter);
            currentChapter.saveChapter(this);

            init.gui.updateProgress(window);

            // Reached final chapter
            if (nextChapterURL.equals(lastChapterURL) || (nextChapterURL + "/").equals(lastChapterURL)) {
                GrabberUtils.sleep(waitTime);
                this.nextChapterBtn = "NOT_SET";
                currentChapter = new Chapter("Chapter " + chapterNumber++, nextChapterURL);
                chapterList.add(currentChapter);
                currentChapter.saveChapter(this);
                init.gui.updateProgress(window);
                break;
            }
            GrabberUtils.sleep(waitTime);
        }
    }

    /**
     * Prints potential failed chapters.
     * Reverses the chapter order for next grabbing. (If grabbing was stopped with this option selected)
     * Closes headless driver if used.
     * Writes EPUB
     */
    public void output() {
        // Print finishing information
        if(init.gui != null) {
            init.gui.appendText(window,"[GRABBER]Finished.\n"); // GUI doesn't need save location displaying
        }

        // Reverse chapter order if needed for potential re-grabbing
        if(reverseOrder) Collections.reverse(chapterList);

        // Print failed chapters
        for(Chapter chapter: chapterList) {
            if(chapter.status == 2) // 0 = not downloaded, 1 = successfully downloaded, 2 = failed download
                if(init.gui != null) {
                    init.gui.appendText(window,"[GRABBER]Failed to download: " +chapter.name);
                }
        }

        if(headlessDriver != null) {
            headlessDriver.close();
            headlessDriver = null; // close() != null --> checks to start a new browser are against null
        }

        // Output EPUB
        EPUB epub = new EPUB(this);
        epub.writeEpub();
    }
}