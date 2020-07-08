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
        // Start new thread not to block others
        Executors.newSingleThreadExecutor().execute(LibrarySystem::pollLibrary);
    }

    private static void pollLibrary() {
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

            if(Library.getNewestChapter(novelUrl) - Library.getLastChapter(novelUrl) >= Library.getThreshold(novelUrl)) {
                // Autodownload
                if(Library.getAutoDownload(novelUrl)) {
                    String cliString = Library.getCLICommand(novelUrl)
                            +" -window checker -chapters "
                            +(Library.getLastChapter(novelUrl)+1)
                            +" "
                            +Library.getNewestChapter(novelUrl);
                    String[] cliParams = cliString.split(" ");
                    autoNovel = init.processParams(init.getParamsFromString(cliParams));
                    Library.setLastChapter(novelUrl, Library.getNewestChapter(novelUrl));
                }
                if(EmailConfig.useAttachment() && Library.getAutoDownload(novelUrl)) {
                    Notification.sendAttachment(autoNovel);
                    System.out.println("[INFO]Email with attachment send.");
                }
                if(EmailConfig.useNotifications()) {
                    Notification.sendNotification(autoNovel);
                    System.out.println("[INFO]Notification send.");
                }
            }

            GrabberUtils.sleep(1000);
        }
        System.out.println("[INFO]Polling done.");
        init.window.buildLibrary();
        // wait 20 minutes
        GrabberUtils.sleep(20*60*1000);
        pollLibrary();
    }
}
