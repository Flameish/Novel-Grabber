import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Download {
    HostSettings currHostSettings;
    List<String> chapterLinks = new ArrayList<>();
    List<String> chaptersNames = new ArrayList<>();
    List<String> failedChapters = new ArrayList<>();
    List<String> successfulChapterNames = new ArrayList<>();
    List<String> successfulFilenames = new ArrayList<>();
    List<String> imageLinks = new ArrayList<>();
    List<String> imageNames = new ArrayList<>();
    List<String> blacklistedTags;
    String window;
    String saveLocation;
    String tocFileName;
    String chapterContainer;
    boolean getImages;
    boolean allChapters;
    boolean invertOrder;
    long startTime = System.nanoTime();
    int firstChapter;
    int lastChapter;
    String nextChapterURL;
    String nextChapterBtn = "NOT_SET";

    //Metadata
    String bookTitle;
    String bookCover;
    String bookAuthor;

    Download() {
        // Settings
        saveLocation = NovelGrabberGUI.saveLocation.getText();
        allChapters = NovelGrabberGUI.chapterAllCheckBox.isSelected();
        invertOrder = NovelGrabberGUI.checkInvertOrder.isSelected();
        window = "auto";
        if (!NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            firstChapter = Integer.parseInt(NovelGrabberGUI.firstChapter.getText());
            if (!NovelGrabberGUI.toLastChapter.isSelected()) {
                lastChapter = Integer.parseInt(NovelGrabberGUI.lastChapter.getText());
            }
        }
        getImages = NovelGrabberGUI.getImages.isSelected();
        String tocUrl = NovelGrabberGUI.chapterListURL.getText();
        String host = Objects.requireNonNull(NovelGrabberGUI.allChapterHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");

        // Create HostSettings
        currHostSettings = new HostSettings(host, tocUrl);
        blacklistedTags = currHostSettings.blacklistedTags;

        // Functions
        autoFetchChapters.grabChapters(this);
        Shared.report(this);
        if (!successfulFilenames.isEmpty()) {
            switch ((String) NovelGrabberGUI.exportSelection.getSelectedItem()) {
                case "Calibre":
                    Shared.createToc(this);
                    break;
                case "EPUB":
                    ToEpub epub = new ToEpub(this);
                    break;
            }
        }
    }

    Download(String method) {
        // Settings
        chapterContainer = NovelGrabberGUI.manChapterContainer.getText();
        saveLocation = NovelGrabberGUI.manSaveLocation.getText();
        invertOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
        getImages = NovelGrabberGUI.manGetImages.isSelected();
        blacklistedTags = NovelGrabberGUI.blacklistedTags;
        window = "manual";
        tocFileName = "Table of Contents";

        // Functions
        switch (method) {
            case "chapterToChapter":
                manFetchChapters.processChaptersToChapters(NovelGrabberGUI.chapterToChapterArgs, this);
                break;
            case "chaptersFromList":
                manFetchChapters.processChapersFromList(this);
                break;
        }
        Shared.report(this);
        manFetchChapters.manGetMetadata(this);
        if (!successfulFilenames.isEmpty()) {
            switch ((String) NovelGrabberGUI.manExportSelection.getSelectedItem()) {
                case "CALIBRE":
                    Shared.createToc(this);
                    break;
                case "EPUB":
                    ToEpub epub = new ToEpub(this);
                    break;
            }
        }
    }
}
