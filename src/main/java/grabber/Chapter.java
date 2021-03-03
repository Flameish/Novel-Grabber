package grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import system.init;
import org.jsoup.nodes.Element;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Chapter implements Serializable {
    private static int chapterId = 0;  // Used to set unique filenames
    public Element chapterContainer;
    public String chapterContent;
    public String name;
    public String chapterURL;
    public String fileName;
    public int status = 0; // 0 = not downloaded, 1 = successfully downloaded, 2 = failed download

    public Chapter(String name, String link) {
        this.name = name;
        this.chapterURL = link;
        fileName = String.format("%05d", ++chapterId) + "-" + name.replaceAll("[^\\w]+", "-");
    }

    /**
     * Fetches chapter content from host.
     * Modifies content of chapter based on selected options.
     * Downloads images if selected.
     * Cleans broken HTML
     * Updates page count on GUI.
     * Adds html header/footer to chapter text.
     */
    public void saveChapter(Novel novel) {
        chapterContainer = novel.source.getChapterContent(this);
        if (chapterContainer == null) {
            GrabberUtils.err(novel.window, "Chapter container not found.");
            status = 2; // Chapter was NOT downloaded
            return;
        }

        removeUnwantedTags(novel.removeStyling, novel.blacklistedTags);

        if (novel.getImages) {
            getImages(novel.window, novel.images);
        } else {  // Remove <img> tags.
            // Images would be loaded from the internet from the original links inside the eReader if left unchanged
            chapterContainer.select("img").remove();
        }

        novel.wordCount = novel.wordCount + GrabberUtils.getWordCount(chapterContainer.toString());
        GrabberUtils.info(novel.window,  "Saved chapter: "+ name);
        if(init.gui != null) {
            init.gui.updatePageCount(novel.window, novel.wordCount);
        }

        chapterContent = cleanContent(chapterContainer, novel.displayChapterTitle);
        chapterContainer = null;
        status = 1; // Chapter was successfully downloaded
    }

    /**
     * Removes general and specified blacklisted tags from chapter body.
     */
    private void removeUnwantedTags(boolean removeStyling, List<String> blacklistedTags) {
        // Remove user set blacklisted tags
        for (String tag : blacklistedTags) {
            chapterContainer.select(tag).remove();
        }
        // Remove empty block elements
        for (Element element : chapterContainer.select("*")) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
        // Try to remove navigation links
        String[] blacklistedWords = new String[] {"next","previous","table","index","back","chapter","home"};
        for(Element link: chapterContainer.select("a[href]")) {
            if(Arrays.stream(blacklistedWords).anyMatch(link.text().toLowerCase()::contains)) link.remove();
        }
        if (removeStyling) {
            chapterContainer.select("[style]").removeAttr("style");
        }
    }


    /**
     * Saves images with filenames in HashMap
     */
    private void getImages(String window, HashMap<String, BufferedImage> images) {
        for (Element image : chapterContainer.select("img")) {
            String imageURL = image.absUrl("src");
            String imageFilename = GrabberUtils.getFilenameFromUrl(imageURL);
            BufferedImage bufferedImage = GrabberUtils.getImage(imageURL);

            if(imageFilename != null && bufferedImage != null) {
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
     * Cleans HTML tags and adds chapter title optionally
     */
    private String cleanContent(Element chapterContainer, boolean displayChapterTitle) {
        String chapterString = chapterContainer.toString();

        Document.OutputSettings settings = new Document.OutputSettings();
        settings.syntax(Document.OutputSettings.Syntax.xml);
        settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        settings.charset("UTF-8");

        chapterString = Jsoup.clean(
                chapterString,
                "http://"+GrabberUtils.getDomainName(chapterURL),
                Whitelist.relaxed().preserveRelativeLinks(true),
                settings);

        if(displayChapterTitle) {
            chapterString = "<span style=\"font-weight: 700; text-decoration: underline;\">" + name + "</span>\n" + chapterString;
        }
        return chapterString;
    }

    @Override
    public String toString() {
        return name;
    }

}