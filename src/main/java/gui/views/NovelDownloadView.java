package gui.views;

import grabber.novel.Novel;
import grabber.novel.NovelMetadata;
import grabber.novel.NovelOptions;
import gui.components.BoundsPopupMenuListener;
import gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

public class NovelDownloadView extends JDialog {

    public NovelDownloadView(NovelMetadata metadata) {
        super();
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.decode("#3b4252"));

        GridBagConstraints c;

        ImageIcon imageIcon;
        if(metadata.getCoverImage() == null) {
            imageIcon = new ImageIcon(getClass().getResource("/images/default_cover.png"));
        } else {
            imageIcon = new ImageIcon(metadata.getCoverImage());
        }
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(150, 200,  Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);

        JLabel imageLbl = new JLabel(imageIcon);
        imageLbl.setPreferredSize(new Dimension(150, 200));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 15, 5, 5);
        add(imageLbl, c);

        JTextArea titleTextArea = new JTextArea(metadata.getTitle());
        titleTextArea.setFont(titleTextArea.getFont().deriveFont(17.0f));
        titleTextArea.setForeground(Color.decode("#8fbcbb"));
        titleTextArea.setOpaque(true);
        titleTextArea.setBorder(null);
        titleTextArea.setEditable(false);
        titleTextArea.setCursor(null);
        titleTextArea.setFocusable(false);
        titleTextArea.setLineWrap(true);
        titleTextArea.setWrapStyleWord(true);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 0, 13);
        add(titleTextArea, c);

        RoundedPanel detailsPanel = new RoundedPanel(new GridBagLayout(), 15, Color.decode("#434c5e"));
        detailsPanel.setForeground(Color.decode("#434c5e"));
        detailsPanel.setOpaque(false);
        TitledBorder detailsPanelBorder =
                BorderFactory.createTitledBorder(new LineBorder(Color.decode("#434c5e"), 10), "Details");
        detailsPanelBorder.setTitleJustification(TitledBorder.LEFT);
        detailsPanelBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        detailsPanelBorder.setTitleColor(Color.decode("#88c0d0"));
        detailsPanel.setBorder(detailsPanelBorder);

        // Author
        JLabel authorLbl = new JLabel("Author:");
        authorLbl.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 0, 0, 10);
        detailsPanel.add(authorLbl, c);

        JLabel authorNameLbl = new JLabel(metadata.getAuthor());
        authorNameLbl.setForeground(Color.decode("#88c0d0"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 0, 0, 0);
        detailsPanel.add(authorNameLbl, c);

        JLabel chapterLbl = new JLabel("Chapters:");
        chapterLbl.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 10);
        detailsPanel.add(chapterLbl, c);

        JLabel chapterCountLbl = new JLabel();
        chapterCountLbl.setText(String.valueOf(metadata.getChapterList().size()));
        chapterCountLbl.setForeground(Color.decode("#88c0d0"));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        detailsPanel.add(chapterCountLbl, c);

        // Tags
        JLabel tagsLbl = new JLabel("Tags:");
        tagsLbl.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.insets = new Insets(0, 0, 0, 10);
        detailsPanel.add(tagsLbl, c);

        JTextArea tagsTextArea = new JTextArea();
        tagsTextArea.setText(String.join(", ", metadata.getSubjects()));
        tagsTextArea.setForeground(Color.decode("#88c0d0"));
        tagsTextArea.setBorder(null);
        tagsTextArea.setEditable(false);
        tagsTextArea.setCursor(null);
        tagsTextArea.setOpaque(false);
        tagsTextArea.setFocusable(false);
        tagsTextArea.setLineWrap(true);
        tagsTextArea.setWrapStyleWord(true);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        detailsPanel.add(tagsTextArea, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 5, 15);
        add(detailsPanel, c);

        // Description
        RoundedPanel descriptionPanel = new RoundedPanel(new GridBagLayout(), 15, Color.decode("#434c5e"));
        descriptionPanel.setForeground(Color.decode("#434c5e"));
        descriptionPanel.setOpaque(false);
        TitledBorder descriptionPanelBorder =
                BorderFactory.createTitledBorder(new LineBorder(Color.decode("#434c5e"), 10), "Description");
        descriptionPanelBorder.setTitleJustification(TitledBorder.LEFT);
        descriptionPanelBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        descriptionPanelBorder.setTitleColor(Color.decode("#88c0d0"));
        descriptionPanel.setBorder(descriptionPanelBorder);

        JTextArea descriptionTextArea = new JTextArea();
        descriptionTextArea.setText(metadata.getDescription());
        descriptionTextArea.setForeground(Color.decode("#eceff4"));
        descriptionTextArea.setBackground(Color.decode("#434c5e"));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setCursor(null);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);

        JScrollPane resultScrollPane = new JScrollPane(descriptionTextArea);
        resultScrollPane.setBorder(null);
        c = new GridBagConstraints();
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 0, 0, 0);
        descriptionPanel.add(resultScrollPane, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 15, 15, 15);
        add(descriptionPanel, c);

        // Options Collection
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBackground(Color.decode("#3b4252"));

        // Novel Options
        RoundedPanel novelOptionsPanel = new RoundedPanel(new GridBagLayout(), 15, Color.decode("#434c5e"));
        novelOptionsPanel.setForeground(Color.decode("#434c5e"));
        novelOptionsPanel.setOpaque(false);
        TitledBorder novelOptionsPanelBorder =
                BorderFactory.createTitledBorder(new LineBorder(Color.decode("#434c5e"), 10), "Novel Options");
        novelOptionsPanelBorder.setTitleJustification(TitledBorder.LEFT);
        novelOptionsPanelBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        novelOptionsPanelBorder.setTitleColor(Color.decode("#88c0d0"));
        novelOptionsPanel.setBorder(novelOptionsPanelBorder);

        JCheckBox createChapterHeadlineCB = new JCheckBox("Create chapter headlines");
        createChapterHeadlineCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 0, 0, 5);
        novelOptionsPanel.add(createChapterHeadlineCB, c);

        JCheckBox createTocCB = new JCheckBox("Create table of contents page");
        createTocCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        novelOptionsPanel.add(createTocCB, c);

        JCheckBox createDescCB = new JCheckBox("Create description page");
        createDescCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        novelOptionsPanel.add(createDescCB, c);

        JCheckBox downloadImagesCB = new JCheckBox("Download images");
        downloadImagesCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTHWEST;
        novelOptionsPanel.add(downloadImagesCB, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        optionPanel.add(novelOptionsPanel, c);

        // Chapter Range
        RoundedPanel chapterRangePanel = new RoundedPanel(new GridBagLayout(), 15, Color.decode("#434c5e"));
        chapterRangePanel.setForeground(Color.decode("#434c5e"));
        chapterRangePanel.setOpaque(false);
        TitledBorder chapterRangePanelBorder =
                BorderFactory.createTitledBorder(new LineBorder(Color.decode("#434c5e"), 10), "Chapter Range");
        chapterRangePanelBorder.setTitleJustification(TitledBorder.LEFT);
        chapterRangePanelBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        chapterRangePanelBorder.setTitleColor(Color.decode("#88c0d0"));
        chapterRangePanel.setBorder(chapterRangePanelBorder);

        JComboBox firstChapterCB = new JComboBox(metadata.getChapterList().toArray());
        BoundsPopupMenuListener firstChapterPopupListener = new BoundsPopupMenuListener(true, false);
        firstChapterCB.addPopupMenuListener(firstChapterPopupListener);
        firstChapterCB.setPrototypeDisplayValue("Chapter 1: Novel Grabber");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 0, 0);
        chapterRangePanel.add(firstChapterCB, c);

        JLabel rangeLbl = new JLabel("— to —");
        rangeLbl.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 5, 0);
        chapterRangePanel.add(rangeLbl, c);

        JComboBox lastChapterCB = new JComboBox(metadata.getChapterList().toArray());
        lastChapterCB.setSelectedIndex(lastChapterCB.getModel().getSize()-1);
        BoundsPopupMenuListener lastChapterPopupListener = new BoundsPopupMenuListener(true, false);
        lastChapterCB.addPopupMenuListener(lastChapterPopupListener);
        lastChapterCB.setPrototypeDisplayValue("Chapter 1: Novel Grabber");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        chapterRangePanel.add(lastChapterCB, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(15, 0, 0, 0);
        optionPanel.add(chapterRangePanel, c);

        // Download Options
        RoundedPanel downloadOptionsPanel = new RoundedPanel(new GridBagLayout(), 15, Color.decode("#434c5e"));
        downloadOptionsPanel.setForeground(Color.decode("#434c5e"));
        downloadOptionsPanel.setOpaque(false);
        TitledBorder downloadOptionsPanelBorder =
                BorderFactory.createTitledBorder(new LineBorder(Color.decode("#434c5e"), 10), "Download");
        downloadOptionsPanelBorder.setTitleJustification(TitledBorder.LEFT);
        downloadOptionsPanelBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        downloadOptionsPanelBorder.setTitleColor(Color.decode("#88c0d0"));
        downloadOptionsPanel.setBorder(downloadOptionsPanelBorder);

        JTextField downloadLocationField = new JTextField("Save to");
        downloadLocationField.setForeground(Color.decode("#4c566a"));
        downloadLocationField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (downloadLocationField.getText().equals("Save to")) {
                    downloadLocationField.setText("");
                    downloadLocationField.setForeground(Color.decode("#eceff4"));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (downloadLocationField.getText().isEmpty()) {
                    downloadLocationField.setForeground(Color.decode("#4c566a"));
                    downloadLocationField.setText("Search");
                }
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 0, 0);
        downloadOptionsPanel.add(downloadLocationField, c);

        JButton downloadSaveLocationBtn = new JButton();
        downloadSaveLocationBtn.setIcon(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        downloadSaveLocationBtn.setBorder(BorderFactory.createEmptyBorder());
        downloadSaveLocationBtn.setContentAreaFilled(false);
        downloadSaveLocationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadSaveLocationBtn.addActionListener(arg0 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                downloadLocationField.setText(chooser.getSelectedFile().toString());
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 0, 0);
        downloadOptionsPanel.add(downloadSaveLocationBtn, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(15, 0, 0, 0);
        optionPanel.add(downloadOptionsPanel, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 0, 15, 15);
        add(optionPanel, c);


        setTitle(metadata.getTitle());
        setSize(new Dimension(750, 550));
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
