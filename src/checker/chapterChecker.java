package checker;

import grabber.HostSettings;
import gui.GUI;
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

public class chapterChecker {
    public static List<String> hosts = new ArrayList<>();
    public static List<String> urls = new ArrayList<>();
    private static List<Integer> lastChapters;
    private static List<Integer> toBeRemoved;
    private static ScheduledExecutorService service;
    private static final int pollingInterval = 20;
    public static boolean checkerRunning;
    private static boolean taskIsKilled;
    private static String curTitle;
    private static String latestChapterListFile;


    //checker handling.
    public static void chapterPolling(GUI mygui) {
        taskIsKilled = false;
        checkerRunning = true;
        lastChapters = new ArrayList<>();
        toBeRemoved = new ArrayList<>();

        latestChapterListFile = GUI.appdataPath + File.separator + "lastChapters_" + urls.hashCode() + ".json";
        File file = new File(latestChapterListFile);
        if (file.exists()) {
            readDataFromJSON();
        } else {
            initializeFile(mygui);
        }
        if (urls.isEmpty()) {
            mygui.appendText("checker", "No checkers defined.");
            mygui.stopPolling();
            mygui.resetCheckerGUIButtons();
            return;
        }
        if (lastChapters.contains(-1)) {
            mygui.appendText("checker", "Could not reach one or more hosts.");
            mygui.stopPolling();
            mygui.resetCheckerGUIButtons();
            return;
        }
        // Updates checker list on gui with chapter numbers.
        for (int i = 0; i < urls.size(); i++) {
            GUI.listModelCheckerLinks.set(i, "Latest chapter: " + lastChapters.get(i) + " / [" + urls.get(i) + "]");
        }
        // Runs every set interval.
        Runnable runnable = () -> {
            try {
                for (int i = 0; i < urls.size(); i++) {
                    if (!taskIsKilled) {
                        Thread.sleep((int) (Math.random() * 3001 + 2000));
                        mygui.appendText("checker", "Polling: " + urls.get(i));
                        int newChapter = countChapters(hosts.get(i), urls.get(i));
                        if (newChapter > lastChapters.get(i)) {
                            if (newChapter - lastChapters.get(i) > 1) {
                                mygui.appendText("checker", newChapter - lastChapters.get(i) + " new chapters.");
                                showNotification(curTitle + ": " + (newChapter - lastChapters.get(i)) + " new chapters.");
                            } else {
                                mygui.appendText("checker", "New chapter: " + newChapter);
                                showNotification(curTitle + ": " + newChapter);
                            }
                            lastChapters.add(i, newChapter);
                            writeDataToJSON(latestChapterListFile, true);
                            GUI.listModelCheckerLinks.set(i, "Latest chapter: " + newChapter + " / [" + curTitle + "]");
                        }
                    }
                }
                if (!taskIsKilled)
                    mygui.appendText("checker", "Polling again in " + pollingInterval + " minutes.");
            } catch (IllegalArgumentException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        // Start scheduled task.
        if (!taskIsKilled) {
            mygui.checkStatusLbl.setText("Checking active.");
            mygui.checkStopPollingBtn.setEnabled(true);
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 0, pollingInterval, TimeUnit.MINUTES);
        } else {
            mygui.appendText("checker", "Stopping polling.");
            mygui.stopPolling();
        }
    }

    /**
     * Creates and fills a file for a new list of Checkers.
     */
    private static void initializeFile(GUI mygui) {
        mygui.checkStatusLbl.setText("Initializing...");
        // Gets chapter count for each checker entry.
        for (int i = 0; i < urls.size(); i++) {
            try {
                mygui.appendText("checker", "Initializing: " + urls.get(i));
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
            mygui.appendText("checker", "Removing faulty: " + urls.get(a));
            urls.remove(a);
            hosts.remove(a);
            GUI.listModelCheckerLinks.removeElementAt(a);
        }
        toBeRemoved.clear();
        // Create a new file to store latest chapter numbers. File name is the hashCode() of List<String>urls.
        if (!urls.isEmpty()) {
            latestChapterListFile = GUI.appdataPath + File.separator + "lastChapters_" + urls.hashCode() + ".json";
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

    public static void writeDataToJSON(String filepath, boolean withChapters) {
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

    public static void killTask(GUI mygui) {
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
            mygui.appendText("checker", "Stopped polling.");
            mygui.resetCheckerGUIButtons();
        }
    }

    private static int countChapters(String host, String tocUrl) {
        HostSettings currHostSettings = new HostSettings(host, tocUrl);
        try {
            Document doc = Jsoup.connect(currHostSettings.url).timeout(30 * 1000).get();
            curTitle = doc.title();
            Elements chapterItems = doc.select(currHostSettings.chapterLinkSelecter);
            return chapterItems.size();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void showNotification(String message) {
        try {
            GUI.trayIcon.displayMessage("Novel-Grabber: Chapter release", message, TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
