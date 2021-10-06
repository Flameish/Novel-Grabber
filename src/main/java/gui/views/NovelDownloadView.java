package gui.views;

import grabber.Grabber;
import grabber.GrabberOptions;
import grabber.helper.Utils;
import grabber.novel.Chapter;
import grabber.novel.Novel;
import grabber.novel.NovelMetadata;
import grabber.novel.NovelOptions;
import grabber.sources.SourceException;
import gui.components.BoundsPopupMenuListener;
import gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.Executors;

public class NovelDownloadView extends JDialog {

    private JButton downloadStartBtn;
    private JButton downloadStopBtn;
    private JButton downloadContinueBtn;
    private NovelMetadata metadata;
    private JSlider waitTimeSlider;
    private JLabel pagesLbl;
    private JLabel pagesCountLbl;
    private JCheckBox createChapterHeadlineCB;
    private JCheckBox createTocCB;
    private JCheckBox createDescCB;
    private JCheckBox downloadImagesCB;
    private JComboBox startingChapterCB;
    private JComboBox endingChapterCB;
    private JProgressBar downloadProgressBar;
    private JButton downloadCancelBtn;
    private JLabel errorLbl;
    private JTextArea descriptionTextArea;
    private JButton imageLbl;
    private JButton titleTextArea;
    private JButton authorNameLbl;
    private JLabel chapterCountLbl;
    private JTextArea tagsTextArea;
    private int pageCounter;
    private Grabber grabber;
    private Novel novel;

    public NovelDownloadView(NovelMetadata metadata) {
        super();
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.decode("#3b4252"));

        this.metadata = metadata;

        GridBagConstraints c;

        // Cover
        imageLbl = new JButton();
        setCoverImage(metadata.getCoverImage());
        imageLbl.setPreferredSize(new Dimension(150, 200));
        imageLbl.setToolTipText("Change Cover");
        imageLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageLbl.setBorder(BorderFactory.createEmptyBorder());
        imageLbl.setContentAreaFilled(false);
        // Edit cover
        imageLbl.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Change Cover");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".jpg .png .gif", "png", "jpg", "gif", "jpeg");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try (InputStream inputStream = new FileInputStream(chooser.getSelectedFile())) {
                    byte[] newCoverImage = Utils.getBytesFromInputStream(inputStream);
                    String coverName = chooser.getSelectedFile().toString();
                    setCoverImage(newCoverImage);
                    metadata.setCoverImage(coverName, newCoverImage);
                } catch (IOException ioException) {
                    JOptionPane.showConfirmDialog(
                            null,
                            "Could not load new cover image: " + ioException.getMessage(),
                            "Error",
                            JOptionPane.CANCEL_OPTION);
                }
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 15, 5, 5);
        add(imageLbl, c);

        // Title
        titleTextArea = new JButton(metadata.getTitle());
        titleTextArea.setFont(titleTextArea.getFont().deriveFont(17.0f));
        titleTextArea.setForeground(Color.decode("#8fbcbb"));
        titleTextArea.setToolTipText("Edit Title");
        titleTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleTextArea.setBorder(BorderFactory.createEmptyBorder());
        titleTextArea.setContentAreaFilled(false);
        // Edit popup
        titleTextArea.addActionListener(e -> {
            JLabel infoLabel = new JLabel("Title");
            infoLabel.setForeground(Color.decode("#88c0d0"));
            JTextField inputField = new JTextField(metadata.getTitle());
            Object[] message = {
                    infoLabel,
                    inputField
            };
            int result = JOptionPane.showConfirmDialog(
                    null,
                    message,
                    "Edit Title",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            // Update Title
            if (result == 0 && !inputField.getText().isEmpty()) {
                String newTitle = inputField.getText();
                metadata.setTitle(newTitle);
                titleTextArea.setText(newTitle);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
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

        authorNameLbl = new JButton(metadata.getAuthor());
        authorNameLbl.setForeground(Color.decode("#88c0d0"));
        authorNameLbl.setToolTipText("Edit Author");
        authorNameLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        authorNameLbl.setBorder(BorderFactory.createEmptyBorder());
        authorNameLbl.setContentAreaFilled(false);
        authorNameLbl.addActionListener(e -> {
            JLabel infoLabel = new JLabel("Author");
            infoLabel.setForeground(Color.decode("#88c0d0"));
            JTextField inputField = new JTextField(metadata.getAuthor());
            Object[] message = {
                    infoLabel,
                    inputField
            };
            int result = JOptionPane.showConfirmDialog(
                    null,
                    message,
                    "Edit Author",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result == 0 && !inputField.getText().isEmpty()) {
                String newAuthor = inputField.getText();
                metadata.setAuthor(newAuthor);
                authorNameLbl.setText(newAuthor);
            }
        });
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

        chapterCountLbl = new JLabel(String.valueOf(metadata.getChapterList().size()));
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

        tagsTextArea = new JTextArea();
        tagsTextArea.setText(String.join(", ", metadata.getSubjects()));
        tagsTextArea.setForeground(Color.decode("#88c0d0"));
        tagsTextArea.setBorder(null);
        tagsTextArea.setEditable(false);
        tagsTextArea.setCursor(null);
        tagsTextArea.setOpaque(false);
        tagsTextArea.setFocusable(false);
        tagsTextArea.setLineWrap(true);
        tagsTextArea.setWrapStyleWord(true);
        tagsTextArea.setToolTipText("Edit Tags");
        tagsTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tagsTextArea.setBorder(BorderFactory.createEmptyBorder());
        tagsTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel infoLabel = new JLabel("Tags");
                infoLabel.setForeground(Color.decode("#88c0d0"));

                DefaultListModel<String> tagListModel = new DefaultListModel<>();
                metadata.getSubjects().forEach(tagListModel::addElement);
                JList<String> tagList = new JList<>(tagListModel);
                JScrollPane tagScrollPane = new JScrollPane(tagList);
                tagScrollPane.setPreferredSize(new Dimension(300, 300));

                JPanel addPanel = new JPanel(new GridBagLayout());

                JTextField addField = new JTextField();
                GridBagConstraints c = new GridBagConstraints();
                c.weightx = 1.0f;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = new Insets(0,0,0,0);
                addPanel.add(addField, c);

                JButton addBtn = new JButton("Add tag");
                addBtn.addActionListener(al -> {
                    String newTag = addField.getText();
                    if (newTag != null) {
                        tagListModel.addElement(newTag);
                    }
                });
                c = new GridBagConstraints();
                c.insets = new Insets(0,5,0,0);
                addPanel.add(addBtn, c);

                JButton removeBtn = new JButton("Remove selected");
                removeBtn.addActionListener(al -> {
                    for (String tag: tagList.getSelectedValuesList()) {
                        tagListModel.removeElement(tag);
                    }
                });
                Object[] message = {
                        infoLabel,
                        addPanel,
                        tagScrollPane,
                        removeBtn
                };
                int result = JOptionPane.showConfirmDialog(
                        null,
                        message,
                        "Edit Tags",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (result == 0) {
                    metadata.setSubjects(Collections.list(tagListModel.elements()));
                    tagsTextArea.setText(String.join(", ", metadata.getSubjects()));
                }
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        detailsPanel.add(tagsTextArea, c);

        pagesLbl = new JLabel("Pages:");
        pagesLbl.setForeground(Color.decode("#eceff4"));
        pagesLbl.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 10);
        detailsPanel.add(pagesLbl, c);

        pagesCountLbl = new JLabel();
        pagesCountLbl.setForeground(Color.decode("#88c0d0"));
        pagesCountLbl.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        detailsPanel.add(pagesCountLbl, c);

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

        descriptionTextArea = new JTextArea();
        descriptionTextArea.setText(metadata.getDescription());
        descriptionTextArea.setForeground(Color.decode("#eceff4"));
        descriptionTextArea.setBackground(Color.decode("#434c5e"));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setCursor(null);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        descriptionTextArea.setBorder(BorderFactory.createEmptyBorder());
        descriptionTextArea.setToolTipText("Edit Description");
        descriptionTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel infoLabel = new JLabel("Description");
                infoLabel.setForeground(Color.decode("#88c0d0"));
                JTextField inputField = new JTextField(metadata.getAuthor());
                Object[] message = {
                        infoLabel,
                        inputField
                };
                int result = JOptionPane.showConfirmDialog(
                        null,
                        message,
                        "Edit Author",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (result == 0 && !inputField.getText().isEmpty()) {
                    String newAuthor = inputField.getText();
                    metadata.setAuthor(newAuthor);
                    authorNameLbl.setText(newAuthor);
                }
            }
        });

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

        createChapterHeadlineCB = new JCheckBox("Create chapter headlines");
        createChapterHeadlineCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 0, 0, 5);
        novelOptionsPanel.add(createChapterHeadlineCB, c);

        createTocCB = new JCheckBox("Create table of contents page");
        createTocCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        novelOptionsPanel.add(createTocCB, c);

        createDescCB = new JCheckBox("Create description page");
        createDescCB.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        novelOptionsPanel.add(createDescCB, c);

        downloadImagesCB = new JCheckBox("Download images");
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

        startingChapterCB = new JComboBox<>(metadata.getChapterList().toArray());
        BoundsPopupMenuListener firstChapterPopupListener = new BoundsPopupMenuListener(true, false);
        startingChapterCB.addPopupMenuListener(firstChapterPopupListener);
        startingChapterCB.setPrototypeDisplayValue("Chapter 1: Novel Grabber");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 0, 0);
        chapterRangePanel.add(startingChapterCB, c);

        JLabel rangeLbl = new JLabel("— to —");
        rangeLbl.setForeground(Color.decode("#eceff4"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 0, 5, 0);
        chapterRangePanel.add(rangeLbl, c);

        endingChapterCB = new JComboBox<>(metadata.getChapterList().toArray());
        endingChapterCB.setSelectedIndex(endingChapterCB.getModel().getSize()-1);
        BoundsPopupMenuListener lastChapterPopupListener = new BoundsPopupMenuListener(true, false);
        endingChapterCB.addPopupMenuListener(lastChapterPopupListener);
        endingChapterCB.setPrototypeDisplayValue("Chapter 1: Novel Grabber");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        chapterRangePanel.add(endingChapterCB, c);

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

        JPanel saveLocationPanel = new JPanel(new GridBagLayout());
        saveLocationPanel.setBackground(Color.decode("#434c5e"));

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
        saveLocationPanel.add(downloadLocationField, c);

        JButton downloadSaveLocationBtn = new JButton();
        downloadSaveLocationBtn.setIcon(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        downloadSaveLocationBtn.setBorder(BorderFactory.createEmptyBorder());
        downloadSaveLocationBtn.setContentAreaFilled(false);
        downloadSaveLocationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadSaveLocationBtn.setToolTipText("Browse locations");
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
        c.insets = new Insets(0, 5, 0, 0);
        saveLocationPanel.add(downloadSaveLocationBtn, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 0, 0);
        downloadOptionsPanel.add(saveLocationPanel, c);

        waitTimeSlider = new JSlider(JSlider.HORIZONTAL,0,10,0);
        waitTimeSlider.setSnapToTicks(true);
        waitTimeSlider.setPaintTicks(true);
        waitTimeSlider.setPaintLabels(true);
        waitTimeSlider.setMinorTickSpacing(1);
        waitTimeSlider.setMajorTickSpacing(5);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("0"));
        labels.put(5, new JLabel("Wait Time (0 s)"));
        labels.put(10, new JLabel("10"));
        waitTimeSlider.setLabelTable(labels);
        waitTimeSlider.addChangeListener(e -> {
            JLabel middleLbl = (JLabel) waitTimeSlider.getLabelTable().get(5);
            middleLbl.setText(String.format("Wait Time (%d s)", waitTimeSlider.getValue()));
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(waitTimeSlider, c);

        JSeparator downloadSeparator = new JSeparator();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(downloadSeparator, c);

        downloadStartBtn = new JButton("Start Download");
        downloadStartBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(this::startDownload));
        downloadStartBtn.setBackground(Color.decode("#8fbcbb"));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(downloadStartBtn, c);

        downloadStopBtn = new JButton("Stop");
        downloadStopBtn.addActionListener(e -> stopDownload());
        downloadStopBtn.setBackground(Color.decode("#bf616a"));
        downloadStopBtn.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(downloadStopBtn, c);

        downloadContinueBtn = new JButton("Continue");
        downloadContinueBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(this::continueDownload));
        downloadContinueBtn.setBackground(Color.decode("#8fbcbb"));
        downloadContinueBtn.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(downloadContinueBtn, c);

        downloadCancelBtn = new JButton("Cancel");
        downloadCancelBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(this::cancelDownload));
        downloadCancelBtn.setBackground(Color.decode("#bf616a"));
        downloadCancelBtn.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 0, 0);
        downloadOptionsPanel.add(downloadCancelBtn, c);

        downloadProgressBar = new JProgressBar();
        downloadProgressBar.setVisible(false);
        downloadProgressBar.setStringPainted(true);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(downloadProgressBar, c);

        errorLbl = new JLabel();
        errorLbl.setForeground(Color.red);
        errorLbl.setVisible(false);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);
        downloadOptionsPanel.add(errorLbl, c);

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
        setSize(new Dimension(850, 600));
        setModal(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setCoverImage(byte[] imageBytes) {
        ImageIcon coverImageIcon;
        if(imageBytes == null) {
            coverImageIcon = new ImageIcon(getClass().getResource("/images/default_cover.png"));
        } else {
            coverImageIcon = new ImageIcon(imageBytes);
        }
        Image image = coverImageIcon.getImage();
        Image scaledImage = image.getScaledInstance(150, 200,  Image.SCALE_SMOOTH);
        imageLbl.setIcon(new ImageIcon(scaledImage));
    }

    public void startDownload() {
        downloadStartBtn.setVisible(false);
        downloadStopBtn.setVisible(true);
        pagesLbl.setVisible(true);
        pagesCountLbl.setVisible(true);
        pageCounter = 0;

        int startingChapterIndex = startingChapterCB.getSelectedIndex();
        int endingChapterIndex = endingChapterCB.getSelectedIndex();
        // Reverse the chapter list if the starting chapter is higher than the ending chapter
        // and switch starting / ending index
        if (startingChapterIndex > endingChapterIndex) {
            Collections.reverse(metadata.getChapterList());
            int size = metadata.getChapterList().size();
            endingChapterIndex  = size - startingChapterCB.getSelectedIndex() -1;
            startingChapterIndex = size - endingChapterCB.getSelectedIndex() -1;
        }

        NovelOptions novelOptions = NovelOptions.builder()
                .startingChapterIndex(startingChapterIndex)
                .endingChapterIndex(endingChapterIndex)
                .displayNovelToc(createTocCB.isSelected())
                .displayNovelDesc(createDescCB.isSelected())
                .displayChapterTitle(createChapterHeadlineCB.isSelected())
                .getImages(downloadImagesCB.isSelected())
                .build();
        novel = new Novel(metadata);
        novel.setOptions(novelOptions);

        downloadProgressBar.setVisible(true);
        downloadProgressBar.setMinimum(0);
        downloadProgressBar.setMaximum(novel.getChaptersToDownloadCount());
        downloadProgressBar.setValue(0);

        GrabberOptions grabberOptions = GrabberOptions.builder()
                .waitTime(waitTimeSlider.getValue())
                .build();
        grabber = new Grabber(grabberOptions);
        grabber.addCPListener(obj -> {
            updatePageCounter(obj.getChapter().getWordCount());
            downloadProgressBar.setValue(downloadProgressBar.getValue() + 1);
        });
        try {
            grabber.downloadNovel(novel);
        } catch (SourceException e) {
            e.printStackTrace();
            errorLbl.setText("<html><p style='text-align:center; width:150px'>" + e.getMessage() + "</p></html>");
            errorLbl.setVisible(true);
            downloadProgressBar.setVisible(false);
        }
        // Reverse chapter order back
        if (startingChapterCB.getSelectedIndex() > endingChapterCB.getSelectedIndex()) {
            Collections.reverse(metadata.getChapterList());
        }
    }

    public void stopDownload() {
        grabber.stopDownload();
        downloadStopBtn.setVisible(false);
        downloadContinueBtn.setVisible(true);
        downloadCancelBtn.setVisible(true);
    }

    public void continueDownload() {
        downloadContinueBtn.setVisible(false);
        downloadCancelBtn.setVisible(false);
        downloadStopBtn.setVisible(true);
        try {
            grabber.downloadNovel(novel);
        } catch (SourceException e) {
            e.printStackTrace();
        }
    }

    public void cancelDownload() {
        downloadContinueBtn.setVisible(false);
        downloadCancelBtn.setVisible(false);
        downloadStartBtn.setVisible(true);
        pagesLbl.setVisible(false);
        pagesCountLbl.setVisible(false);
        downloadProgressBar.setVisible(false);
        pageCounter = 0;
        pagesCountLbl.setText("");
        metadata.resetAllChapterStatus();
    }

    public void updatePageCounter(int pages) {
        pageCounter += pages;
        pagesCountLbl.setText(String.valueOf(pageCounter / 300));
    }

}
