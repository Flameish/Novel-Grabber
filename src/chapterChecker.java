import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class chapterChecker {
    static List<String> hosts = new ArrayList<>();
    static List<String> urls = new ArrayList<>();
    private static ScheduledExecutorService service;
    private static String title;
    private static String lastHost = "";
    private static int pollingInterval = 10;
    private static boolean taskIsKilled;

    static void chapterPolling() {
        taskIsKilled = false;
        if (!SystemTray.isSupported()) {
            NovelGrabberGUI.appendText("checker", Shared.time() + "System tray not supported!");
            NovelGrabberGUI.stopPolling();
            return;
        }

        List<Integer> latestChapter = new ArrayList<>();
        List<Integer> toBeRemoved = new ArrayList<>();

        //Initializing
        for (int i = 0; i < urls.size(); i++) {
            try {
                lastHost = hosts.get(i);
                NovelGrabberGUI.appendText("checker", Shared.time() + "Initializing: " + urls.get(i));
                latestChapter.add(countChapters(hosts.get(i), urls.get(i)));
                Thread.sleep(2500);
            } catch (IllegalArgumentException | IOException e) {
                toBeRemoved.add(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        toBeRemoved.sort(Comparator.reverseOrder());
        for (int i = 0; i < toBeRemoved.size(); i++) {
            int a = toBeRemoved.get(i);
            NovelGrabberGUI.appendText("checker", Shared.time() + "Removing faulty: " + urls.get(a));
            urls.remove(a);
            hosts.remove(a);

        }
        toBeRemoved.clear();
        NovelGrabberGUI.listModelCheckerLinks.clear();
        for (int i = 0; i < urls.size(); i++) {
            NovelGrabberGUI.listModelCheckerLinks.addElement("Latest chapter: " + latestChapter.get(i) + " / [" + urls.get(i) + "]");
        }

        if (urls.isEmpty()) {
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.appendText("checker", Shared.time() + "No checkers defined.");
            return;
        }
        Runnable runnable = () -> {
            try {
                for (int i = 0; i < urls.size(); i++) {
                    if (lastHost.equals(hosts.get(i))) {
                        NovelGrabberGUI.appendText("checker", Shared.time() + "Same host as last url. Waiting 5 seconds...");
                        Thread.sleep(5000);
                    }
                    NovelGrabberGUI.appendText("checker", Shared.time() + "Polling: " + urls.get(i));
                    int temp = countChapters(hosts.get(i), urls.get(i));
                    if (temp > latestChapter.get(i)) {
                        NovelGrabberGUI.appendText("checker", Shared.time() + "New chapter: " + temp);
                        latestChapter.add(i, temp);
                        NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + latestChapter.get(i) + " / [" + urls.get(i) + "]");
                        showNotification(title + ": " + latestChapter.get(i));
                    }
                    lastHost = hosts.get(i);
                    //testing; sets latest chapter to 1
                    //latestChapter.set(i,1);
                }
                NovelGrabberGUI.appendText("checker", Shared.time() + "Polling again in " + pollingInterval + " minutes.");
            } catch (IllegalArgumentException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        if (!taskIsKilled) {
            NovelGrabberGUI.appendText("checker", Shared.time() + "Polling again in " + pollingInterval + " minutes.");
            service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(runnable, pollingInterval, pollingInterval, TimeUnit.MINUTES);
        } else {
            NovelGrabberGUI.stopPolling();
            NovelGrabberGUI.appendText("checker", Shared.time() + "Stopping polling.");
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
        NovelGrabberGUI.appendText("checker", Shared.time() + "Stopping polling.");
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
