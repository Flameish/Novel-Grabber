package grabber;

import gui.GUI;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Download {
    public List<String> chapterLinks = new ArrayList<>();
    public HostSettings currHostSettings;
    public BufferedImage bufferedCover;
    public String bufferedCoverName;
    public Document tocDoc;
    public GUI gui;

    List<String> chaptersNames = new ArrayList<>();
    List<String> failedChapters = new ArrayList<>();
    List<String> successfulChapterNames = new ArrayList<>();
    List<String> successfulFilenames = new ArrayList<>();
    public boolean noDescription = false;

    List<String> imageLinks = new ArrayList<>();
    List<String> imageNames = new ArrayList<>();
    List<String> blacklistedTags;
    String export;
    String window;
    String saveLocation;
    String tocFileName;
    String chapterContainer;
    public List<String> bookDesc = new ArrayList<>();
    List<String> xhrChapterIds = new ArrayList<>();
    boolean getImages;
    boolean allChapters;
    boolean invertOrder;
    int xhrBookId;
    List<String> successfulExtraPagesNames = new ArrayList<>();
    public boolean autoChapterToChapter;
    public long startTime;
    int firstChapter;
    int lastChapter;
    int waitTime;
    List<String> successfulExtraPagesFilenames = new ArrayList<>();
    String nextChapterURL;
    String nextChapterBtn = "NOT_SET";
    boolean useHeaderlessBrowser;
    boolean displayChapterTitle;

    //Metadata
    public String bookTitle;
    public String bookAuthor;
    public List<String> bookSubjects = new ArrayList<>();
    public String bookCover;
    int chapterToChapterNumber;
    int wordCount = 0;

    // Automatic
    public Download(GUI myGUI) {
        // Settings
        gui = myGUI;
        String tocUrl = gui.chapterListURL.getText();
        String host = Objects.requireNonNull(gui.autoHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");
        useHeaderlessBrowser = gui.useHeaderlessBrowserCheckBox.isSelected();
        window = "auto";
        // Create grabber.HostSettings
        currHostSettings = new HostSettings(host, tocUrl);
        blacklistedTags = currHostSettings.blacklistedTags;
        // Functions
        if (HostSettings.autoChapterToChapterWebsitesList.contains(gui.autoHostSelection.getSelectedItem().toString())) {
            autoChapterToChapter = true;
        }
        autoFetchChapters.getChapters(this);
        autoFetchChapters.getMetadata(this);
    }

    // Manual
    public Download(GUI myGUI, String method) {
        // Settings
        startTime = System.nanoTime();
        gui = myGUI;
        chapterContainer = gui.manChapterContainer.getText();
        saveLocation = gui.manSaveLocation.getText();
        invertOrder = gui.manInvertOrder.isSelected();
        getImages = gui.manGetImages.isSelected();
        blacklistedTags = GUI.blacklistedTags;
        waitTime = Integer.parseInt(gui.manWaitTime.getText());
        window = "manual";
        tocFileName = "Table of Contents";
        export = gui.manExportSelection.getSelectedItem().toString();
        displayChapterTitle = gui.manDispalyChapterTitleCheckbox.isSelected();
        bookDesc.add(0, "");
        manFetchChapters.killTask = false;
        // Functions
        switch (method) {
            case "chapterToChapter":
                manFetchChapters.processChaptersToChapters(GUI.chapterToChapterArgs, this);
                break;
            case "chaptersFromList":
                manFetchChapters.processChapersFromList(this);
                break;
        }
        manFetchChapters.manGetMetadata(this);
        if (!successfulFilenames.isEmpty() && !manFetchChapters.killTask) {
            switch ((String) myGUI.manExportSelection.getSelectedItem()) {
                case "Calibre":
                    Shared.createToc(this);
                    break;
                case "EPUB":
                    Shared.createCoverPage(this);
                    Shared.createToc(this);
                    if (!bookDesc.get(0).isEmpty() && !noDescription) Shared.createDescPage(this);
                    ToEpub epub = new ToEpub(this);
                    break;
            }
            Shared.report(this);
        }
    }

    public void startAutoDownload() {
        startTime = System.nanoTime();
        saveLocation = gui.saveLocation.getText();
        export = gui.exportSelection.getSelectedItem().toString();
        waitTime = Integer.parseInt(gui.waitTime.getText());
        allChapters = gui.chapterAllCheckBox.isSelected();
        invertOrder = gui.checkInvertOrder.isSelected();
        useHeaderlessBrowser = gui.useHeaderlessBrowserCheckBox.isSelected();
        displayChapterTitle = gui.displayChapterTitleCheckBox.isSelected();
        if (!gui.autoChapterToChapterNumberField.getText().equals("Number")) {
            chapterToChapterNumber = Integer.valueOf(gui.autoChapterToChapterNumberField.getText());
        } else {
            chapterToChapterNumber = 1;
        }
        if (!gui.chapterAllCheckBox.isSelected()) {
            firstChapter = (Integer) gui.firstChapter.getValue();
            if (!gui.toLastChapter.isSelected()) {
                lastChapter = (Integer) gui.lastChapter.getValue();
            }
        }
        getImages = gui.getImages.isSelected();
        // Write buffered cover to save location
        if (bufferedCover != null && bookCover != null) {
            try {
                File outputfile = new File(saveLocation + File.separator + "images" + File.separator + bufferedCoverName);
                if (!outputfile.exists()) outputfile.mkdirs();
                ImageIO.write(bufferedCover, bufferedCoverName.substring(bufferedCoverName.lastIndexOf(".") + 1), outputfile);
            } catch (IOException e) {
                gui.appendText(window, "[ERROR]Could not write cover image to file.");
            }
        }
        // Getting the chapters again if it was stopped previously
        if (autoFetchChapters.killTask) autoFetchChapters.getChapters(this);

        autoFetchChapters.killTask = false;
        if (autoChapterToChapter && !gui.useHeaderlessBrowserCheckBox.isSelected()) {
            String[] chapterInfo = {
                    gui.autoFirstChapterURL.getText(),
                    gui.autoLastChapterURL.getText(),
                    currHostSettings.nextChapterBtn
            };
            chapterContainer = currHostSettings.chapterContainer;
            autoFetchChapters.processChaptersToChapters(chapterInfo, this);
        } else {
            autoFetchChapters.downloadChapters(this);
        }
        if (!successfulFilenames.isEmpty() && !autoFetchChapters.killTask) {
            switch ((String) gui.exportSelection.getSelectedItem()) {
                case "Calibre":
                    Shared.createToc(this);
                    break;
                case "EPUB":
                    Shared.createCoverPage(this);
                    Shared.createToc(this);
                    if (!bookDesc.get(0).isEmpty() && !noDescription) Shared.createDescPage(this);
                    ToEpub epub = new ToEpub(this);
                    break;
            }
            Shared.report(this);
        }
    }
}
