package system.library;

import grabber.CLI;
import grabber.GrabberUtils;
import grabber.Novel;
import system.init;
import system.data.library.Library;
import system.data.library.LibraryNovel;
import system.notification.NotificationHandler;

/**
 * Handles continues polling of followed novels for new releases.
 */
public class LibrarySystem {
    private static Library library;
    private static Thread polling;
    private static NotificationHandler notifications;

    public static void startPolling() {
        if(polling == null) {
            polling = new Thread(LibrarySystem::pollLibrary);
        }
        library = Library.getInstance();
        notifications = new NotificationHandler();
        polling.start();
    }

    /**
     * Checks each novel in system.library for new chapter releases.
     * Downloads new chapters and sends emails if selected.
     * Sleeps thread for specified interval.
     */
    private static void pollLibrary() {
        if(library.isPollingEnabled()) {

            for(LibraryNovel libraryNovel: library.getStarredNovels()) {
                System.out.println("[LIBRARY]Checking "+ libraryNovel.getTitle());
                String[] cliParams = CLI.createArgsFromString(libraryNovel.getCliString());

                Novel autoNovel = Novel.builder()
                        .fromCLI(CLI.createParamsFromArgs(cliParams))
                        .window("checker")
                        .build();
                autoNovel.fetchChapterList();
                autoNovel.getMetadata();
                libraryNovel.setNewestChapter(autoNovel.chapterList.size());

                // Check if newly released chapter amount is past set threshold
                int chapterDifference = libraryNovel.getNewestChapter() - libraryNovel.getLastChapter();

                if(chapterDifference >= libraryNovel.getThreshold() && libraryNovel.isAutoDownloadEnabled()) {
                    autoDownload(libraryNovel, autoNovel);
                    // Send EPUB as email attachment if selected
                    if(libraryNovel.isSendAttachmentEnabled()) {
                        notifications.sendEmailAttachment(autoNovel);
                    }
                }
                // Send notification if selected
                if(chapterDifference > 0 && libraryNovel.notificationsEnabled()) {
                    notifications.sendNotifications(libraryNovel, autoNovel);
                    // Adjust last downloaded chapter to newest

                }
                if(libraryNovel.isUpdateLast()) {
                    libraryNovel.setLastChapter(autoNovel.chapterList.size());
                }
            }
            System.out.println("[LIBRARY]Checking done.");
            // Update system.library gui
            if(init.gui != null) {
                init.gui.buildLibrary();
            }
        }
        // Write changes to file
        library.save();

        GrabberUtils.sleep(library.getFrequency()*60*1000);
        pollLibrary();
    }

    /**
     * Downloads and creates EPUB of new chapters.
     * Updates last downloaded chapter number.
     * @param libraryNovel
     * @param autoNovel
     */
    private static void autoDownload(LibraryNovel libraryNovel, Novel autoNovel) {
        // Set chapter range
        autoNovel = Novel.modifier(autoNovel)
                .firstChapter(libraryNovel.getLastChapter())
                .lastChapter(autoNovel.chapterList.size())
                .build();
        try {
            autoNovel.downloadChapters();
            autoNovel.writeEpub();
            // downloadChapters throws an Exception when grabbing was stopped midway, not possible here
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
