package system;

import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;
import gui.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;

public class App {
    private static String HOME_PATH;
    private static String CACHE_PATH;

    public static void main(String[] args) {

        showGUI();

    }

    public static void showGUI() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.setProperty("awt.useSystemAAFontSettings","on");
                System.setProperty("swing.aatext", "true");
                UIManager.setLookAndFeel(new FlatNordIJTheme());
                GUI.setUIFont(new javax.swing.plaf.FontUIResource("DejaVu Sans", Font.PLAIN,13));
                GUI gui = new GUI();
                gui.setVisible(true);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }

    public static void checkFolderStructure() {
        try {
            HOME_PATH = getHomePath();
            if (!checkWritePermissions(HOME_PATH)) {
                Logger.error(String.format("No write permissions in %s! Existing.", HOME_PATH));
                System.exit(0);
            }
            CACHE_PATH = HOME_PATH + "/cache";
        } catch (URISyntaxException e) {
            Logger.error(e.getMessage());
            System.exit(0);
        }
    }

    public static String getHomePath() throws URISyntaxException {
        File jarFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        return jarFile.getParentFile().getPath();
    }

    public static boolean checkWritePermissions(String path) {
        File file = new File(path);
        return file.canWrite();
    }


}
