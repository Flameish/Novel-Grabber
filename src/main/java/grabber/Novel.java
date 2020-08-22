package grabber;

import gui.GUI;
import gui.manSetMetadata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import grabber.scripts.ChapterListScripts;
import grabber.scripts.LoginScripts;
import system.Config;
import system.init;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Novel {
    public Driver headlessDriver;
    public Document tableOfContent;
    public Document tempPage;
    public Map<String, String> cookies;
    public List<Chapter> chapterList;
    public List<String> blacklistedTags = new ArrayList<>();
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
    public String bookTitle;
    public String bookAuthor;
    public List<String> bookSubjects = new ArrayList();
    public String bookDesc;
    public BufferedImage bufferedCover;
    public String bufferedCoverName;
    public String bookCover;
    public String url;
    public String chapterLinkSelector;
    public String chapterContainer;
    public String bookTitleSelector;
    public String bookDescSelector;
    public String bookAuthorSelector;
    public String bookSubjectSelector;
    public String bookCoverSelector;

    /**
     * Main novel download handling object.
     * Fetches and stores novel information.
     * Downloads chapters and creates EPUB.
     */
    public Novel() { }

    /**
     * Builder methods to create a novel object from various download settings.
     * @return
     */
    public static NovelBuilder builder() {
        return new NovelBuilder();
    }

    /**
     * Modifies an existing Novel object without creating a new one.
     * @param novel
     * @return
     */
    public static NovelBuilder modifier(Novel novel) {
        return new NovelBuilder(novel);
    }

    /**
     * Fetches the chapter list of novel.
     * Gets potential login cookies for host site.
     * Create potential headless driver if selected.
     */
    public void fetchChapterList() {
        if(useAccount) {
            LoginScripts.getLoginCookies(this);
        }
        if(useHeadless) {
            headlessDriver = new Driver(this);
        }
        ChapterListScripts.getList(this);
    }

    /**
     * Downloads chapters to file.
     * Updates download progress on GUI.
     * @throws Exception on stopped grabbing.
     */
    public void downloadChapters() throws Exception {
        System.out.println("[GRABBER]Starting download...");

        if(init.gui != null) {
            init.gui.setMaxProgress(window, lastChapter-firstChapter+1);
        }

        // Reset information if re-grab
        if(reGrab) {
            wordCount = 0;
            // Reset download status of chapters
            for(Chapter chapter: chapterList) chapter.status = 0;
        }

        if(reverseOrder) Collections.reverse(chapterList);

        // Create headless driver if it wasn't previously created during chapter list fetching
        if(useHeadless && headlessDriver == null) {
            headlessDriver = new Driver(this);
        }

        for(int i = firstChapter-1; i < lastChapter; i++) { // -1 since chapter numbers start at 1
            if(killTask) {
                throw new Exception("[GRABBER]Stopped.");
            }
            chapterList.get(i).saveChapter(this);

            if(init.gui != null) {
                init.gui.updateProgress(window);
            }

            GrabberUtils.sleep(waitTime);
        }

        reGrab = true;
    }

    public void writeEpub() {
        EPUB epub = new EPUB(this);
        epub.writeEpub();
    }

    /**
     * Prints potential failed chapters.
     * Reverses the chapter order for next grabbing. (If grabbing was stopped with this option selected)
     * Closes headless driver if used.
     */
    public void report() {
        // Print finishing information
        System.out.println("[GRABBER]Output: " + saveLocation + epubFilename);
        if(init.gui != null) {
            init.gui.appendText(window,"[GRABBER]Finished."); // GUI doesn't need save location displaying
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

        // Close headless browser
        if(useHeadless) headlessDriver.close();
    }

    /**
     * Set CSS selectors from fetched config file for this host.
     */
    void setHostSelectors() {
        JSONObject currentSite = (JSONObject) Config.getInstance().siteSelectorsJSON.get(hostname);
        if(currentSite != null) {
            url = String.valueOf(currentSite.get("url"));
            chapterLinkSelector = String.valueOf(currentSite.get("chapterLinkSelector"));
            chapterContainer = String.valueOf(currentSite.get("chapterContainer"));
            for(Object tagObject: (JSONArray) currentSite.get("blacklistedTags")) {
                blacklistedTags.add(tagObject.toString());
            }
            bookTitleSelector = String.valueOf(currentSite.get("bookTitleSelector"));
            bookDescSelector = String.valueOf(currentSite.get("bookDescSelector"));
            bookCoverSelector = String.valueOf(currentSite.get("bookCoverSelector"));
            bookAuthorSelector = String.valueOf(currentSite.get("bookAuthorSelector"));
            bookSubjectSelector = String.valueOf(currentSite.get("bookSubjectSelector"));
        } else {
            url = "";
        }
    }

    /**
     * Stores all hyperlinks from the given URL and displays them on the gui.
     */
    public void retrieveLinks() {
        init.gui.appendText(window, "Retrieving links from: " + novelLink);
        // Fetch webpage
        if (useHeadless) {
            headlessDriver = new Driver(this);
            headlessDriver.driver.navigate().to(novelLink);
            headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            String baseUrl = headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
            tableOfContent = Jsoup.parse(headlessDriver.driver.getPageSource(), baseUrl);
            headlessDriver.close();
        } else {
            try {
                tableOfContent = Jsoup.connect(novelLink).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        init.gui.manLinkListModel.removeAllElements();
        // Add every link as a new chapter and add to gui
        Elements links = tableOfContent.select("a[href]");
        for (Element chapterLink : links) {
            if (chapterLink.attr("abs:href").startsWith("http") && !chapterLink.text().isEmpty()) {
                Chapter chapter = new Chapter(chapterLink.text(),chapterLink.attr("abs:href"));
                init.gui.manLinkListModel.addElement(chapter);
            }
        }
        init.gui.appendText("manual", "[GRABBER]"+ links.size() + " links retrieved.");
    }

    /**
     * Handles downloading chapters of a provided list.
     */
    public void processChaptersFromList() throws Exception {
        // Add chapters from listModel
        chapterList = new ArrayList<>();
        for (int i = 0; i < init.gui.manLinkListModel.size(); i++) {
            chapterList.add(init.gui.manLinkListModel.get(i));
        }
        if (reverseOrder) Collections.reverse(chapterList);
        if (useHeadless) headlessDriver = new Driver(this);
        init.gui.setMaxProgress(window, chapterList.size());
        for (Chapter chapter : chapterList) {
            if(killTask) {
                throw new Exception("Grabbing stopped.");
            }
            chapter.saveChapter(this);
            init.gui.updateProgress(window);
            GrabberUtils.sleep(waitTime);
        }

        if (useHeadless) headlessDriver.close();
    }

    /**
     * Follows the chapters via a "next chapter button".
     */
    public void processChaptersToChapters(String[] args) throws Exception {
        init.gui.appendText(window, "[GRABBER]Connecting...");
        init.gui.setMaxProgress(window, 9001);

        nextChapterURL = args[0];
        String lastChapterURL = args[1];
        nextChapterBtn = args[2];
        int chapterNumber = GUI.chapterToChapterNumber;

        if (useHeadless) headlessDriver = new Driver(this);

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
                nextChapterBtn = "NOT_SET";
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
     * Set metadata specified in metadata GUI dialog
     */
    public void manSetMetadata() {
        if (manSetMetadata.manMetadataInfo[0] != null && !manSetMetadata.manMetadataInfo[0].isEmpty()) {
            bookTitle = manSetMetadata.manMetadataInfo[0];
        } else bookTitle = "Unknown";
        if (manSetMetadata.manMetadataInfo[1] != null && !manSetMetadata.manMetadataInfo[1].isEmpty()) {
            bookAuthor = manSetMetadata.manMetadataInfo[1];
        } else bookAuthor = "Unknown";
        if (manSetMetadata.manMetadataInfo[2] != null && !manSetMetadata.manMetadataInfo[2].isEmpty()) {
            bookCover = manSetMetadata.manMetadataInfo[2];
        }
        if (manSetMetadata.manMetadataInfo[3] != null && !manSetMetadata.manMetadataInfo[3].isEmpty()) {
            bookDesc = manSetMetadata.manMetadataInfo[3];
        } else {
            bookDesc = "";
        }
        if (manSetMetadata.manMetadataTags != null && !manSetMetadata.manMetadataTags.isEmpty()) {
            bookSubjects = manSetMetadata.manMetadataTags;
        }
        noDescription = manSetMetadata.noDescription;
    }

    /**
     * Resets GUI for current grabbing.
     * Sets various metadata for this novel.
     */
    public void getMetadata() {
        if(window.equals("auto") || window.equals("checker")) {
            if(init.gui != null) {
                init.gui.autoBookTitle.setText("");
                init.gui.autoAuthor.setText("");
                init.gui.autoChapterAmount.setText("");
                init.gui.setBufferedCover(null);
                init.gui.autoBookSubjects.setText("");
            }
            setTitle();
            setDesc();
            setAuthor();
            setTags();
            setChapterNumber();
            setCover();
        }
        if(window.equals("manual")) {
            manSetMetadata();
        }
    }

    /**
     * Will try to fetch the book title via host selectors or set it as "Unknown".
     * Removes special characters: regex: [\\/:*?"<>|]
     * Updates GUI with title.
     */
    void setTitle() {
        if (tableOfContent.select(bookTitleSelector) != null && !tableOfContent.select(bookTitleSelector).isEmpty()) {
            bookTitle = tableOfContent.select(bookTitleSelector).first().text();
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoBookTitle.setText(bookTitle);
            }
        } else {
            bookTitle = "Unknown";
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoBookTitle.setText("Unknown");
            }
        }
    }

    /**
     * Sets the book description via host selector.
     * Updates GUI with description.
     */
    void setDesc() {
        if (tableOfContent.select(bookDescSelector) != null && !tableOfContent.select(bookDescSelector).isEmpty()) {
            bookDesc = tableOfContent.select(bookDescSelector).first().text();
        } else {
            bookDesc = "";
        }
    }

    /**
     * Sets the book auhtor via host selector.
     * Updates GUI with author.
     */
    void setAuthor() {
        if (tableOfContent.select(bookAuthorSelector) != null && !tableOfContent.select(bookAuthorSelector).isEmpty()) {
            bookAuthor = tableOfContent.select(bookAuthorSelector).first().text();
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoAuthor.setText(bookAuthor);
            }
        } else {
            bookAuthor = "Unknown";
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoAuthor.setText("Unknown");
            }
        }
    }
    /**
     * Sets the book tags via host selector.
     * Updates GUI with tags.
     */
    void setTags() {
        if (tableOfContent.select(bookSubjectSelector) != null && !tableOfContent.select(bookSubjectSelector).isEmpty()) {
            Elements tags = tableOfContent.select(bookSubjectSelector);
            for (Element tag : tags) {
                bookSubjects.add(tag.text());
            }

            // Display book subjects on GUI
            int maxNumberOfSubjects = 0;
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoBookSubjects.setText("<html>");
                for (String eachTag : bookSubjects) {
                    init.gui.autoBookSubjects.setText(init.gui.autoBookSubjects.getText() + eachTag + ", ");
                    maxNumberOfSubjects++;
                    if (maxNumberOfSubjects == 4) {
                        maxNumberOfSubjects = 0;
                        init.gui.autoBookSubjects.setText(init.gui.autoBookSubjects.getText() + "<br>");
                    }
                }
                if (!init.gui.autoBookSubjects.getText().isEmpty()) {
                    init.gui.autoBookSubjects.setText(
                            init.gui.autoBookSubjects.getText().substring(0,
                                    init.gui.autoBookSubjects.getText().lastIndexOf(",")));
                }
            }
        } else {
            bookSubjects.add("Unknown");
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoBookSubjects.setText("Unknown");
            }
        }
    }

    /**
     * Updates GUI with chapter numbers.
     */
    void setChapterNumber() {
        if (!chapterList.isEmpty()) {
            if(init.gui != null && window.equals("auto")) {
                init.gui.autoChapterAmount.setText(String.valueOf(chapterList.size()));
                init.gui.autoGetNumberButton.setEnabled(true);
            }
        }
    }

    /**
     * Will set the book cover as a BufferedCover via host selectors.
     * Updates GUI with fetched cover.
     */
    void setCover() {
        Element coverSelect = tableOfContent.select(bookCoverSelector).first();
        if (coverSelect != null) {
            String coverLink = coverSelect.attr("abs:src");
            // Custom
            if (url.equals("https://wordexcerpt.com/")) {
                coverLink = coverSelect.attr("style");
                coverLink = coverLink.substring(coverLink.indexOf("'")+1, coverLink.lastIndexOf("'"));
            }
            if (url.equals("https://webnovel.com/")) {
                coverLink = coverLink.replace("/300/300", "/600/600");
            }
            if (url.equals("https://dreame.com/") || url.equals("https://ficfun.com/") ) {
                coverLink = coverSelect.select(".js-cover.img").attr("abs:data-cover");
            }
            if (url.equals("https://mtlnovel.com/")) {
                coverLink = coverLink.substring(0, coverLink.indexOf(".webp"));
            }
            if (url.equals("https://www.inkitt.com/")) {
                String backgroundimageUrl = coverSelect.select(".story-horizontal-cover__front").attr("style");
                coverLink = backgroundimageUrl.substring(backgroundimageUrl.indexOf("https://"), backgroundimageUrl.indexOf(")")-1);
            }
            if (url.equals("https://foxaholic.com/") || url.equals("https://wordrain69.com/") ) {
                coverLink = coverSelect.attr("abs:data-src");
            }
            try {
                bufferedCover = GrabberUtils.getImage(coverLink);
                bookCover = GrabberUtils.getFilenameFromUrl(coverLink);
                if(init.gui != null && window.equals("auto")) {
                    init.gui.setBufferedCover(bufferedCover);
                }
            } catch (IOException e) {
                try {
                    bufferedCover = ImageIO.read(getClass().getResource("/images/cover_placeholder.png"));
                    bookCover = "cover_placeholder.png";
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        } else {
            try {
                bufferedCover = ImageIO.read(getClass().getResource("/images/cover_placeholder.png"));
                bookCover = "cover_placeholder.png";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}