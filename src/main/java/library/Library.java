package library;

import grabber.GrabberUtils;
import grabber.Novel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import system.Config;
import system.init;
import notifications.DesktopNotification;
import notifications.EmailNotification;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles continues polling of followed novels for new releases.
 */
public class Library {
    private static Library library;
    private static String libraryFile = GrabberUtils.getCurrentPath() + "/library.json";
    public static String libraryFolder = GrabberUtils.getCurrentPath() + "/Novels";
    private Config config = Config.getInstance();
    private EmailNotification emailClient;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Future<?> future;
    private List<LibraryNovel> starredNovels = new ArrayList<>();

    private Library() {
        if (!config.getHost().isEmpty()) {
            try {
                emailClient = new EmailNotification();
            } catch (Exception e) {
                GrabberUtils.err("Could not establish connection to SMTP Server. Check email settings and restart.");
            }
        }
    }

    public static Library getInstance() {
        if (library == null) {
            library = new Library();
            library.readLibraryFile();
        }
        return library;
    }

    /**
     * Reads library file(JSON) and creates library novels list
     */
    private void readLibraryFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(libraryFile))) {
            JSONObject libraryObj = (JSONObject) new JSONParser().parse(reader);
            // Create starred novels from json objects
            JSONArray libraryNovels = (JSONArray) libraryObj.get("starredNovels");
            for (Object loadedNovel: libraryNovels) {
                try {
                    starredNovels.add(new LibraryNovel((JSONObject) loadedNovel));
                } catch (NullPointerException e) {
                    GrabberUtils.err("Could not convert novel JSON: " + loadedNovel);
                }
            }
        } catch (IOException e) {
            GrabberUtils.err("No library file found.");
        } catch (ParseException e) {
            GrabberUtils.err("Could not parse library file.", e);
        }
    }

    /**
     * Saves library as JSON file.
     */
    public void writeLibraryFile() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(libraryFile))) {
            JSONObject libraryObj = new JSONObject();
            // Create JSON array from starred novels
            JSONArray libraryNovels = new JSONArray();
            for(LibraryNovel libraryNovel: starredNovels) {
                libraryNovels.add(libraryNovel.getAsJSONObject());
            }
            libraryObj.put("starredNovels", libraryNovels);

            writer.write(libraryObj.toJSONString());
        } catch(IOException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }

    public List<LibraryNovel> getNovels() {
        return starredNovels;
    }

    public LibraryNovel getNovel(String novelUrl) {
        for(LibraryNovel currNovel: starredNovels) {
            if (currNovel.getNovelUrl().equals(novelUrl)) return currNovel;
        }
        return null;
    }

    public boolean isStarred(String novelUrl) {
        for(LibraryNovel currNovel: starredNovels) {
            if (currNovel.getNovelUrl().equals(novelUrl)) return true;
        }
        return false;
    }

    public void removeNovel(String novelUrl) {
        starredNovels.removeIf(currNovel -> currNovel.getNovelUrl().equals(novelUrl));
    }

    public void addNovel(Novel novel) {
        LibraryNovel libNovel = toLibraryNovel(novel);
        starredNovels.add(libNovel);
        // Try to move file to library folder if previously downloaded
        File epub = new File(novel.saveLocation + "/" + novel.filename);
        if (epub.exists()) {
            try {
                Files.move(Paths.get(epub.getPath()), Paths.get(libNovel.getSaveLocation() + novel.filename));
            } catch (IOException e) {
                GrabberUtils.err("[LIBRARY]Could not move novel file. " + e.getMessage(), e);
            }
        }
    }

    public LibraryNovel toLibraryNovel(Novel novel) {
        LibraryNovel libNovel = new LibraryNovel();
        libNovel.novelLink = novel.novelLink;
        int chapterAmount = novel.chapterList.size();
        libNovel.setLastChapterNumber(chapterAmount);
        libNovel.setLastChapterName(novel.chapterList.get(chapterAmount-1).name);
        libNovel.setNewestChapterNumber(chapterAmount);
        libNovel.setNewestChapterName(novel.chapterList.get(chapterAmount-1).name);
        libNovel.setUpdateLast(true);
        libNovel.setCheckingActive(true);
        libNovel.setUseAccount(novel.useAccount);
        libNovel.setDisplayChapterTitle(novel.displayChapterTitle);
        libNovel.setWaitTime(novel.waitTime);
        libNovel.setGetImages(novel.getImages);
        libNovel.metadata = novel.metadata;

        String destDir;
        String cleanFolderName = libNovel.metadata.getTitle().trim()
                .replace("^\\.+", "")
                .replaceAll("[\\\\/:*?\"<>|]", "");
        if (cleanFolderName.length() > 240) cleanFolderName = cleanFolderName.substring(0,240);
        if(Config.getInstance().isUseStandardLocation()) {
            destDir = Config.getInstance().getSaveLocation() + "/" + cleanFolderName + "/";
        } else {
            destDir = Library.libraryFolder+ "/" + cleanFolderName + "/";
        }
        libNovel.metadata.saveCover(destDir);
        libNovel.setSaveLocation(destDir);
        return libNovel;
    }

    public void startPolling() {
        future = scheduler.scheduleWithFixedDelay(this::run, 0, config.getFrequency(), TimeUnit.MINUTES);
    }

    public void stopPolling() {
        future.cancel(true);
    }

    /**
     * Checks each novel in library for new chapter releases.
     * Optionally downloads new chapters and sends notifications if selected.
     */
    private void run() {
        if(init.gui != null) {
            init.gui.libraryIsChecking(true);
        }
        for(LibraryNovel libNovel: getNovels()) {
            // Skip novel if checking is disabled for it
            if (!libNovel.isCheckingActive()) continue;

            GrabberUtils.info("Checking "+ libNovel.getMetadata().getTitle());

            // Make novel builder generic to handle libNovel in future
            Novel novel;
            try {
                novel = Novel.builder()
                        .novelLink(libNovel.getNovelUrl())
                        .saveLocation(libNovel.getSaveLocation())
                        .setSource(libNovel.getNovelUrl())
                        .useAccount(libNovel.isUseAccount())
                        .getImages(libNovel.isGetImages())
                        .displayChapterTitle(libNovel.isDisplayChapterTitle())
                        .waitTime(libNovel.getWaitTime())
                        .window("checker")
                        .build();
            } catch (ClassNotFoundException e) {
                GrabberUtils.err(e.getMessage());
                continue;
            } catch (IOException e) {
                GrabberUtils.err(e.getMessage(), e);
                continue;
            }
            // Get chapter list
            novel.check();
            if(novel.chapterList.isEmpty()) continue;

            int newestChapterNumber = novel.chapterList.size();
            String newestChapterName = novel.chapterList.get(newestChapterNumber-1).name;
            libNovel.setNewestChapterNumber(newestChapterNumber);
            libNovel.setNewestChapterName(newestChapterName);

            // Difference between last local chapter and newest released online
            int chapterDifference = libNovel.getNewestChapterNumber() - libNovel.getLastLocalChapterNumber();
            // Download new chapters if releases are past set threshold
            if(chapterDifference >= libNovel.getThreshold() && libNovel.isAutoDownloadEnabled()) {
                novel = Novel.modifier(novel)
                        .firstChapter(libNovel.getLastLocalChapterNumber() + 1)
                        .lastChapter(novel.chapterList.size())
                        .build();
                try {
                    novel.downloadChapters();
                    novel.output();
                } catch (InterruptedException e) {
                    GrabberUtils.err(e.getMessage(), e);
                }

                // Send EPUB as email attachment
                if(libNovel.isSendAttachmentEnabled() && emailClient != null) {
                    emailClient.sendAttachment(novel);
                }
                // Update last local chapter number to newest
                if(libNovel.isUpdateLast()) {
                    libNovel.setLastChapterNumber(newestChapterNumber);
                    libNovel.setLastChapterName(newestChapterName);
                }
            }

            // Send notifications
            if(chapterDifference > 0 && libNovel.notificationsEnabled()) {
                if(libNovel.isSendEmailNotification() && emailClient != null) {
                    emailClient.sendNotification(novel);
                }
                if(libNovel.isSendDesktopNotification()) {
                    DesktopNotification.sendChapterReleaseNotification(novel);
                }
                // Update last local chapter number to newest
                if(libNovel.isUpdateLast()) {
                    libNovel.setLastChapterNumber(newestChapterNumber);
                    libNovel.setLastChapterName(newestChapterName);
                }
            }

        }
        // Update library gui
        if(init.gui != null) {
            init.gui.buildLibrary();
            init.gui.libraryIsChecking(false);
        }
        // Write changes to file
        writeLibraryFile();
    }

    /**
     * Checks specific novel for new releases
     */
    public void checkNovel(LibraryNovel libNovel) throws IOException, ClassNotFoundException {
        if(init.gui != null) {
            init.gui.libraryIsChecking(true);
        }
        GrabberUtils.info("Checking "+ libNovel.getMetadata().getTitle());

        Novel novel = Novel.builder()
                .novelLink(libNovel.getNovelUrl())
                .saveLocation(libNovel.getSaveLocation())
                .setSource(libNovel.getNovelUrl())
                .useAccount(libNovel.isUseAccount())
                .window("checker")
                .build();
        // Get chapter list
        novel.check();
        if(!novel.chapterList.isEmpty()) {
            int newestChapterNumber = novel.chapterList.size();
            String newestChapterName = novel.chapterList.get(newestChapterNumber-1).name;
            libNovel.setNewestChapterNumber(newestChapterNumber);
            libNovel.setNewestChapterName(newestChapterName);
        }
        // Update library gui
        if(init.gui != null) {
            init.gui.buildLibrary();
            init.gui.libraryIsChecking(false);
        }
        // Write changes to file
        writeLibraryFile();
    }
}
