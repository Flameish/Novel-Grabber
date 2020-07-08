package system;
import grabber.*;
import gui.GUI;
import library.LibrarySystem;
import org.json.simple.parser.ParseException;
import system.persistent.Accounts;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static grabber.GrabberUtils.getDomain;

public class init {
    public static GUI window;

    public static void main(String[] args) {
        final Map<String, List<String>> params = getParamsFromString(args);
        getConfigs();
        processParams(params);
    }

    public static Map<String, List<String>> getParamsFromString(String[] args) {
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (final String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return null;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return null;
            }
        }
        return params;
    }

    public static Novel processParams(Map<String, List<String>> params) {
        if(params.containsKey("gui") || params.isEmpty()) {
            startGUI();
        }
        else if(params.containsKey("help")) {
            printHelp();
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
                        case "edge":
                            novelOptions.browser = "Edge";
                            break;
                        case "opera":
                            novelOptions.browser = "Opera";
                            break;
                        case "ie":
                            novelOptions.browser = "IE";
                            break;
                    }
                    if(params.get("headless").size() > 1) {
                        novelOptions.headlessGUI = params.get("headless").get(1).toLowerCase().equals("gui");
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
                if(params.containsKey("autoGetImages")) {
                    novelOptions.getImages =  true;
                }
                if(params.containsKey("window")) {
                    novelOptions.window =  params.get("window").get(0);
                }
                if(params.containsKey("removeStyle")) {
                    novelOptions.removeStyling =  true;
                }
                if(params.containsKey("noDesc")) {
                    novelOptions.noDescription =  true;
                }
                if(params.containsKey("login")) {
                    novelOptions.useAccount = true;
                }
                if(params.containsKey("account")) {
                    Accounts.setAccount(novelOptions.hostname, params.get("account").get(0), params.get("account").get(1));
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
                    if(autoNovel.options.window.equals("checker")) {
                        autoNovel.metadata.bookTitle = autoNovel.options.firstChapter
                                +"-"+ autoNovel.options.lastChapter
                                +"-"+ autoNovel.metadata.bookTitle;
                    }
                    autoNovel.downloadChapters();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                EPUB epub = new EPUB(autoNovel);
                epub.createCoverPage();
                epub.createToc();
                epub.createDescPage();
                epub.writeEpub();

                autoNovel.report();
                return autoNovel;
            } else {
                System.out.println("No novel link.");
            }
        }
        return null;
    }



    private static void startGUI() {
        // Start gui
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                window = new GUI();
                window.setLocationRelativeTo(null);
                window.setVisible(true);
                LibrarySystem.startPolling();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void getConfigs() {
        try {
            Config.checkConfigFolder();
            Config.readConfig();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        Config.fetchSelectors();
    }

    private static void printHelp() {
        System.out.println("Novel-Grabber is a gui based web scrapper that can download and \n" +
                "convert chapters into EPUB from various supported web/light novel sites \n" +
                "or from any other site manually.\n" +
                "\n" +
                "Usage:\n" +
                "[] = optional paramaters {} = arguments for paramater\n" +
                "  -gui\t\t\t\t\t\tStarts the Graphical User Interface.\n" +
                "  -link {novel URL}\t\t\t\tURL to the novel's table of contents page.\n" +
                "  [-wait] {miliseconds}\t\t\t\tTime between each chapter grab.\n" +
                "  [-headless] {chrome/firefox/opera/edge/IE}\tVisit the website in your browser. Executes javascript etc.\n" +
                "  [-chapters] {all}, {5 27}, {12 last}\t\tSpecify which chapters to download.\n" +
                "  [-path] {directory path}\t\t\tOutput directory for the EPUB.\n" +
                "  [-login]\t\t\t\t\tLog in on website with saved account.\n" +
                "  [-account] {username password}\t\tAdd the account to be used.\n" +
                "  [-displayTitle]\t\t\t\tWrite the chapter title at the top of each chapter text.\n" +
                "  [-invertOrder]\t\t\t\tInvert the chapter order.\n" +
                "  [-noDesc]\t\t\t\t\tDon't create a description page.\n" +
                "  [-autoGetImages]\t\t\t\t\tGrab images from chapter.\n" +
                "  [-removeStyle]\t\t\t\tRemove css styling from chapter.\n" +
                "  \n" +
                "Examples:\n" +
                "java -jar Novel-Grabber.jar -link https://myhost.com/novel/a-novel\n" +
                "java -jar Novel-Grabber.jar -link https://myhost.com/novel/a-novel -chapters 5 last -displayTitle -wait 3000\n" +
                "java -jar Novel-Grabber.jar -link https://myhost.com/novel/a-novel -path /home/flameish/novels -account flameish kovzhvwlmzgv");
    }
}