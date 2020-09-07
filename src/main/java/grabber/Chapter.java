package grabber;

import grabber.scripts.ChapterContentScripts;
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

/**
 * Stores chapter information.
 * Saves chapter as HTML file.
 */
public class Chapter implements Serializable {
    private static int chapterId = 0;  // Used to set unique filenames
    public Element chapterContainer;
    public String chapterContent;
    public Document doc;
    public String name;
    public String chapterURL;
    public String fileName;
    public String xhrChapterId;
    public int status = 0; // 0 = not downloaded, 1 = successfully downloaded, 2 = failed download
    public int xhrBookId;

    public Chapter(String name, String link) {
        this.name = name;
        this.chapterURL = link;
        fileName = String.format("%05d", ++chapterId) + "-" + name.replaceAll("[^\\w]+", "-");
    }

    /**
     * Fetches chapter content from host.
     * Modifies content of chapter based on selected options.
     * Downloads images if selected.
     * Updates page count on GUI.
     * Adds html header/footer to chapter text.
     * @param novel
     */
    public void saveChapter(Novel novel) {
        ChapterContentScripts.fetchContent(novel, this);

        // Check for empty content
        if (chapterContainer == null) {
            if(init.gui != null) {
                init.gui.appendText(novel.window,
                        "[GRABBER]Chapter container (" + novel.chapterContainer + ") not found.");
            }
            System.out.println("[GRABBER]Chapter container (" + novel.chapterContainer + ") not found.");
            status = 2;
            return;
        }

        // Get the next chapter URL from the "nextChapterBtn" href for Chapter-To-Chapter grabbing.
        if(novel.window.equals("manual")) {
            if (!novel.nextChapterBtn.equals("NOT_SET")) {
                novel.nextChapterURL = doc.select(novel.nextChapterBtn).first().absUrl("href");
            }
        }
        removeUnwantedTags(novel);

        if (novel.getImages) {
            getImages(novel);
        // Remove <img> tags.
        // Images would be loaded from the host via original href links on the eReader if left in.
        } else {
            chapterContainer.select("img").remove();
        }

        if (novel.displayChapterTitle) {
           chapterContainer.prepend(
                   "<span style=\"font-weight: 700; text-decoration: underline;\">" + name + "</span><br>" + EPUB.NL);
        }
        chapterContainer.prepend(EPUB.htmlHead);
        chapterContainer.append( EPUB.NL+EPUB.htmlFoot);

        cleanHtml();

        updatePageCount(novel);

        if(init.gui != null && !novel.window.equals("checker")) {
            init.gui.appendText(novel.window, "[GRABBER]Saved chapter: "+ name);
        }
        System.out.println("[GRABBER]Saved chapter: "+ name);
        status = 1;
        // Improve GC
        chapterContent = chapterContainer.toString();
        doc = null;
        chapterContainer = null;
    }

    /**
     * Removes general and specified blacklisted tags from chapter body.
     * @param novel
     */
    private void removeUnwantedTags(Novel novel) {
        // Always remove <script>
        chapterContainer.select("script").remove();
        chapterContainer.select("style").remove();
        // Try to remove navigation links
        String[] blacklistedWords = new String[] {"next","previous","table","index","back","chapter","home"};
        for(Element link: chapterContainer.select("a[href]")) {
            if(Arrays.stream(blacklistedWords).anyMatch(link.text().toLowerCase()::contains)) link.remove();
        }
        if (novel.removeStyling) {
            chapterContainer.select("[style]").removeAttr("style");
        }

        if (novel.blacklistedTags != null && !novel.blacklistedTags.isEmpty()) {
            for (String tag : novel.blacklistedTags) {
                if (!chapterContainer.select(tag).isEmpty()) {
                    chapterContainer.select(tag).remove();
                }
            }
        }
    }

    /**
     * Updates word counter of novel and page counter on GUI.
     * (300 words per page)
     * @param novel
     */
    private void updatePageCount(Novel novel) {
        novel.wordCount = novel.wordCount + GrabberUtils.getWordCount(chapterContainer.toString());
        if(init.gui != null && !novel.window.equals("checker")) {
            init.gui.pagesCountLbl.setText(String.valueOf(novel.wordCount / 300));
        }
    }

    /**
     * Saves images w/ filenames in HashMap
     * @param novel
     */
    private void getImages(Novel novel) {
        for (Element image : chapterContainer.select("img")) {
            try {
                String imageURL = image.absUrl("src");
                String imageFilename = GrabberUtils.getFilenameFromUrl(imageURL);
                BufferedImage bufferedImage = GrabberUtils.getImage(imageURL);
                if(imageFilename != null && bufferedImage != null) {
                    novel.images.put(imageFilename, bufferedImage);
                    image.attr("src", imageFilename);
                    if(init.gui != null && !novel.window.equals("checker")) {
                        init.gui.appendText(novel.window, "[CHAPTER]Saved image: "+ imageFilename);
                    }
                    System.out.println("[CHAPTER]Saved image: "+ imageFilename);
                } else {
                    image.remove();
                    if(init.gui != null && !novel.window.equals("checker")) {
                        init.gui.appendText(novel.window, "[CHAPTER-ERROR]Could not save image: "+ imageFilename);
                    }
                    System.out.println("[CHAPTER-ERROR]Could not save image: "+ imageFilename);
                }
            } catch (IOException e) {
                image.remove();
                e.printStackTrace();
            }
        }
    }

    private void cleanHtml() {
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

    }

    @Override
    public String toString() {
            return name;
    }

}