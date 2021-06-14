package grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import system.Config;
import system.init;
import org.jsoup.nodes.Element;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Chapter implements Serializable {
    public static int chapterCounter = 0;  // Used to set unique filenames
    public Element chapterContainer;
    public String chapterContent;
    public String name;
    public String chapterURL;
    public String fileName;
    public int status = 0; // 0 = not downloaded, 1 = successfully downloaded, 2 = failed download

    public Chapter(String name, String link) {
        this.name = name.trim();
        this.chapterURL = link;
        fileName = String.format("%05d", ++chapterCounter) + "-" + this.name.replaceAll("[^\\w]+", "-");
    }

    /**
     * Fetches chapter content from host.
     * Modifies content of chapter based on selected options.
     * Downloads images if selected.
     * Cleans broken HTML
     * Updates page count on GUI.
     */
    public void saveChapter(Novel novel) {
        chapterContainer = novel.source.getChapterContent(this);
        if (chapterContainer == null) {
            GrabberUtils.err(novel.window, "Chapter container not found.");
            status = 2; // Chapter was NOT downloaded
            return;
        }

        removeUnwantedTags(novel.blacklistedTags);

        if (novel.getImages) {
            getImages(novel.window, novel.images);
        } else {  // Remove <img> tags.
            // Images would be loaded from the internet from the original links inside the eReader if left unchanged
            chapterContainer.select("img").remove();
        }

        if(novel.displayChapterTitle) addTitle();
        cleanHTMLContent();
        // Update word count on GUI
        novel.wordCount = novel.wordCount + GrabberUtils.getWordCount(chapterContainer.toString());
        GrabberUtils.info(novel.window,  "Saved chapter: "+ name);
        if(init.gui != null) {
            init.gui.updatePageCount(novel.window, novel.wordCount);
        }

        chapterContainer = null;
        status = 1; // Chapter was successfully downloaded

    }

    /**
     * Removes general and specified blacklisted tags from chapter body.
     */
    private void removeUnwantedTags(List<String> blacklistedTags) {
        // Remove user set blacklisted tags
        for (String tag : blacklistedTags) {
            chapterContainer.select(tag).remove();
        }
        // Remove empty block elements
        for (Element element : chapterContainer.select("*:not(:has(img))")) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
        // Try to remove navigation links
        String[] blacklistedWords = new String[] {"next","previous","table","index","back","chapter","home"};
        for(Element link: chapterContainer.select("a[href]")) {
            if(Arrays.stream(blacklistedWords).anyMatch(link.text().toLowerCase()::contains)) link.remove();
        }
    }

    /**
     * Saves images with filenames in HashMap and points img src to local file
     */
    private void getImages(String window, HashMap<String, BufferedImage> images) {
        for (Element image : chapterContainer.select("img")) {
            String imageURL = image.absUrl("src");
            String imageFilename = GrabberUtils.getFilenameFromUrl(imageURL);
            BufferedImage bufferedImage = GrabberUtils.getImage(imageURL);

            if(bufferedImage != null) {
                // Sometimes image names can be empty. Create random name for it then.
                if (imageFilename == null || imageFilename.isEmpty()) {
                    imageFilename = UUID.randomUUID().toString();
                }
                // Check if image has file extension. If not set as png.
                if(GrabberUtils.getFileExtension(imageFilename) == null) imageFilename += ".png";
                // Modify href of image src to downloaded image
                image.attr("src", imageFilename);

                images.put(imageFilename, bufferedImage);

                GrabberUtils.info(window,"Saved image: " + imageFilename);
            } else {
                image.remove();
                GrabberUtils.err(window,"Could not save image: " + imageFilename);
            }
        }
    }

    /**
     * Cleans chapter HTML to
     */
    private void cleanHTMLContent() {
        Document.OutputSettings settings = new Document.OutputSettings();
        settings.syntax(Document.OutputSettings.Syntax.xml);
        settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        settings.charset("UTF-8");
        chapterContent = Jsoup.clean(chapterContainer.toString(),
                "http://"+GrabberUtils.getDomainName(chapterURL),
                Whitelist.relaxed().preserveRelativeLinks(true),
                settings);
    }

    /**
     * Adds the chapter title on top of chapter body according to format setting
     */
    private void addTitle() {
        Config config = Config.getInstance();
        String chapterTitle;
        switch (config.getChapterTitleFormat()) {
            case 1:
                chapterTitle = "<h1>" + name + "</h1>\n";
                break;
            case 2: // Custom
                chapterTitle = String.format(config.getChapterTitleTemplate() + "\n", name);
                break;
            default:
                chapterTitle = "<span><b><u>" + name + "</u></b></span>\n";
        }
        chapterContainer.child(0).before(chapterTitle);
    }

    @Override
    public String toString() {
        return name;
    }

}