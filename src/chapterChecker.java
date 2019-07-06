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
    private static final int pollingInterval = 20;
    static boolean checkerRunning;
    private static boolean taskIsKilled;
    private static String curTitle;
    private static String latestChapterListFile;

    static void chapterPolling() {
        if (!SystemTray.isSupported()) {
            NovelGrabberGUI.appendText("checker", "System tray not supported!");
            NovelGrabberGUI.stopPolling();
            return;
        }
        latestChapterListFile = NovelGrabberGUI.appdataPath + File.separator + "lastChapters_" + urls.hashCode() + ".txt";
        taskIsKilled = false;
        checkerRunning = true;
        List<Integer> lastChapter = new ArrayList<>();
        List<Integer> toBeRemoved = new ArrayList<>();
        NovelGrabberGUI.checkStatusLbl.setText("Initializing...");
        //Initializing
        //reads latest chapter numbers from existing file. File name is compared against hashCode() of List<String> urls.
        File file = new File(latestChapterListFile);
        if (file.exists()) {
            Scanner sc = null;
            try {
                sc = new Scanner(new File(latestChapterListFile));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            while (Objects.requireNonNull(sc).hasNextLine()) {
                lastChapter.add(Integer.parseInt(sc.next()));
                sc.nextLine();
            }
            sc.close();
            //gets amount of chapters in toc of novel urls. Faulty urls get added to toBeRemoved.
        } else {
            for (int i = 0; i < urls.size(); i++) {
                try {
                    NovelGrabberGUI.appendText("checker", "Initializing: " + urls.get(i));
                    lastChapter.add(countChapters(hosts.get(i), urls.get(i)));
                    Thread.sleep(3000);
                } catch (IllegalArgumentException e) {
                    toBeRemoved.add(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //removes faulty entries
            toBeRemoved.sort(Comparator.reverseOrder());
            for (int a : toBeRemoved) {
                NovelGrabberGUI.appendText("checker", "Removing faulty: " + urls.get(a));
                urls.remove(a);
                hosts.remove(a);
            }
            toBeRemoved.clear();
            NovelGrabberGUI.listModelCheckerLinks.clear();
            //creates a new file to store latest chapter numbers and names it after hashCode() of entries (List<String>urls
            if (!urls.isEmpty()) {
                latestChapterListFile = NovelGrabberGUI.appdataPath + File.separator + "lastChapters_" + urls.hashCode() + ".txt";
                try (PrintStream out = new PrintStream(latestChapterListFile, "UTF-8")) {
                    for (int i = 0; i < urls.size(); i++) {
                        out.println(lastChapter.get(i) + " " + urls.get(i));
                    }
                    NovelGrabberGUI.appendText("checker", "Creating latest-chapter file: " + latestChapterListFile);
                } catch (IOException e) {
                    out.println(Shared.time() + e.getMessage());
                }
            }
        }
        if (urls.isEmpty()) {
            NovelGrabberGUI.appendText("checker", "No checkers defined.");
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.resetCheckerGUIButtons();
            return;
        }
        if (lastChapter.contains(-1)) {
            NovelGrabberGUI.appendText("checker", "Could not reach one or more hosts.");
            return;
        }
        //updates checker list on GUI
        NovelGrabberGUI.listModelCheckerLinks.clear();
        for (int i = 0; i < urls.size(); i++) {
            NovelGrabberGUI.listModelCheckerLinks.addElement("Latest chapter: " + lastChapter.get(i) + " / [" + urls.get(i) + "]");
        }
        Runnable runnable = () -> {
            try {
                for (int i = 0; i < urls.size(); i++) {
                    if (!taskIsKilled) {
                        Thread.sleep((int) (Math.random() * 7001 + 2000));
                        NovelGrabberGUI.appendText("checker", "Polling: " + urls.get(i));
                        int newChapter = countChapters(hosts.get(i), urls.get(i));
                        if (newChapter > lastChapter.get(i)) {
                            if (newChapter - lastChapter.get(i) > 1) {
                                NovelGrabberGUI.appendText("checker", newChapter - lastChapter.get(i) + " new chapters.");
                                showNotification(curTitle + ": " + (newChapter - lastChapter.get(i)) + " new chapters.");
                            } else {
                                NovelGrabberGUI.appendText("checker", "New chapter: " + newChapter);
                                showNotification(curTitle + ": " + newChapter);
                            }
                            modifyFile(latestChapterListFile, lastChapter.get(i).toString() + " " + urls.get(i), newChapter + " " + urls.get(i));
                            NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + newChapter + " / [" + curTitle + "]");
                            lastChapter.add(i, newChapter);
                        }
                    }
                }
                if (!taskIsKilled)
                    NovelGrabberGUI.appendText("checker", "Polling again in " + pollingInterval + " minutes.");
            } catch (IllegalArgumentException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        NovelGrabberGUI.checkStatusLbl.setText("Checking active.");
        NovelGrabberGUI.checkStopPollingBtn.setEnabled(true);
        if (!taskIsKilled) {
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, 0, pollingInterval, TimeUnit.MINUTES);
        } else {
            NovelGrabberGUI.appendText("checker", "Stopping polling.");
            NovelGrabberGUI.stopPolling();
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
            try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
                out.print(newText);
            } catch (IOException e) {
                out.println(Shared.time() + e.getMessage());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
        Document doc = null;
        try {
            doc = Jsoup.connect(currentNovel.getUrl()).timeout(10 * 1000).get();
            curTitle = doc.title();
            Elements content = doc.select(currentNovel.getChapterLinkContainer());
            Elements chapterItem = content.select(currentNovel.getChapterLinkSelector());
            return chapterItem.size();
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
