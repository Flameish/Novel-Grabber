import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

class updater {

    static void updateJar() {
        String oldVersionString = NovelGrabberGUI.versionNumber;
        String newVersionString = getLatestVersionString();
        if (compareStrings(oldVersionString, newVersionString) == -1) {
            String jarLink = "https://github.com/Flameish/Novel-Grabber/releases/download/" + newVersionString + "/Novel-Grabber.jar";
            try {
                downloadFileFromGitHub(jarLink);
                startNovelGrabber();
                System.exit(0);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            NovelGrabberGUI.appendText("update", "Novel-Grabber is up-to-date.");
        }
    }

    /**
     * Gets the latest version from Github releases as a String.
     */
    private static String getLatestVersionString() {
        try {
            NovelGrabberGUI.updateProcessLbl.setText("Checking new releases...");
            Document doc = Jsoup.connect("https://github.com/Flameish/Novel-Grabber/releases").get();
            Element content = doc.select("a[title]").first();
            return content.attr("title");
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    // Compare version strings
    static int compareStrings(String oldVersionString, String newVersionString) {
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
        NovelGrabberGUI.updateProcessLbl.setText("Starting Novel-Grabber...");
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
        NovelGrabberGUI.updateProcessLbl.setText("Downloading new release...");
        String link = URL;
        String fileName = "Novel-Grabber.jar";
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
        byte[] buffer = new byte[4096];
        int n;
        try (OutputStream output = new FileOutputStream(new File(fileName))) {
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
            }
        }
    }
}
