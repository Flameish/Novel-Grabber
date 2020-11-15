package system;
import com.formdev.flatlaf.FlatIntelliJLaf;
import grabber.*;
import gui.GUI;
import system.library.LibrarySystem;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Initially called class.
 * Handles cli input.
 * Creates GUI instance.
 * Creates LibrarySystem instance.
 */
public class init {
    public static final String versionNumber = "3.2.0";
    public static GUI gui;
    public static LibrarySystem librarySystem;

    public static void main(String[] args) {
        final Map<String, List<String>> params = CLI.createParamsFromArgs(args);
        processParams(params);
    }

    /**
     * Controls program start based on cli parameter.
     */
    public static void processParams(Map<String, List<String>> params) {
        if(params.containsKey("gui") || params.isEmpty()) {
            startGUI();
            librarySystem = new LibrarySystem();
        }
        else if(params.containsKey("help")) {
            printHelp();
        }
        else if(params.containsKey("libraryEnabled")) {
            librarySystem = new LibrarySystem();
        }
        else {
            if(!params.get("link").get(0).isEmpty()) {
                CLI.downloadNovel(params);
            } else {
                System.out.println("No novel link.");
            }
        }
    }

    /**
     * Creates GUI instance.
     */
    private static void startGUI() {
        EventQueue.invokeLater(() -> {
            try {
                System.setProperty("awt.useSystemAAFontSettings","on");
                System.setProperty("swing.aatext", "true");
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                setUIFont (new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,12));
                gui = new GUI();
                gui.setLocationRelativeTo(null);
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    public static void setUIFont(javax.swing.plaf.FontUIResource f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, f);
        }
    }
}