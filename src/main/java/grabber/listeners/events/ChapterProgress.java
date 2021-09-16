package grabber.listeners.events;

import grabber.novel.Chapter;

public class ChapterProgress {
    private Chapter chapter;

    public ChapterProgress(Chapter chapter) {
        this.chapter = chapter;
    }


    public Chapter getChapter() {
        return chapter;
    }
}
