package gui;


import gui.views.AutomaticView;
import gui.views.SearchView;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    public GUI() {
        super("Novel Grabber 3.0");
        getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints c;

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Automatic", new AutomaticView());
        tabbedPane.add("Search", new SearchView());
        c = new GridBagConstraints();
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        getContentPane().add(tabbedPane, c);




        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 720);
        setLocationRelativeTo(null);
    }



    public void addAutoDownload() {

    }

    // Set font for each swing element
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
