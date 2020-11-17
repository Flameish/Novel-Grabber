package grabber;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Entities;
import system.init;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
            System.err.println("[ERROR]Chapter container not found.");
            if(init.gui != null) {
                init.gui.appendText(novel.window,
                        "[ERROR]Chapter container not found.");
            }
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

        fixHTML(novel.displayChapterTitle);

        novel.wordCount = novel.wordCount + GrabberUtils.getWordCount(chapterContainer.toString());
        System.out.println("[GRABBER]Saved chapter: "+ name);
        if(init.gui != null) {
            init.gui.appendText(novel.window, "[GRABBER]Saved chapter: "+ name);
            init.gui.updatePageCount(novel.window, novel.wordCount);
        }

        chapterContent = chapterContainer.toString();
        chapterContainer = null;
        status = 1; // Chapter was successfully downloaded
    }

    /**
     * Removes general and specified blacklisted tags from chapter body.
     */
    private void removeUnwantedTags(boolean removeStyling, List<String> blacklistedTags) {
        // Always remove <script>
        chapterContainer.select("script").remove();
        chapterContainer.select("style").remove();
        // Try to remove navigation links
        String[] blacklistedWords = new String[] {"next","previous","table","index","back","chapter","home"};
        for(Element link: chapterContainer.select("a[href]")) {
            if(Arrays.stream(blacklistedWords).anyMatch(link.text().toLowerCase()::contains)) link.remove();
        }
        if (removeStyling) {
            chapterContainer.select("[style]").removeAttr("style");
        }

        for (String tag : blacklistedTags) {
            chapterContainer.select(tag).remove();
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

                System.out.println("[CHAPTER] Saved image: " + imageFilename);
                if(init.gui != null) {
                    init.gui.appendText(window, "[CHAPTER] Saved image: " + imageFilename);
                }
            } else {
                image.remove();
                System.err.println("[ERROR] Could not save image: " + imageFilename);
                if(init.gui != null) {
                    init.gui.appendText(window, "[ERROR] Could not save image: " + imageFilename);
                }
            }
        }
    }

    /**
     * Cleans HTML tags and adds header & footer
     * Add chapter title optionally
     */
    private void fixHTML(boolean displayChapterTitle) {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setTranslateSpecialEntities(true);
        props.setTransResCharsToNCR(true);
        props.setTransSpecialEntitiesToNCR(true);
        props.setOmitComments(true);

        Document.OutputSettings outputSettings = new Document.OutputSettings();
        outputSettings.syntax(Document.OutputSettings.Syntax.xml);
        outputSettings.escapeMode(Entities.EscapeMode.xhtml);

        String chapter = chapterContainer.toString().replaceAll("<br>", "\n");
        TagNode tagNode = cleaner.clean(chapter);

        String html = "<" + tagNode.getName() + ">" + cleaner.getInnerHtml(tagNode) + "</" + tagNode.getName() + ">";
        chapterContainer = Jsoup.parse(html).outputSettings(outputSettings);
        if (displayChapterTitle) {
            chapterContainer.prepend(
                    "<span style=\"font-weight: 700; text-decoration: underline;\">" + name + "</span><br>" + EPUB.NL);
        }
        chapterContainer.prepend(EPUB.htmlHead);
        chapterContainer.append(EPUB.NL+EPUB.htmlFoot);

    }

    @Override
    public String toString() {
            return name;
    }

}