package grabber.novel;

import grabber.helper.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class NovelMetadata {
    private String url;
    private String title = "Unknown";
    private String author = "Unknown";
    private String description = "Unknown";
    private List<String> subjects = new ArrayList<>();
    private List<Chapter> chapterList = new ArrayList<>();
    private byte[] coverImage;
    private String coverName;

    public NovelMetadata() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!title.isEmpty()) this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (!author.isEmpty()) this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!description.isEmpty()) this.description = description;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public byte[] getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverUrl) {
        try {
            coverName = Utils.getFilenameFromUrl(coverUrl);
            coverImage = Utils.downloadImage(coverUrl);
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error");
            e.printStackTrace();
            // TODO: Default cover
            /*
            // Load default cover
            try {
                coverImage = ImageIO.read(this.getClass().getResource("/images/cover_placeholder.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

             */
        }
    }

    public void setCoverImage(String coverName, byte[] image) {
        this.coverName = coverName;
        this.coverImage = image;
    }

    public String getCoverName() {
        return coverName;
    }

    public void setCoverImage(byte[] coverImage) {
        this.coverImage = coverImage;
    }

    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void resetAllChapterStatus() {
        for (Chapter chapter : chapterList) {
            chapter.setDownloadStatus(Chapter.Status.NOT_DOWNLOADED);
        }
    }
}
