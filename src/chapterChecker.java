import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

class chapterChecker {
    static List<String> hosts = new ArrayList<>();
    static List<String> urls = new ArrayList<>();
    private static ScheduledExecutorService service;
    private static String title;
    private static int pollingInterval = 5;
    private static boolean taskIsKilled;
    private static String latestChapterListFile;

    static void chapterPolling() {
        taskIsKilled = false;
        if (!SystemTray.isSupported()) {
            NovelGrabberGUI.appendText("checker", "System tray not supported!");
            NovelGrabberGUI.stopPolling();
            return;
        }
        latestChapterListFile = NovelGrabberGUI.homepath + File.separator + "lastChapters_" + urls.hashCode() + ".txt";
        List<Integer> latestChapter = new ArrayList<>();
        List<Integer> toBeRemoved = new ArrayList<>();
        NovelGrabberGUI.checkStatusLbl.setText("Initializing...");
        //Initializing
        File file = new File(latestChapterListFile);
        if (file.exists()) {
            Scanner sc = null;
            try {
                sc = new Scanner(new File(latestChapterListFile));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            while (Objects.requireNonNull(sc).hasNextLine()) {
                latestChapter.add(Integer.parseInt(sc.next()));
                sc.nextLine();
            }
            sc.close();
        } else {
            for (int i = 0; i < urls.size(); i++) {
                try {
                    NovelGrabberGUI.appendText("checker", "Initializing: " + urls.get(i));
                    latestChapter.add(countChapters(hosts.get(i), urls.get(i)));
                    Thread.sleep(3000);
                } catch (IllegalArgumentException | IOException e) {
                    toBeRemoved.add(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            toBeRemoved.sort(Comparator.reverseOrder());
            for (int a : toBeRemoved) {
                NovelGrabberGUI.appendText("checker", "Removing faulty: " + urls.get(a));
                urls.remove(a);
                hosts.remove(a);

            }
            toBeRemoved.clear();
            NovelGrabberGUI.listModelCheckerLinks.clear();
            if (!urls.isEmpty()) {
                latestChapterListFile = NovelGrabberGUI.homepath + File.separator + "lastChapters_" + urls.hashCode() + ".txt";
                try (PrintStream out = new PrintStream(latestChapterListFile,
                        "UTF-8")) {
                    for (int i = 0; i < urls.size(); i++) {
                        out.println(latestChapter.get(i) + " " + urls.get(i));
                    }
                    NovelGrabberGUI.appendText("checker", "Creating latest-chapter file: " + latestChapterListFile);
                } catch (IOException e) {
                    out.println(Shared.time() + e.getMessage());
                }
            }
        }
        NovelGrabberGUI.listModelCheckerLinks.clear();
        for (int i = 0; i < urls.size(); i++) {
            NovelGrabberGUI.listModelCheckerLinks.addElement("Latest chapter: " + latestChapter.get(i) + " / [" + urls.get(i) + "]");
        }
        if (urls.isEmpty()) {
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.appendText("checker", "No checkers defined.");
            NovelGrabberGUI.checkRemoveEntry.setEnabled(false);
            NovelGrabberGUI.checkPollStartBtn.setEnabled(false);
            return;
        }
        Runnable runnable = () -> {
            try {
                for (int i = 0; i < urls.size(); i++) {
                    Thread.sleep((int) (Math.random() * 7001 + 2000));
                    NovelGrabberGUI.appendText("checker", "Polling: " + urls.get(i));
                    int temp = countChapters(hosts.get(i), urls.get(i));
                    if (temp > latestChapter.get(i)) {
                        modifyFile(latestChapterListFile, latestChapter.get(i).toString() + " " + urls.get(i), temp + " " + urls.get(i));
                        NovelGrabberGUI.appendText("checker", "New chapter: " + temp);
                        latestChapter.add(i, temp);
                        NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + latestChapter.get(i) + " / [" + urls.get(i) + "]");
                        showNotification(title + ": " + latestChapter.get(i));
                    }
                    //testing; sets latest chapter to 1
                    //latestChapter.set(i,1);
                }
                NovelGrabberGUI.appendText("checker", "Polling again in " + pollingInterval + " minutes.");
            } catch (IllegalArgumentException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        NovelGrabberGUI.checkStatusLbl.setText("Checking active.");
        NovelGrabberGUI.checkStopPollingBtn.setEnabled(true);
        if (!taskIsKilled) {
            NovelGrabberGUI.appendText("checker", "Polling again in " + pollingInterval + " minutes.");
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, pollingInterval, pollingInterval, TimeUnit.SECONDS);
        } else {
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.appendText("checker", "Stopping polling.");
        }
    }

    private static void modifyFile(String filePath, String oldString, String newString) {
        try {
            File file = new File(filePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder oldText = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                oldText.append(line).append(System.lineSeparator());
            }
            reader.close();
            String newText = oldText.toString().replaceAll(oldString, newString);
            try (PrintStream out = new PrintStream(filePath,
                    "UTF-8")) {
                out.print(newText);
            } catch (IOException e) {
                out.println(Shared.time() + e.getMessage());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    static void killTask() {
        taskIsKilled = true;
        service.shutdown();
        try {
            if (!service.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
        NovelGrabberGUI.appendText("checker", "Stopping polling.");
    }

    private static int countChapters(String host, String tocUrl) throws IllegalArgumentException, IOException {
        Novel currentNovel = new Novel(host, tocUrl);
        Document doc = Jsoup.connect(currentNovel.getUrl()).get();
        title = doc.title();
        Elements content = doc.select(currentNovel.getChapterLinkContainer());
        Elements chapterItem = content.select(currentNovel.getChapterLinkSelector());
        return chapterItem.size();
    }

    private static void showNotification(String message) {
        try {
            NovelGrabberGUI.trayIcon.displayMessage("Novel-Grabber: Chapter release", message, TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
