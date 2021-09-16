package grabber;

import grabber.listeners.ChapterProgressListener;
import grabber.listeners.events.ChapterProgress;
import grabber.novel.Chapter;
import grabber.novel.Chapter.Status;
import grabber.novel.Novel;
import grabber.novel.NovelMetadata;
import grabber.novel.NovelOptions;
import grabber.sources.Source;
import grabber.sources.SourceException;
import grabber.sources.Sources;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Element;
import system.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Grabber {
    public GrabberOptions grabberOptions = new GrabberOptions();
    public List<ChapterProgressListener> cpListeners = new ArrayList<>();


    public Grabber() {}

    public Grabber(GrabberOptions grabberOptions) {
        this.grabberOptions = grabberOptions;
    }

    public void setOptions(GrabberOptions grabberOptions) {
        this.grabberOptions = grabberOptions;
    }

    public NovelMetadata fetchNovelDetails(String novelUrl) throws SourceException {
        Source source = Sources.getSourceByUrl(novelUrl);
        Logger.info(String.format("Fetching novel information for: %s", novelUrl));
        return source.fetchNovelMetadata(novelUrl);
    }

    public Novel downloadNovel(String novelUrl) throws SourceException {
        NovelMetadata metadata = fetchNovelDetails(novelUrl);
        Novel novel = new Novel(metadata);
        downloadNovel(novel);
        return novel;
    }

    public Novel updateNovel(Novel novel, String novelUrl) throws SourceException {
        NovelMetadata newMetadata = fetchNovelDetails(novelUrl);
        Logger.info("Comparing chapter lists...");
        List<Chapter> newChapterList = newMetadata.getChapterList();
        List<Chapter> oldChapterList = novel.getChapters();
        if (newChapterList.size() == oldChapterList.size()) {
            Logger.info("No change in chapter list.");
            return novel;
        }
        // TODO: Think of something with better performance
        for (Chapter oldChapter : oldChapterList) {
            for (Chapter newChapter : newChapterList) {
                if (oldChapter.getUrl().equals(newChapter.getUrl())) {
                    newChapter.setChapterBody(oldChapter.getChapterBody());
                    newChapter.setDownloadStatus(Status.SUCCESS);
                }
            }
        }
        novel.getMetadata().setChapterList(newChapterList);
        downloadNovel(novel);

        return novel;
    }

    public void downloadNovel(Novel novel) throws IllegalArgumentException, SourceException {
        if (novel.chaptersToDownloadCount() < 1) throw new IllegalArgumentException("Chapter range is less than 1!");
        NovelOptions novelOptions = novel.getOptions();
        Source source = Sources.getSourceByUrl(novel.getMetadata().getUrl());

        Logger.info(String.format("Downloading %d chapter(s)", novel.chaptersToDownloadCount()));
        for (Chapter chapter : novel.getToDownloadChapters()) {
            if (chapter.getDownloadStatus() == Status.SUCCESS) continue;
            downloadChapter(source, chapter, novelOptions);

            if (chapter.getDownloadStatus() == Status.SUCCESS) {
                if (novelOptions.isGetImages()) chapter.downloadImages(novel.getImages());
                else chapter.removeImageReferences();

                novel.addWordCount(chapter.getWordCount());
            }
            cpListeners.forEach(listener -> listener.update(new ChapterProgress(chapter)));

            try {
                TimeUnit.SECONDS.sleep(grabberOptions.getWaitTime());
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
            }
        }
    }

    public Novel retryFailedChapters(Novel novel) throws SourceException {
        NovelOptions novelOptions = novel.getOptions();
        List<Chapter> failedChapters = novel.getFailedChapters();
        Source source = Sources.getSourceByUrl(novel.getMetadata().getUrl());

        Logger.info(String.format("Retrying %d failed chapter(s)", failedChapters.size()));
        for (int x = 0; x < grabberOptions.getRetryAttempts(); x++) {

            for (Chapter chapter : failedChapters) {
                downloadChapter(source, chapter, novel.getOptions());

                if (chapter.getDownloadStatus() == Status.SUCCESS) {
                    if (novelOptions.isGetImages()) chapter.downloadImages(novel.getImages());
                    else chapter.removeImageReferences();

                    novel.addWordCount(chapter.getWordCount());
                }

                cpListeners.forEach(listener -> listener.update(new ChapterProgress(chapter)));

                try {
                    TimeUnit.SECONDS.sleep(grabberOptions.getWaitTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return novel;
    }

    public void stopDownload() {

    }

    public void downloadChapter(Source source, Chapter chapter, NovelOptions novelOptions) {
        try {
            Element chapterBody = source.fetchChapterBody(chapter.getUrl());
            chapter.setChapterBody(chapterBody);

            chapter.removeUnwantedTags(novelOptions.getBlacklistedTags());

            if(novelOptions.isCreateChapterHeadlines()) {
                chapter.addTitle("<span>%ct</span>");
            }

            chapter.setDownloadStatus(Status.SUCCESS);
        } catch (SourceException e) {
            if (e.getCause() instanceof HttpStatusException) {
                // Chapters with HTTP error code 400 can't be retried
                HttpStatusException httpException = (HttpStatusException) e.getCause();
                int statusCode = httpException.getStatusCode();
                chapter.setDownloadStatus(400 <= statusCode && statusCode < 500 ? Status.DENIED : Status.FAILED);
            } else {
                e.printStackTrace();
                chapter.setDownloadStatus(Status.FAILED);
            }
        }
    }



    public GrabberOptions getGrabberOptions() {
        return grabberOptions;
    }

    public List<ChapterProgressListener> getCpListeners() {
        return cpListeners;
    }

    public void addCPListener(ChapterProgressListener cpListener) {
        cpListeners.add(cpListener);
    }
}
