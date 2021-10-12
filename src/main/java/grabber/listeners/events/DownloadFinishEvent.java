package grabber.listeners.events;

import grabber.novel.Chapter;
import grabber.novel.Chapter.Status;

import java.util.ArrayList;
import java.util.List;

public class DownloadFinishEvent {
    private List<Chapter> chapters;
    private int unsuccessfulChapterCount;
    private boolean isSuccessful;

    public DownloadFinishEvent(List<Chapter> chapters) {
        this.chapters = chapters;
        unsuccessfulChapterCount = countUnsuccessfulChapters();
        isSuccessful = unsuccessfulChapterCount < 1;
    }

    private int countUnsuccessfulChapters() {
        int count = 0;
        for (Chapter chapter : chapters) {
            if (chapter.getDownloadStatus() != Status.SUCCESS) count++;
        }
        return count;
    }

    public List<Chapter> getChaptersWithStatus(Status status) {
        List<Chapter> chapters = new ArrayList<>();
        for (Chapter chapter : chapters) {
            if (chapter.getDownloadStatus() == status) {
                chapters.add(chapter);
            }
        }
        return chapters;
    }

    public int getUnsuccessfulChapterCount() {
        return unsuccessfulChapterCount;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
