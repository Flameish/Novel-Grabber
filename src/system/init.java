package system;
import grabber.*;
import gui.GUI;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class init {
    public static GUI window;

    public static void main(String[] args) {
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (final String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return;
            }
        }
        getConfigs();
        processParams(params);
    }


    private static void startGUI() {
        // Start gui
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                window = new GUI();
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void getConfigs() {
        try {
            Config.checkConfigFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Accounts.readAccounts();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Fetch latest selectors
        Config.fetchSelectors();
    }

    public static String getDomain(String url) {
        for(Object key: Config.siteSelectorsJSON.keySet()) {
            Object keyvalue = Config.siteSelectorsJSON.get(key);
            String keyUrl = ((JSONObject) keyvalue).get("url").toString();
            if(!keyUrl.trim().isEmpty()) {
                if(getDomainName(url).equals(getDomainName(keyUrl)))
                    return key.toString();
            }
        }
        return null;
    }

    public static String getDomainName(String url)  {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            System.out.println(url);
            e.printStackTrace();
        }
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    private static void processParams(Map<String, List<String>> params) {
        if(params.containsKey("gui") || params.isEmpty()) {
            startGUI();
        } else {
            if(!params.get("link").get(0).isEmpty()) {
                NovelOptions novelOptions = new NovelOptions();

                novelOptions.novelLink = params.get("link").get(0);
                novelOptions.hostname = getDomain(params.get("link").get(0).
                        substring(0, GrabberUtils.ordinalIndexOf(params.get("link").get(0), "/", 3) + 1));
                novelOptions.window = "auto";
                novelOptions.displayChapterTitle = params.containsKey("displayTitle");
                novelOptions.invertOrder = params.containsKey("invertOrder");

                if(params.containsKey("headless")) {
                    novelOptions.headless = true;
                    String browser = params.get("headless").get(0).toLowerCase();
                    switch (browser) {
                        case "firefox":
                            novelOptions.browser = "Firefox";
                            break;
                        case "chrome":
                            novelOptions.browser = "Chrome";
                            break;
                        case "Edge":
                            novelOptions.browser = "Edge";
                            break;
                        case "Opera":
                            novelOptions.browser = "Opera";
                            break;
                        case "IE":
                            novelOptions.browser = "IE";
                            break;
                    }
                }
                if(params.containsKey("path")) {
                    novelOptions.saveLocation = params.get("path").get(0);
                } else {
                    Path currentRelativePath = Paths.get("");
                    novelOptions.saveLocation = currentRelativePath.toAbsolutePath().toString();
                }
                if(params.containsKey("wait")) {
                    novelOptions.waitTime =  Integer.parseInt(params.get("wait").get(0));
                }
                if(params.containsKey("account")) {
                    Accounts.addAccount(novelOptions.hostname, params.get("account").get(0), params.get("account").get(1));
                    novelOptions.useAccount = true;
                }

                Novel autoNovel = new Novel(novelOptions);
                autoNovel.getChapterList();
                autoNovel.getMetadata();
                if(params.containsKey("chapters")) {
                    if(params.get("chapters").get(0).equals("all")) {
                        autoNovel.options.firstChapter = 1;
                        autoNovel.options.lastChapter = autoNovel.chapters.size();
                    } else {
                        autoNovel.options.firstChapter = Integer.parseInt(params.get("chapters").get(0));
                        if(params.get("chapters").get(1).equals("last")) {
                            autoNovel.options.lastChapter = autoNovel.chapters.size();
                        } else {
                            autoNovel.options.lastChapter = Integer.parseInt(params.get("chapters").get(1));
                        }
                    }
                } else {
                    autoNovel.options.firstChapter = 1;
                    autoNovel.options.lastChapter = autoNovel.chapters.size();
                }
                try {
                    autoNovel.downloadChapters();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autoNovel.createCoverPage();
                autoNovel.createToc();
                autoNovel.createDescPage();
                autoNovel.createEPUB();
                autoNovel.report();
                System.out.println("[INFO]Output: "+novelOptions.saveLocation+ autoNovel.metadata.bookAuthor + " - " + autoNovel.metadata.bookTitle + ".epub");
            } else {
                System.out.println("No novel link.");
            }
        }
    }
}