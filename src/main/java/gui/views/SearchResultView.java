package gui.views;

import grabber.Grabber;
import grabber.novel.NovelMetadata;
import grabber.sources.Source;
import grabber.sources.SourceException;
import grabber.sources.domains.royalroad_com;
import search.SearchResult;

import javax.swing.*;
import java.awt.*;

public class SearchResultView extends JDialog {

    private final JButton downloadStartBtn;
    private final SearchResult searchResult;

    public SearchResultView(SearchResult searchResult) {
        super();
        setLayout(new GridBagLayout());
        this.searchResult = searchResult;

        GridBagConstraints c;

        ImageIcon imageIcon;
        if(searchResult.getCoverImage() == null) {
            imageIcon = new ImageIcon(getClass().getResource("/images/default_cover.png"));
        } else {
            imageIcon = new ImageIcon(searchResult.getCoverImage());
        }
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(150, 200,  Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);  // transform it back

        JLabel imageLbl = new JLabel(imageIcon);
        imageLbl.setPreferredSize(new Dimension(150, 200));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 13, 5, 5);
        add(imageLbl, c);

        JPanel detailsPanel = new JPanel(new GridBagLayout());

        JLabel titleLbl = new JLabel("<html><p style=\"width:300px\">" + searchResult.getTitle() + "</p></html>");
        titleLbl.setFont(titleLbl.getFont().deriveFont(17.0f));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(titleLbl, c);

        JLabel authorLbl = new JLabel("Author: " + searchResult.getAuthor());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 0, 0);
        detailsPanel.add(authorLbl, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0f;
        c.insets = new Insets(10, 5, 5, 5);
        add(detailsPanel, c);

        downloadStartBtn = new JButton("Download");
        downloadStartBtn.setFocusable(false);
        downloadStartBtn.addActionListener(e -> openInDownloadView());
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.insets = new Insets(10, 5, 5, 12);
        add(downloadStartBtn, c);

        JTextArea descriptionTextArea = new JTextArea();
        descriptionTextArea.setText(searchResult.getDescription());
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setCursor(null);
        descriptionTextArea.setOpaque(false);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);

        JScrollPane resultScrollPane = new JScrollPane(descriptionTextArea);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder("Description"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 10, 10);
        add(resultScrollPane, c);

        setTitle("Search result - " + searchResult.getTitle());
        setSize(new Dimension(750, 550));
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openInDownloadView() {
        try {
            Grabber grabber = new Grabber();
            NovelMetadata metadata = grabber.fetchNovelDetails(searchResult.getUrl());
            dispose();
            new NovelDownloadView(metadata);
        } catch (SourceException e) {
            downloadStartBtn.setText("Error");
            downloadStartBtn.setForeground(Color.red);
        }
    }
}
