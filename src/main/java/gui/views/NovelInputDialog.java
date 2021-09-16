package gui.views;

import grabber.Grabber;
import grabber.novel.NovelMetadata;
import grabber.sources.Source;
import grabber.sources.SourceException;
import grabber.sources.domains.royalroad_com;
import org.jsoup.HttpStatusException;

import javax.swing.*;
import java.awt.*;

public class NovelInputDialog extends JDialog {

    private final JTextField novelUrlField;
    private final JLabel errorLbl;

    public NovelInputDialog() {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints c;
        // search.Search Panel
        JPanel searchPanel = new JPanel(new GridBagLayout());

        novelUrlField = new JTextField(20);
        novelUrlField.requestFocusInWindow();
        novelUrlField.addActionListener(e -> checkNovelUrl());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
        searchPanel.add(novelUrlField, c);

        JButton novelCheckBtn = new JButton("Fetch");
        novelCheckBtn.addActionListener(actionEvent -> checkNovelUrl());
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 10, 0, 0);
        searchPanel.add(novelCheckBtn, c);

        errorLbl = new JLabel();
        errorLbl.setForeground(Color.red);
        errorLbl.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 0, 0, 0);
        searchPanel.add(errorLbl, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);
        add(searchPanel, c);

        setTitle("Enter novel Url");
        pack();
        setModal(true);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void checkNovelUrl() {
        if (!novelUrlField.getText().isEmpty()) {
            try {
                errorLbl.setText("");
                errorLbl.setVisible(false);
                Grabber grabber = new Grabber();
                NovelMetadata metadata = grabber.fetchNovelDetails(novelUrlField.getText());
                dispose();
                new NovelDownloadView(metadata);
            } catch (SourceException e) {
                if (e.getCause() instanceof HttpStatusException) {
                    HttpStatusException httpException = (HttpStatusException) e.getCause();
                    int statusCode = httpException.getStatusCode();
                    String httpErrorMsg = String.valueOf(statusCode);
                    if (400 <= statusCode && statusCode < 500 ) {
                        httpErrorMsg = "Access forbidden!";
                    }
                    if (500 <= statusCode && statusCode < 600 ) {
                        httpErrorMsg = "Server forbidden!";
                    }
                    errorLbl.setText("<html><p style='text-align:center; width:250px'>" + httpErrorMsg + "</p></html>");
                } else {
                    errorLbl.setText("<html><p style='text-align:center; width:250px'>" + e.getMessage() + "</p></html>");
                }
                errorLbl.setVisible(true);
                pack();
            }
        }

    }
}
