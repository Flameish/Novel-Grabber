package grabber.novel;

import grabber.helper.Utils;
import org.jsoup.nodes.Element;
import system.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class Chapter {
    public enum Status {
        NOT_DOWNLOADED,
        SUCCESS,
        FAILED,
        DENIED
    }
    private Element chapterBody;
    private String name;
    private String url;
    private Status downloadStatus = Status.NOT_DOWNLOADED;

    public Chapter(String name, String url) {
        this.name = name.trim();
        this.url = url;
    }

    public void setChapterBody(Element chapterContent) {
        this.chapterBody = chapterContent;
    }

    /**
     * Removes general and specified blacklisted HTML tags from chapter body.
     */
    public void removeUnwantedTags(List<String> blacklistedTags) {
        // Remove user set blacklisted tags
        for (String tag : blacklistedTags) {
            chapterBody.select(tag).remove();
        }
        // Remove empty block elements
        for (Element element : chapterBody.select("*:not(:has(img))")) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
        // Try to remove navigation links
        String[] blacklistedWords = new String[] {"next","previous","table","index","back","chapter","home"};
        for(Element link: chapterBody.select("a[href]")) {
            if(Arrays.stream(blacklistedWords).anyMatch(link.text().toLowerCase()::contains)) link.remove();
        }
    }

    /**
     * Downloads images and changes HTML image-src in chapter.
     * Removes image tags for images which couldn't be downloaded.
     */
    public void downloadImages(Map<String, byte[]> imagesByFilename) {
        for (Element imageElement : chapterBody.select("img")) {
            String imageUrl = imageElement.absUrl("src");
            try {
                String imageFilename = Utils.getFilenameFromUrl(imageUrl);
                // Check if image was already downloaded
                if (!imagesByFilename.containsKey(imageFilename)) {
                    imagesByFilename.put(imageFilename, Utils.downloadImage(imageUrl));
                    Logger.info(String.format("Downloaded %s", imageFilename));
                }
                // Point image src to file
                imageElement.attr("src", imageFilename);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
                imageElement.remove();
            }
        }
    }

    /**
     * Adds the chapter title on top of chapter body according to format setting
     */
    public void addTitle(String titleFormat) {
        chapterBody.child(0).before(titleFormat.replace("%ct", name));
    }

    public Element getChapterBody() {
        return chapterBody;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(Status downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getWordCount() {
        return chapterBody.text().split(" ").length;
    }

    public void removeImageReferences() {
        chapterBody.select("img").remove();
    }

    @Override
    public String toString() {
        return name;
    }
}
