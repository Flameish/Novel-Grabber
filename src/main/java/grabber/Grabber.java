package grabber;

import grabber.listeners.ChapterProgressListener;
import grabber.listeners.DownloadStatusListener;
import grabber.listeners.events.ChapterProgressEvent;
import grabber.listeners.events.DownloadFinishEvent;
import grabber.listeners.events.DownloadStopEvent;
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
    public List<DownloadStatusListener> dsListeners = new ArrayList<>();
    private boolean isStop = false;

    public Grabber() {}

    public Grabber(GrabberOptions grabberOptions) {
        this.grabberOptions = grabberOptions;
    }

    public static NovelMetadata fetchNovelDetails(String novelUrl) throws SourceException {
        Logger.info(String.format("Fetching novel information for: %s", novelUrl));
        Source source = Sources.getSourceByUrl(novelUrl);
        return source.fetchNovelMetadata(novelUrl);
    }

    public Novel downloadNovel(String novelUrl) throws SourceException {
        NovelMetadata metadata = fetchNovelDetails(novelUrl);
        Novel novel = new Novel(metadata);
        downloadNovel(novel);
        return novel;
    }

    public void downloadNovel(Novel novel) throws IllegalArgumentException {
        List<Chapter> chaptersToDownload = novel.getToDownloadChapters();
        if (chaptersToDownload.size() < 1) throw new IllegalArgumentException("Chapter range is less than 1!");

        isStop = false;

        Logger.info(String.format("%s: Downloading %d chapter(s)",
                novel.getMetadata().getTitle(),
                chaptersToDownload.size()
        ));
        downloadChapters(novel, chaptersToDownload);

        if (!isStop) {
            dsListeners.forEach(listener -> listener.downloadFinished(new DownloadFinishEvent(chaptersToDownload)));
        }
    }

    private void downloadChapters(Novel novel, List<Chapter> chaptersToDownload) {
        for (Chapter chapter : chaptersToDownload) {
            // Stop download
            if (isStop) {
                dsListeners.forEach(listener -> listener.downloadStopped(new DownloadStopEvent()));
                break;
            }
            // Check if chapter was already downloaded
            if (chapter.getDownloadStatus() == Status.SUCCESS) {
                Logger.info(chapter.getName() + " already downloaded.");
                continue;
            }

            // Download chapter
            NovelOptions novelOptions = novel.getOptions();
            Source source = novel.getMetadata().getSource();
            downloadChapter(source, chapter, novelOptions);

            if (chapter.getDownloadStatus() == Status.SUCCESS) {
                if (novelOptions.isGetImages()) chapter.downloadImages(novel.getImages());
                else chapter.removeImageReferences();

                novel.addWordCount(chapter.getWordCount());
            }
            // Notify chapter progress
            cpListeners.forEach(listener -> listener.update(new ChapterProgressEvent(chapter)));
            Logger.info(String.format("%s [%s]", chapter.getName(), chapter.getDownloadStatus()));

            try {
                TimeUnit.MILLISECONDS.sleep(grabberOptions.getWaitTime());
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
            }
        }
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

                cpListeners.forEach(listener -> listener.update(new ChapterProgressEvent(chapter)));
                try {
                    TimeUnit.MILLISECONDS.sleep(grabberOptions.getWaitTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return novel;
    }


    public void stopDownload() {
        isStop = true;
    }

    public void setOptions(GrabberOptions grabberOptions) {
        this.grabberOptions = grabberOptions;
    }

    public GrabberOptions getGrabberOptions() {
        return grabberOptions;
    }

    public List<ChapterProgressListener> getCpListeners() {
        return cpListeners;
    }

    public void addChapterProgressListener(ChapterProgressListener cpListener) {
        cpListeners.add(cpListener);
    }
    public void addDownloadStatusListener(DownloadStatusListener dsListener) {
        dsListeners.add(dsListener);
    }

}
