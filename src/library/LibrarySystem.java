package library;

import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelOptions;
import system.Notification;
import system.init;
import system.persistent.EmailConfig;
import system.persistent.Library;

import java.util.concurrent.Executors;

public class LibrarySystem {

    public static void startPolling() {
        Executors.newSingleThreadExecutor().execute(LibrarySystem::pollLibrary);
    }

    private static void pollLibrary() {
        if(Library.getPolling()) {
            Notification mailer = new Notification();
            System.out.println("[INFO]Polling library...");
            for(String novelUrl: Library.getLibrary()) {
                // Getting the latest chapter count
                NovelOptions options = new NovelOptions();
                options.hostname = Library.getHost(novelUrl);
                options.headless = Library.useHeadless(novelUrl);
                options.useAccount = Library.useAccount(novelUrl);
                options.window  = "checker";
                options.novelLink = novelUrl;

                Novel autoNovel = new Novel(options);
                autoNovel.getChapterList();
                autoNovel.getMetadata();
                Library.setNewestChapter(novelUrl, autoNovel.chapters.size());
                int chapterDifference = Library.getNewestChapter(novelUrl) - Library.getLastChapter(novelUrl);
                // Autodownload
                if(chapterDifference >= Library.getThreshold(novelUrl) && chapterDifference > 0) {
                    if(Library.getAutoDownload(novelUrl)) {
                        String cliString = Library.getCLICommand(novelUrl)
                                +" -window checker -chapters "
                                +(Library.getLastChapter(novelUrl)+1)
                                +" "
                                +Library.getNewestChapter(novelUrl);
                        String[] cliParams = cliString.split(" ");
                        autoNovel = init.processParams(init.getParamsFromString(cliParams));
                        Library.setLastChapter(novelUrl, Library.getNewestChapter(novelUrl));
                        if(Library.useAttachment() && !EmailConfig.getHost().isEmpty()) {
                            mailer.sendAttachment(autoNovel);
                            System.out.println("[INFO]Email with attachment send.");
                        }
                    }
                }
                // Notification
                if(Library.useNotifications() && chapterDifference > 0 && !EmailConfig.getHost().isEmpty()) {
                    mailer.sendNotification(autoNovel);
                    System.out.println("[INFO]Notification send.");
                    // Adjust last downloaded chapter to newest
                    if(Library.getUpdateLast()) {
                        Library.setLastChapter(novelUrl, autoNovel.chapters.size());
                    }
                }
            }
            System.out.println("[INFO]Polling done.");
            init.window.buildLibrary();
            // wait 20 minutes
        }
        GrabberUtils.sleep(Library.getFrequency()*60*1000);
        pollLibrary();
    }
}
