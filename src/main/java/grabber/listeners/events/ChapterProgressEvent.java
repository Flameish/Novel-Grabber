package grabber.listeners.events;

import grabber.novel.Chapter;

public class ChapterProgressEvent {
    private Chapter chapter;

    public ChapterProgressEvent(Chapter chapter) {
        this.chapter = chapter;
    }


    public Chapter getChapter() {
        return chapter;
    }
}
