package grabber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NovelMetadata {
    private String title = "Unknown";
    private String author = "Unknown";
    private String description = "";
    private String coverFormat = "png";
    private List<String> subjects = new ArrayList<>();
    private BufferedImage bufferedCover;

    public NovelMetadata() {
        try {
            bufferedCover = ImageIO.read(this.getClass().getResource("/images/cover_placeholder.png"));
        } catch (IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }

    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getDescription() {
        return description;
    }
    public String getCoverFormat() {
        return coverFormat;
    }
    public List<String> getSubjects() {
        return subjects;
    }
    public BufferedImage getBufferedCover() {
        return bufferedCover;
    }

    public void setTitle(String title) {
        this.title = title.isEmpty() ? "Unknown": title;
    }
    public void setAuthor(String author) {
        this.author = author.isEmpty() ? "Unknown": author;
    }
    public void setDescription(String description) {
        this.description = description.isEmpty() ? "": description;
    }
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
    public void setBufferedCover(String coverURL) {
        if(coverURL != null && !coverURL.isEmpty()) {
            bufferedCover = GrabberUtils.getImage(coverURL);
            String coverName = GrabberUtils.getFilenameFromUrl(coverURL);
            coverFormat = GrabberUtils.getFileExtension(coverName) == null ? "png" : GrabberUtils.getFileExtension(coverName);
            if(bufferedCover == null) {
                try {
                    bufferedCover = ImageIO.read(this.getClass().getResource("/images/cover_placeholder.png"));
                    coverFormat = "png";
                } catch (IOException e) {
                    GrabberUtils.err(e.getMessage(), e);
                }
            }
        }
    }

    public void setBufferedCover(BufferedImage coverImage, String coverFormat) {
        this.bufferedCover = coverImage;
        this.coverFormat = coverFormat;
    }
}
