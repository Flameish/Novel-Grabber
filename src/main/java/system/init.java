package system;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;
import gui.GUI;

import javax.swing.*;
import java.awt.*;

public class init {

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


}
