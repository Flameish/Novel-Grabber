package gui;


import gui.views.AutomaticView;
import gui.views.NovelDownloadView;
import gui.views.SearchView;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private final AutomaticView automaticView;
    private final SearchView searchView;

    public GUI() {
        super("Novel Grabber 3.0");
        getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints c;

        JTabbedPane tabbedPane = new JTabbedPane();
        automaticView = new AutomaticView();
        searchView = new SearchView();
        tabbedPane.add("Automatic", automaticView);
        tabbedPane.add("Search", searchView);
        c = new GridBagConstraints();
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        getContentPane().add(tabbedPane, c);




        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 720);
        setLocationRelativeTo(null);
    }

    public void addAutoDownload(NovelDownloadView downloadView) {
        automaticView.addDownloadTile(downloadView);
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
