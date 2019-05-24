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

    static void chapterPolling() {
        if (!SystemTray.isSupported()) {
            System.err.println(Shared.time() + "System tray not supported!");
            return;
        }
        if (urls.isEmpty()) {
            return;
        }
        System.out.println(hosts);
        System.out.println(urls);
        List<Integer> latestChapter = new ArrayList<>();
        List<Integer> toBeRemoved = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            try {
                System.out.println(Shared.time() + "Initializing: " + urls.get(i));
                latestChapter.add(countChapters(hosts.get(i), urls.get(i)));
            } catch (IllegalArgumentException | IOException e) {
                toBeRemoved.add(i);
            } finally {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        toBeRemoved.sort(Comparator.reverseOrder());
        for (int i = 0; i < toBeRemoved.size(); i++) {
            int a = toBeRemoved.get(i);
            System.out.println(Shared.time() + "Removing faulty: " + urls.get(a));
            urls.remove(a);
            hosts.remove(a);

        }
        toBeRemoved.clear();
        NovelGrabberGUI.listModelCheckerLinks.clear();
        for (String url : urls) {
            NovelGrabberGUI.listModelCheckerLinks.addElement(url);
        }

        Runnable runnable = () -> {
            try {
                String lastHost = "";
                for (int i = 0; i < urls.size(); i++) {
                    if (lastHost.equals(hosts.get(i))) {
                        System.out.println(Shared.time() + "Same host as last url. Waiting 5 seconds...");
                        Thread.sleep(5000);
                    }
                    System.out.println(Shared.time() + "Polling: " + urls.get(i));
                    int temp = countChapters(hosts.get(i), urls.get(i));
                    System.out.println(Shared.time() + "Latest chapter: " + temp);
                    NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + latestChapter.get(i) + " / " + urls.get(i));
                    if (temp > latestChapter.get(i)) {
                        latestChapter.add(i, temp);
                        NovelGrabberGUI.listModelCheckerLinks.set(i, "Latest chapter: " + latestChapter.get(i) + " / " + urls.get(i));
                        showNotification(title + ": " + latestChapter.get(i));
                    }
                    lastHost = hosts.get(i);
                    //testing; sets latest chapter to 1
                    //latestChapter.set(i,1);
                }
            } catch (IllegalArgumentException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 1, 20, TimeUnit.MINUTES);
    }

    static void killTask() {
        service.shutdown();
        try {
            if (!service.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
        System.out.println(Shared.time() + "Stopping polling.");
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
