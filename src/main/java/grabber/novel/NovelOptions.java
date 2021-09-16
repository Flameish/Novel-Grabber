package grabber.novel;


import java.util.ArrayList;
import java.util.List;

public class NovelOptions {
    private List<String> blacklistedTags = new ArrayList<>();
    private String novelFilenameTemplate = "%a - %t";
    private boolean createChapterHeadlines = false;
    private boolean createToc = true;
    private boolean createDesc = true;
    private boolean reverseChapterOrder = false;
    private boolean getImages = true;
    private int firstChapter = 1;
    private int lastChapter = -1;

    public NovelOptions() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder modifier(NovelOptions options) {
        return new Builder(options);
    }

    public boolean isGetImages() {
        return getImages;
    }

    public boolean isCreateChapterHeadlines() {
        return createChapterHeadlines;
    }

    public List<String> getBlacklistedTags() {
        return blacklistedTags;
    }

    public String getNovelFilenameTemplate() {
        return novelFilenameTemplate;
    }

    public boolean isCreateToc() {
        return createToc;
    }

    public boolean isCreateDesc() {
        return createDesc;
    }

    public boolean isReverseChapterOrder() {
        return reverseChapterOrder;
    }

    public int getFirstChapter() {
        return firstChapter;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public static final class Builder {
        private NovelOptions options;

        public Builder() {
            options = new NovelOptions();
        }

        public Builder(NovelOptions options) {
            this.options = options;
        }


        public Builder getImages(boolean getImages) {
            options.getImages = getImages;
            return this;
        }

        public Builder displayChapterTitle(boolean displayChapterTitle) {
            options.createChapterHeadlines = displayChapterTitle;
            return this;
        }

        public Builder blacklistedTags(List<String> blacklistedTags) {
            options.blacklistedTags = blacklistedTags;
            return this;
        }

        public Builder novelFilenameTemplate(String novelFilenameTemplate) {
            options.novelFilenameTemplate = novelFilenameTemplate;
            return this;
        }

        public Builder displayNovelDesc(boolean displayNovelDesc) {
            options.createDesc = displayNovelDesc;
            return this;
        }

        public Builder displayNovelToc(boolean displayNovelToc) {
            options.createToc = displayNovelToc;
            return this;
        }

        public Builder reverseChapterOrder(boolean reverseChapterOrder) {
            options.reverseChapterOrder = reverseChapterOrder;
            return this;
        }

        public Builder firstChapter(int firstChapter) {
            options.firstChapter = firstChapter;
            return this;
        }

        public Builder lastChapter(int lastChapter) {
            options.lastChapter = lastChapter;
            return this;
        }

        public NovelOptions build() {
            return options;
        }
    }
}

