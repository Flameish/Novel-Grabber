package updater;

import gui.GUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class Updater {

    public static void updateJar() {
        String oldVersionString = GUI.versionNumber;
        String newVersionString = getLatestVersionString();
        if (compareStrings(oldVersionString, newVersionString) == -1) {
            String jarLink = "https://github.com/Flameish/Novel-Grabber/releases/download/" + newVersionString + "/Novel-Grabber.jar";
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    downloadFileFromGitHub(jarLink);
                    startNovelGrabber();
                    System.exit(0);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
    }

    /**
     * Gets the latest version from Github releases as a String.
     */
    private static String getLatestVersionString() {
        try {
            Document doc = Jsoup.connect("https://github.com/Flameish/Novel-Grabber/releases").get();
            Element content = doc.select("a[title]").first();
            return content.attr("title");
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    // Compare version strings
    public static int compareStrings(String oldVersionString, String newVersionString) {
        String[] oldStrings = oldVersionString.split("\\.");
        String[] newStrings = newVersionString.split("\\.");
        int length = Math.max(oldStrings.length, newStrings.length);
        for (int i = 0; i < length; i++) {
            int oldVersionNumber = i < oldStrings.length ? Integer.parseInt(oldStrings[i]) : 0;
            int newVersionNumber = i < newStrings.length ? Integer.parseInt(newStrings[i]) : 0;
            if (oldVersionNumber < newVersionNumber)
                return -1;
            if (oldVersionNumber > newVersionNumber)
                return 1;
        }
        return 0;
    }

    /**
     * Starts the new downloaded Novel-Grabber.jar
     */
    private static void startNovelGrabber() {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("java -jar Novel-Grabber.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();
    }

    private static boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains("301") || hv.contains("302")) return true;
        }
        return false;
    }

    private static void downloadFileFromGitHub(String URL) throws Throwable {
        String link = URL;
        Path currentRelativePath = Paths.get("");
        currentRelativePath.toAbsolutePath().toString();
        String fileName = currentRelativePath.toAbsolutePath().toString() + File.separator + "Novel-Grabber.jar";
        java.net.URL url = new URL(link);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> header = http.getHeaderFields();
        while (isRedirected(header)) {
            link = header.get("Location").get(0);
            url = new URL(link);
            http = (HttpURLConnection) url.openConnection();
            header = http.getHeaderFields();
        }
        InputStream input = http.getInputStream();
        byte[] buffer = new byte[256000];
        int n;
        try (OutputStream output = new FileOutputStream(new File(fileName))) {
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
        }
    }
}
