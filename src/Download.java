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
    List<String> images = new ArrayList<>();
    List<String> blacklistedTags;
    String window;
    String saveLocation;
    String tocFileName;
    String chapterContainer;
    boolean getImages;
    boolean chapterNumeration;
    boolean allChapters;
    boolean invertOrder;
    long startTime = System.nanoTime();
    int firstChapter;
    int lastChapter;
    String nextChapterURL;
    String nextChapterBtn = "NOT_SET";

    Download() {
        // Settings
        this.saveLocation = NovelGrabberGUI.saveLocation.getText();
        this.chapterNumeration = NovelGrabberGUI.useNumeration.isSelected();
        this.allChapters = NovelGrabberGUI.chapterAllCheckBox.isSelected();
        this.invertOrder = NovelGrabberGUI.checkInvertOrder.isSelected();
        this.window = "auto";
        if (!NovelGrabberGUI.chapterAllCheckBox.isSelected()) {
            this.firstChapter = Integer.parseInt(NovelGrabberGUI.firstChapter.getText());
            if (!NovelGrabberGUI.toLastChapter.isSelected()) {
                this.lastChapter = Integer.parseInt(NovelGrabberGUI.lastChapter.getText());
            }
        }
        this.getImages = NovelGrabberGUI.getImages.isSelected();
        String tocUrl = NovelGrabberGUI.chapterListURL.getText();
        String host = Objects.requireNonNull(NovelGrabberGUI.allChapterHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", "");
        this.currHostSettings = new HostSettings(host, tocUrl);
        this.blacklistedTags = currHostSettings.blacklistedTags;

        // Functions
        autoFetchChapters.grabChapters(this);
        if (NovelGrabberGUI.createTocCheckBox.isSelected()) Shared.createToc(this);
        Shared.report(this);

    }

    Download(String method) {
        // Settings
        this.chapterContainer = NovelGrabberGUI.manChapterContainer.getText();
        this.saveLocation = NovelGrabberGUI.manSaveLocation.getText();
        this.chapterNumeration = NovelGrabberGUI.manUseNumeration.isSelected();
        this.invertOrder = NovelGrabberGUI.manCheckInvertOrder.isSelected();
        this.getImages = NovelGrabberGUI.manGetImages.isSelected();
        this.blacklistedTags = NovelGrabberGUI.blacklistedTags;
        this.window = "manual";
        this.tocFileName = "Table of Contents";

        // Functions
        switch (method) {
            case "chapterToChapter":
                manFetchChapters.processChaptersToChapters(NovelGrabberGUI.chapterToChapterArgs, this);
                break;
            case "chaptersFromList":
                manFetchChapters.processChapersFromList(this);
                break;
        }
        if (NovelGrabberGUI.manCreateToc.isSelected()) Shared.createToc(this);
        Shared.report(this);
    }
}
