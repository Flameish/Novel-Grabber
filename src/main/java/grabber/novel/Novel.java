package grabber.novel;

import java.util.*;

public class Novel {
    private final NovelMetadata metadata;
    private NovelOptions options = new NovelOptions();
    private final Map<String, byte[]> images = new HashMap<>();
    private int wordCount = 0;

    public Novel(NovelMetadata metadata) {
        this.metadata = metadata;
    }

    public void addWordCount(int wordsToAdd) {
        this.wordCount += wordsToAdd;
    }

    public int getWordCount() {
        return wordCount;
    }

    public NovelMetadata getMetadata() {
        return metadata;
    }

    public List<Chapter> getChapters() {
        return metadata.getChapterList();
    }

    public void setChapters(List<Chapter> chapters) {
        metadata.setChapterList(chapters);
    }

    public Map<String, byte[]> getImages() {
        return images;
    }

    public NovelOptions getOptions() {
        return options;
    }

    public void setOptions(NovelOptions options) {
        this.options = options;
        // Replace 'all chapters' (-1) variable with actual last chapter number
        if (options.getLastChapter() == -1) {
            int actualLastChapter = metadata.getChapterList().size();
            NovelOptions.modifier(options)
                    .lastChapter(actualLastChapter)
                    .build();
        }
    }

    public List<Chapter> getFailedChapters() {
        List<Chapter> failedChapters = new ArrayList<>();
        for (Chapter chapter :  metadata.getChapterList()) {
            if (chapter.getDownloadStatus() == Chapter.Status.FAILED) failedChapters.add(chapter);
        }
        return failedChapters;
    }

    public List<Chapter> getToDownloadChapters() {
        return metadata.getChapterList().subList(options.getFirstChapter()-1, options.getLastChapter());
    }

    public int chaptersToDownloadCount() {
        return options.getLastChapter()+1 - options.getFirstChapter();
    }

    public void reverseChapterOrder() {
        Collections.reverse(metadata.getChapterList());
    }
}