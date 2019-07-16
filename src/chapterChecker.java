import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.toIntExact;

class chapterChecker {
    static List<String> hosts = new ArrayList<>();
    static List<String> urls = new ArrayList<>();
    private static List<Integer> lastChapters;
    private static List<Integer> toBeRemoved;
    private static ScheduledExecutorService service;
    private static final int pollingInterval = 20;
    static boolean checkerRunning;
    private static boolean taskIsKilled;
    private static String curTitle;
    private static String latestChapterListFile;

    /**
     * Checker handling.
     */
    static void chapterPolling() {
        taskIsKilled = false;
        checkerRunning = true;
        lastChapters = new ArrayList<>();
        toBeRemoved = new ArrayList<>();

        latestChapterListFile = NovelGrabberGUI.appdataPath + File.separator + "lastChapters_" + urls.hashCode() + ".json";
        File file = new File(latestChapterListFile);
        if (file.exists()) {
            readDataFromJSON();
        } else {
            initializeFile();
        }
        if (urls.isEmpty()) {
            NovelGrabberGUI.appendText("checker", "No checkers defined.");
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.resetCheckerGUIButtons();
            return;
        }
        if (lastChapters.contains(-1)) {
            NovelGrabberGUI.appendText("checker", "Could not reach one or more hosts.");
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.resetCheckerGUIButtons();
            return;
        }
        // Updates checker list on GUI with chapter numbers.
        for (int i = 0; i < urls.size(); i++) {
            NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + lastChapters.get(i) + " / [" + urls.get(i) + "]");
        }
        // Runs every set interval.
        Runnable runnable = () -> {
            try {
                for (int i = 0; i < urls.size(); i++) {
                    if (!taskIsKilled) {
                        Thread.sleep((int) (Math.random() * 3001 + 2000));
                        NovelGrabberGUI.appendText("checker", "Polling: " + urls.get(i));
                        int newChapter = countChapters(hosts.get(i), urls.get(i));
                        if (newChapter > lastChapters.get(i)) {
                            if (newChapter - lastChapters.get(i) > 1) {
                                NovelGrabberGUI.appendText("checker", newChapter - lastChapters.get(i) + " new chapters.");
                                showNotification(curTitle + ": " + (newChapter - lastChapters.get(i)) + " new chapters.");
                            } else {
                                NovelGrabberGUI.appendText("checker", "New chapter: " + newChapter);
                                showNotification(curTitle + ": " + newChapter);
                            }
                            lastChapters.add(i, newChapter);
                            writeDataToJSON(latestChapterListFile, true);
                            NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + newChapter + " / [" + curTitle + "]");
                        }
                    }
                }
                if (!taskIsKilled)
                    NovelGrabberGUI.appendText("checker", "Polling again in " + pollingInterval + " minutes.");
            } catch (IllegalArgumentException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        // Start scheduled task.
        if (!taskIsKilled) {
            NovelGrabberGUI.checkStatusLbl.setText("Checking active.");
            NovelGrabberGUI.checkStopPollingBtn.setEnabled(true);
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 0, pollingInterval, TimeUnit.MINUTES);
        } else {
            NovelGrabberGUI.appendText("checker", "Stopping polling.");
            NovelGrabberGUI.stopPolling();
        }
    }

    /**
     * Creates and fills a file for a new list of Checkers.
     */
    private static void initializeFile() {
        NovelGrabberGUI.checkStatusLbl.setText("Initializing...");
        // Gets chapter count for each Checker entry.
        for (int i = 0; i < urls.size(); i++) {
            try {
                NovelGrabberGUI.appendText("checker", "Initializing: " + urls.get(i));
                lastChapters.add(countChapters(hosts.get(i), urls.get(i)));
                Thread.sleep(3000);
            } catch (IllegalArgumentException e) {
                toBeRemoved.add(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Remove faulty entries.
        toBeRemoved.sort(Comparator.reverseOrder());
        for (int a : toBeRemoved) {
            NovelGrabberGUI.appendText("checker", "Removing faulty: " + urls.get(a));
            urls.remove(a);
            hosts.remove(a);
            NovelGrabberGUI.listModelCheckerLinks.removeElementAt(a);
        }
        toBeRemoved.clear();
        // Create a new file to store latest chapter numbers. File name is the hashCode() of List<String>urls.
        if (!urls.isEmpty()) {
            latestChapterListFile = NovelGrabberGUI.appdataPath + File.separator + "lastChapters_" + urls.hashCode() + ".json";
            writeDataToJSON(latestChapterListFile, true);
        }
    }

    /**
     * Reads latest chapter numbers from existing file.
     */
    private static void readDataFromJSON() {
        JSONParser parser = new JSONParser();
        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader(latestChapterListFile));
            for (Object o : a) {
                JSONObject checker = (JSONObject) o;
                lastChapters.add(toIntExact((Long) checker.get("CHAPTER")));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    static void writeDataToJSON(String filepath, boolean withChapters) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < hosts.size(); i++) {
            JSONObject checker = new JSONObject();
            if (withChapters) checker.put("CHAPTER", lastChapters.get(i));
            checker.put("HOST", hosts.get(i));
            checker.put("URL", urls.get(i));
            array.add(checker);
        }
        try (FileWriter JSONfile = new FileWriter(filepath)) {
            JSONfile.write(array.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void killTask() {
        if (!(service == null)) {
            taskIsKilled = true;
            service.shutdown();
            try {
                if (!service.awaitTermination(800, TimeUnit.MINUTES)) {
                    service.shutdownNow();
                }
            } catch (InterruptedException e) {
                service.shutdownNow();
            }
            NovelGrabberGUI.appendText("checker", "Stopped polling.");
            NovelGrabberGUI.resetCheckerGUIButtons();
        }
    }

    private static int countChapters(String host, String tocUrl) {
        Novel currentNovel = new Novel(host, tocUrl);
        try {
            Document doc = Jsoup.connect(currentNovel.getUrl()).timeout(30 * 1000).get();
            curTitle = doc.title();
            Elements chapterItems = doc.select(currentNovel.getChapterLinkSelector());
            return chapterItems.size();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void showNotification(String message) {
        try {
            NovelGrabberGUI.trayIcon.displayMessage("Novel-Grabber: Chapter release", message, TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
