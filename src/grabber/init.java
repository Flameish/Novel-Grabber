package grabber;

import gui.GUI;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class init {
    public static String home_path = System.getProperty("user.home");
    public static String home_folder = "Novel-Grabber";
    public static String JSON_Link = "https://raw.githubusercontent.com/Flameish/Novel-Grabber/master/src/files/siteSelector.json";

    public static void main(String[] args) {
        try {
            checkConfigFolder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Accounts.account_Config_Path = home_path + File.separator + home_folder + File.separator + "accounts.json";
            Accounts.readAccounts();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Fetch latest selectors
        HostSettings.fetchSelectors(JSON_Link);
        // Start gui
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                GUI window = new GUI();
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void checkConfigFolder() throws IOException {
        Path path = Paths.get(home_path);

        if (Files.isDirectory(path) && Files.exists(path)) {
            Path myFolder = path.resolve(home_folder);

            if (Files.notExists(myFolder)) {
                Files.createDirectory(myFolder);
            }
        }
    }
}