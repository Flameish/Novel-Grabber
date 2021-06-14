package gui;

import bots.telegram.Bot;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import grabber.*;
import grabber.sources.Source;
import org.openqa.selenium.Cookie;
import grabber.Accounts;
import library.Library;
import library.LibraryNovel;
import system.Config;
import system.init;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    public static DefaultListModel<Chapter> manLinkListModel = new DefaultListModel<>();
    public static DefaultListModel<String> accountWebsiteListModel = new DefaultListModel<>();
    public static DefaultListModel<Source> sourcesListModel = new DefaultListModel<>();
    public static DefaultListModel<String> settingsMenuModel = new DefaultListModel<>();
    public static List<String> blacklistedTags = new ArrayList<>();
    private Driver guiDriver;
    public static TrayIcon trayIcon;
    public static Integer chapterToChapterNumber = 1;
    private static String[] epubFilenameFormats = {"<author> - <title>", "<title> - <author>", "<title>"};
    private static String[] epubFormats = {"EPUB", "txt", "PDF"};
    private static String[] sslList = {"SMTP","SMTPS","SMTP TLS"};
    private static String[] chapterTitleFormatOptions = {"span","h1","custom"};
    private static String[] guiThemes = {"Flat IntelliJ", "Flat Light","Flat Dark","Flat Darcula"};
    private static MenuItem defaultItem0;
    private final String NL = System.getProperty("line.separator");
    private static final Config settings = Config.getInstance();
    private static final Library library = Library.getInstance();
    public static Novel autoNovel = null;
    public static Novel manNovel = new Novel();
    public JTextField chapterListURL;
    public JTextField autoSaveLocation;
    public JCheckBox chapterAllCheckBox;
    public JSpinner firstChapter;
    public JSpinner lastChapter;
    public JCheckBox toLastChapter;
    public JCheckBox autoGetImages;
    public JCheckBox checkInvertOrder;
    public JTextField waitTime;
    public JProgressBar progressBar;
    public JButton autoGetNumberButton;
    public JTextField manNovelURL;
    public JLabel autoBookTitle;
    public JLabel autoAuthor;
    public JLabel autoChapterAmount;
    public JTextField manWaitTime;
    public JCheckBox manGetImages;
    public JCheckBox manInvertOrder;
    public JTextField manSaveLocation;
    public JTextField manChapterContainer;
    public JLabel updaterStatus;
    public JLabel checkStatusLbl;
    public JButton checkStopPollingBtn;
    public JLabel autoBookSubjects;
    private JList<Chapter> manLinkList;
    private JFrame window;
    private JTabbedPane tabbedPane;
    private JPanel rootPanel;
    private JButton browseButton;
    private JButton autoVisitButton;
    private JButton autoShowBlacklistedTagsBtn;
    private JLabel coverImage;
    private JButton grabChaptersButton;
    private JButton autoCheckAvailability;
    private JPanel autoLogPane;
    private JTextArea logArea;
    private JScrollPane autoLogScrollPane;
    private JPanel autoTab;
    private JPanel manTab;
    private JRadioButton chaptersFromLinksRadioButton;
    private JRadioButton chapterToChapterRadioButton;
    private JButton getLinksButton;
    private JTabbedPane tabbedPane1;
    private JButton manRemoveLinksBtn;
    private JScrollPane manLinkScrollPane;
    private JTextArea manLogArea;
    private JScrollPane manLogScrollPane;
    private JButton manSetMetadataButton;
    private JButton updateButton;
    private JTextArea updateTextArea;
    private JScrollPane updateScrollPane;
    private JProgressBar manProgressBar;
    private JButton manGrabChaptersButton;
    private JButton manBlackListedTags;
    private JButton manBrowseLocationButton;
    private JButton checkRemoveEntry;
    private JButton checkAddNewEntryBtn;
    private JTextArea checkerLogArea;
    private JList checkerList;
    private JScrollPane checkerListScrollPane;
    private JScrollPane checkerLogScrollPane;
    private JLabel checkBusyIcon;
    private JButton checkPollStartBtn;
    private JLabel checkDefaultFileLabel;
    private JLabel autoBusyLabel;
    private JButton stopButton;
    private JButton manStopButton;
    private JButton manJsoupInfoButton;
    public JTextField autoLastChapterURL;
    public JTextField autoFirstChapterURL;
    private JLabel autoLastChapterLbl;
    private JLabel autoFirstChapterLbl;
    private JLabel NovelUrlLbl;
    private JButton autoEditMetaBtn;
    private JButton autoEditBlacklistBtn;
    private JLabel manTocURLlbl;
    private JButton checkForUpdatesButton;
    public JLabel pagesCountLbl;
    public JLabel pagesLbl;
    public JCheckBox useHeaderlessBrowserCheckBox;
    public JComboBox settingsBrowserComboBox;
    public JCheckBox displayChapterTitleCheckBox;
    public JCheckBox manDispalyChapterTitleCheckbox;
    public JTextField autoChapterToChapterNumberField;
    public JCheckBox manUseHeaderlessBrowser;
    public JComboBox manBrowserCombobox;
    private JButton manAddChapterButton;
    private JList accountWebsiteList;
    private JTextField accountUsernameField;
    private JTextField accountPasswordField;
    private JButton accountAddBtn;
    private JScrollPane accountWebsiteScrollPane;
    public JCheckBox useAccountCheckBox;
    private JList settingsMenuList;
    private JScrollPane settingsMenuScrollPane;
    private JPanel settingsHeadlessPanel;
    private JButton emailSaveBtn;
    private JComboBox settingsNameOutputFormatComboBox;
    private JPanel settingsGeneralPanel;
    private JCheckBox settingsAlwaysGetImagesCheckBox;
    private JTextField settingsSavelocationField;
    private JButton settingsBrowseSaveLocationBtn;
    private JCheckBox standardSaveLocationCheckBox;
    private JButton autoStarredBtn;
    private JPanel settingsTab;
    private JPanel libraryTab;
    private JPanel libraryPanel;
    private JScrollPane libraryScrollPane;
    private JPanel settingsEmailPanel;
    private JTextField emailHostField;
    private JTextField emailPortField;
    private JTextField emailUserField;
    private JTextField emailPasswordField;
    private JComboBox emailSLLComboBox;
    private JCheckBox sendNewChapterNotificationsCheckBox;
    private JTextField emailReceiver;
    private JCheckBox sendEPUBAsAttachmentCheckBox;
    private JPanel settingsLibraryPanel;
    private JCheckBox enableCheckingCheckBox;
    private JSpinner libraryFrequencySpinner;
    private JCheckBox updateLastChapterNumberCheckBox;
    private JButton settingsSaveBtn;
    private JCheckBox manDetectChapterContainerCheckBox;
    private JButton manDetectChaptersBtn;
    private JTextField firstChapterField;
    private JTextField lastChapterField;
    private JTextField nextChapterButtonField;
    private JTextField manChapterToChapterNumberField;
    private JPanel chapterToChapterPanel;
    private JPanel chapterFromListPanel;
    public JLabel manPageCounter;
    private JLabel manPageLbl;
    private JButton manReverseBtn;
    private JLabel manChapterAmountLbl;
    private JButton settingsTeleStartBtn;
    private JButton settingsTeleStopBtn;
    private JButton libraryStopBtn;
    private JButton libraryStartBtn;
    private JButton settingsGeneralBtn;
    private JButton settingsLibraryBtn;
    private JButton settingsEmailBtn;
    private JButton settingsUpdateBtn;
    private JPanel accountEditPanel;
    private JButton settingsContributeBtn;
    private JButton settingsSourcesBtn;
    private JPanel settingsSourcesPanel;
    private JScrollPane sourcesScrollPane;
    private JList sourcesJList;
    private JComboBox settingsOutputFormatComboBox;
    private JButton openBrowserButton;
    private JButton editCookiesButton;
    private JPanel sourcesLoginPanel;
    private JCheckBox useHeadlessCheckBox;
    private JPanel sourceCanUseHeadlessPanel;
    private JButton saveCookiesButton;
    private JCheckBox manUseAccountCheckBox;
    private JCheckBox settingsSeperateChaptersCheckBox;
    private JButton settingsTelegramBotBtn;
    private JPanel settingsTelegramPanel;
    private JTextField settingsTeleApiTknField;
    private JTextField settingsTeleMaxChapterPerDayField;
    private JTextField settingsTeleMaxChapterPerNovelField;
    private JTextField settingsTeleWaitTimeField;
    private JButton saveButton;
    private JCheckBox settingsTeleMaxChapterPerDayCheckBox;
    private JCheckBox settingsTeleMaxChapterPerNovelCheckBox;
    private JCheckBox settingsTeleWaitTimeCheckBox;
    private JLabel settingsTeleStatusLbl;
    private JButton settingsTeleInfoBtn;
    private JCheckBox libraryOnlyShowNovelsWithCheckBox;
    private JTextField librarySearchField;
    private JButton searchButton;
    private JCheckBox libraryDoNotDisplayCoversCheckBox;
    private JPanel libraryNovelPanel;
    private JComboBox libraryHostListComboBox;
    private JCheckBox settingsNotificationWhenFinishedCheckBox;
    private JSpinner settingsTeleDownloadLimitSpinner;
    private JComboBox settingsGuiThemeComboBox;
    private JCheckBox settingsTeleImagesAllowedCheckBox;
    private JComboBox settingsGuiFontComboBox;
    private JPanel settingsNovelPanel;
    private JButton settingsNovelBtn;
    private JComboBox settingsChapterTitleComboBox;
    private JButton settingsNovelSaveBtn;
    private JTextField settingsNovelCustomChapterTitleField;
    private JButton manEditChapterOrder;
    public JTextArea autoBookDescArea;
    private JScrollPane autoBookDescScrollPane;
    private JButton autoEditMetadataButton;


    public GUI() {
        $$$setupUI$$$();
        initialize();

        // Button logic

        // Automatic grabbing
        // First Auto-Novel initialization, fetching info and chapter list
        autoCheckAvailability.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            if (!chapterListURL.getText().isEmpty()) {
                try {
                    autoBusyLabel.setVisible(true);
                    // Create novel object with settings
                    autoNovel = Novel.builder()
                            .novelLink(chapterListURL.getText())
                            .window("auto")
                            .browser(settings.getBrowser())
                            .useAccount(useAccountCheckBox.isSelected())
                            .setSource(chapterListURL.getText())
                            .build();
                    autoNovel.check();
                } catch (ClassNotFoundException ex) {
                    GrabberUtils.err("auto", ex.getMessage());
                } catch (IOException ex) {
                    GrabberUtils.err("auto", ex.getMessage(), ex);
                } catch (Exception ex) {
                    GrabberUtils.err("auto", "Something went wrong, see log for more information.", ex);
                } finally {
                    updateMetadataDisplay();
                }
            }
        }));

        // Start Auto-Novel chapter download and EPUB creation
        grabChaptersButton.addActionListener(arg0 -> Executors.newSingleThreadExecutor().execute(() -> {
            // input validation
            if (chapterListURL.getText().isEmpty()) {
                showPopup("URL field is empty.", "warning");
                chapterListURL.requestFocusInWindow();
                return;
            }
            if (autoSaveLocation.getText().isEmpty()) {
                showPopup("Save directory field is empty.", "warning");
                autoSaveLocation.requestFocusInWindow();
                return;
            }
            if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && (((Integer) firstChapter.getValue() < 1)
                    || ((Integer) lastChapter.getValue()) < 1)) {
                showPopup("Chapter numbers can't be lower than 1.", "warning");
                return;
            }
            if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && ((Integer) lastChapter.getValue() > autoNovel.chapterList.size())) {
                showPopup("Novel doesn't have that many chapters.", "warning");
                return;
            }
            if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && ((Integer) lastChapter.getValue()) < (Integer) firstChapter.getValue()) {
                showPopup("Last chapter can't be lower than first chapter.", "warning");
                return;
            }
            if ((!chapterAllCheckBox.isSelected()) && (toLastChapter.isSelected())
                    && ((Integer) firstChapter.getValue()) < 1) {
                showPopup("First chapter number can't be lower than 1.", "warning");
                return;
            }
            if (waitTime.getText().isEmpty()) {
                showPopup("Wait time cannot be empty.", "warning");
                return;
            }
            if (!waitTime.getText().matches("\\d+") && !waitTime.getText().isEmpty()) {
                showPopup("Wait time must contain numbers.", "warning");
                return;
            }
            try {
                autoDownloadInProgress(true);
                int tempFirstChapter;
                int tempLastChapter;
                if(chapterAllCheckBox.isSelected()) {
                    tempFirstChapter = 1;
                    tempLastChapter = autoNovel.chapterList.size();
                } else {
                    tempFirstChapter = (int) firstChapter.getValue();
                    if(toLastChapter.isSelected()) {
                        tempLastChapter = autoNovel.chapterList.size();
                    } else {
                        tempLastChapter = (int) lastChapter.getValue();
                    }
                }
                // Set chapter range
                autoNovel = Novel.modifier(autoNovel)
                        .saveLocation(autoSaveLocation.getText())
                        .waitTime(Integer.parseInt(waitTime.getText()))
                        .displayChapterTitle(displayChapterTitleCheckBox.isSelected())
                        .getImages(autoGetImages.isSelected())
                        .browser(settings.getBrowser())
                        .useAccount(useAccountCheckBox.isSelected())
                        .firstChapter(tempFirstChapter)
                        .lastChapter(tempLastChapter)
                        .build();
                autoNovel.downloadChapters(); // Throws exception if grabbing was stopped
                autoNovel.output();
            } catch (InterruptedException e) {
                GrabberUtils.err("auto",e.getMessage(), e);
                autoNovel.killTask = false;
            } finally {
                autoDownloadInProgress(false);
            }
        }));

        browseButton.addActionListener(arg0 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                autoSaveLocation.setText(chooser.getSelectedFile().toString());
            }
        });

        autoEditBlacklistBtn.addActionListener(e -> editBlacklistedTags.main(autoNovel.blacklistedTags));

        autoEditMetaBtn.addActionListener(e -> {
            editMetadata.main(autoNovel.metadata);
            updateMetadataDisplay();
        });

        autoChapterToChapterNumberField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (autoChapterToChapterNumberField.getText().equals("Number")) {
                    autoChapterToChapterNumberField.setText("");
                    autoChapterToChapterNumberField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (autoChapterToChapterNumberField.getText().isEmpty()) {
                    autoChapterToChapterNumberField.setForeground(Color.GRAY);
                    autoChapterToChapterNumberField.setText("Number");
                }
            }
        });

        chapterAllCheckBox.addActionListener(arg0 -> {
            if (chapterAllCheckBox.isSelected()) {
                firstChapter.setEnabled(false);
                lastChapter.setEnabled(false);
                toLastChapter.setEnabled(false);
            } else {
                firstChapter.setEnabled(true);
                lastChapter.setEnabled(true);
                toLastChapter.setEnabled(true);
            }
        });

        toLastChapter.addActionListener(arg0 -> {
            if (toLastChapter.isSelected()) {
                chapterAllCheckBox.setEnabled(false);
                lastChapter.setEnabled(false);
            } else {
                chapterAllCheckBox.setEnabled(true);
                lastChapter.setEnabled(true);
            }
        });

        stopButton.addActionListener(e -> {
            stopButton.setEnabled(false);
            autoNovel.killTask = true;
        });

        autoGetNumberButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> autoChapterOrder.main(autoNovel)));

        // manual chapter download
        manNovel.metadata = new NovelMetadata();
        manNovel.blacklistedTags = new ArrayList<>();

        getLinksButton.addActionListener(e -> {
            if (manNovelURL.getText().isEmpty()) {
                JOptionPane.showMessageDialog(window, "URL field is empty.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                manNovelURL.requestFocusInWindow();
            }
            if (!manNovelURL.getText().isEmpty()) {
                try {
                    manNovel = Novel.modifier(manNovel)
                            .novelLink(manNovelURL.getText())
                            .useAccount(manUseAccountCheckBox.isSelected())
                            .window("manual")
                            .useHeadless(manUseHeaderlessBrowser.isSelected())
                            .browser(settings.getBrowser())
                            .setSource()
                            .build();
                    manNovel.check();
                } catch (ClassNotFoundException ex) {
                    GrabberUtils.err("manual", ex.getMessage());
                } catch (NullPointerException | IOException ex) {
                    GrabberUtils.err("manual", ex.getMessage(), ex);
                } finally {
                    if (!manLinkListModel.isEmpty()) {
                        manRemoveLinksBtn.setEnabled(true);
                        manDetectChaptersBtn.setEnabled(true);
                        manReverseBtn.setEnabled(true);
                        manChapterAmountLbl.setVisible(true);
                        manChapterAmountLbl.setText("Chapters: " + manLinkListModel.size());
                    }
                }
            }
        });

        manGrabChaptersButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            // Chapter-To-Chapter
            // input validation
            if (chapterToChapterRadioButton.isSelected()) {
                if (manSaveLocation.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window, "Save directory field is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manSaveLocation.requestFocusInWindow();
                } else if (manChapterContainer.getText().isEmpty() && !manDetectChapterContainerCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(window, "Chapter container selector is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterContainer.requestFocusInWindow();
                } else if (manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time cannot be empty.", "warning");
                } else if (!manWaitTime.getText().matches("\\d+") && !manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time must contain numbers.", "warning");
                } else if ((!manSaveLocation.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty() || manDetectChapterContainerCheckBox.isSelected())
                        && (!manWaitTime.getText().isEmpty())) {
                    manDownloadInProgress(true);
                    try {
                        manNovel = Novel.modifier(manNovel)
                                .window("manual")
                                .useHeadless(manUseHeaderlessBrowser.isSelected())
                                .browser(settings.getBrowser())
                                .displayChapterTitle(manDispalyChapterTitleCheckbox.isSelected())
                                .waitTime(Integer.parseInt(manWaitTime.getText()))
                                .getImages(manGetImages.isSelected())
                                .autoDetectContainer(manDetectChapterContainerCheckBox.isSelected())
                                .saveLocation(manSaveLocation.getText())
                                .setSource()
                                .build();
                        if (manUseAccountCheckBox.isSelected()) manNovel.getLoginCookies();
                        manNovel.processChaptersToChapters(
                                firstChapterField.getText(),
                                lastChapterField.getText(),
                                nextChapterButtonField.getText(),
                                manChapterToChapterNumberField.getText());
                        manNovel.output();
                    } catch (Exception ex) {
                        GrabberUtils.err("manual", ex.getMessage(), ex);
                        manNovel.killTask = false;
                    } finally {
                        manDownloadInProgress(false);
                    }
                }
                // Download chapters from link list
                // input validation
            } else {
                if(manLinkListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(window, "No chapter links found.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
                else if (manSaveLocation.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window, "Save directory field is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manSaveLocation.requestFocusInWindow();
                } else if (manChapterContainer.getText().isEmpty() && !manDetectChapterContainerCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(window, "Chapter container selector is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterContainer.requestFocusInWindow();
                } else if (manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time cannot be empty.", "warning");
                } else if (!manWaitTime.getText().matches("\\d+") && !manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time must contain numbers.", "warning");
                } else if ((!manSaveLocation.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty() || manDetectChapterContainerCheckBox.isSelected())
                        && (!manWaitTime.getText().isEmpty())
                ) {
                    manDownloadInProgress(true);
                    try {
                        manNovel.chapterList = new ArrayList<>();
                        for (int i = 0; i < manLinkListModel.size(); i++) {
                            manNovel.chapterList.add(manLinkListModel.get(i));
                        }
                        manNovel = Novel.modifier(manNovel)
                                .useHeadless(manUseHeaderlessBrowser.isSelected())
                                .browser(settings.getBrowser())
                                .displayChapterTitle(manDispalyChapterTitleCheckbox.isSelected())
                                .waitTime(Integer.parseInt(manWaitTime.getText()))
                                .getImages(manGetImages.isSelected())
                                .autoDetectContainer(manDetectChapterContainerCheckBox.isSelected())
                                .saveLocation(manSaveLocation.getText())
                                .firstChapter(1)
                                .lastChapter(manNovel.chapterList.size())
                                .setSource()
                                .build();
                        manNovel.downloadChapters();
                        manNovel.output();
                    } catch (InterruptedException err) {
                        GrabberUtils.err("manual", err.getMessage(), err);
                        manNovel.killTask = false;
                    } catch (IOException | ClassNotFoundException err) {
                        GrabberUtils.err("manual", err.getMessage(), err);
                    } finally {
                        manDownloadInProgress(false);
                    }
                }
            }
        }));

        chaptersFromLinksRadioButton.addActionListener(e -> {
            if (chaptersFromLinksRadioButton.isSelected()) {
                chapterToChapterRadioButton.setSelected(false);
                chapterToChapterPanel.setVisible(false);
                chapterFromListPanel.setVisible(true);
            } else {
                chapterFromListPanel.setVisible(false);
            }
        });

        chapterToChapterRadioButton.addActionListener(e -> {
            if (chapterToChapterRadioButton.isSelected()) {
                chaptersFromLinksRadioButton.setSelected(false);
                chapterToChapterPanel.setVisible(true);
                chapterFromListPanel.setVisible(false);
            } else {
                chapterToChapterPanel.setVisible(false);
            }
        });

        manRemoveLinksBtn.addActionListener(arg0 -> {
            if (!manLinkListModel.isEmpty()) {
                int[] indices = manLinkList.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    manLinkListModel.removeElementAt(indices[i]);
                }
                if (manLinkListModel.isEmpty()) {
                    manRemoveLinksBtn.setEnabled(false);
                    manDetectChaptersBtn.setEnabled(false);
                    manReverseBtn.setEnabled(false);
                    manChapterAmountLbl.setVisible(false);
                }
                manChapterAmountLbl.setVisible(true);
                manChapterAmountLbl.setText("Chapters: "+manLinkListModel.size());
                appendText("manual", indices.length + " links removed.");
            }
        });

        manBrowseLocationButton.addActionListener(arg0 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manSaveLocation.setText(chooser.getSelectedFile().toString());
            }
        });

        manSetMetadataButton.addActionListener(arg0 -> {
            editMetadata.main(manNovel.metadata);
        });

        manBlackListedTags.addActionListener(e -> {
            editBlacklistedTags.main(manNovel.blacklistedTags);
        });

        manStopButton.addActionListener(e -> {
            manStopButton.setEnabled(false);
            manNovel.killTask = true;
        });

        manJsoupInfoButton.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI("https://jsoup.org/cookbook/extracting-data/selector-syntax"));
            } catch (URISyntaxException ex) {
                GrabberUtils.err(ex.getMessage(), ex);
            }
        });

        manAddChapterButton.addActionListener(actionEvent -> {
            editChapterList.main("manual");
            if (!manLinkListModel.isEmpty()) {
                manRemoveLinksBtn.setEnabled(true);
                manDetectChaptersBtn.setEnabled(true);
                manReverseBtn.setEnabled(true);
                manChapterAmountLbl.setVisible(true);
                manChapterAmountLbl.setText("Chapters: "+manLinkListModel.size());
            }
        });

        manDetectChaptersBtn.addActionListener(actionEvent -> {
            manLinkListModel.removeAllElements();
            manNovel.chapterList = GrabberUtils.getMostLikelyChapters(manNovel.tableOfContent);
            for(Chapter chapter: manNovel.chapterList) manLinkListModel.addElement(chapter);
            manChapterAmountLbl.setVisible(true);
            manChapterAmountLbl.setText("Chapters: "+manLinkListModel.size());
        });


        settingsBrowseSaveLocationBtn.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                settingsSavelocationField.setText(chooser.getSelectedFile().toString());
            }
        });

        settingsSaveBtn.addActionListener(actionEvent -> {
            settings.setBrowser(settingsBrowserComboBox.getSelectedItem().toString());
            settings.setShowNovelFinishedNotification(settingsNotificationWhenFinishedCheckBox.isSelected());
            settings.setGuiTheme(settingsGuiThemeComboBox.getSelectedIndex());
            settings.setFontName(settingsGuiFontComboBox.getSelectedItem().toString());
            try {
                switch (settingsGuiThemeComboBox.getSelectedIndex()) {
                    case 0:
                        UIManager.setLookAndFeel(new FlatIntelliJLaf());
                        break;
                    case 1:
                        UIManager.setLookAndFeel(new FlatLightLaf());
                        break;
                    case 2:
                        UIManager.setLookAndFeel(new FlatDarkLaf());
                        break;
                    case 3:
                        UIManager.setLookAndFeel(new FlatDarculaLaf());
                        break;
                }
                init.setUIFont(new FontUIResource(settings.getFontName(), Font.PLAIN,13));
                SwingUtilities.updateComponentTreeUI(init.gui);
            } catch (UnsupportedLookAndFeelException ex) {
                showPopup("Error switching themes:" + ex.getMessage(), "error");
                GrabberUtils.err("Error switching themes:" + ex.getMessage(), ex);
                ex.printStackTrace();
            }

            settings.save();
        });

        autoStarredBtn.addActionListener(actionEvent -> {
            if(library.isStarred(autoNovel.novelLink)) {
                library.removeNovel(autoNovel.novelLink);
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/unstarred_icon.png")));
            } else {
                library.addNovel(autoNovel);
                library.writeLibraryFile();
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/starred_icon.png")));
            }
            buildLibHostComboBox();
        });

        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 2) {
                buildLibrary();
            }
        });
        // Email config
        emailSaveBtn.addActionListener(actionEvent -> {
            settings.setHost(emailHostField.getText());
            settings.setReceiverEmail(emailReceiver.getText());
            settings.setPassword(emailPasswordField.getText());
            settings.setUsername(emailUserField.getText());
            settings.setPort(Integer.parseInt(emailPortField.getText()));
            settings.setSsl(emailSLLComboBox.getSelectedItem().toString());
            settings.save();
        });

        // Library config
        enableCheckingCheckBox.addActionListener(actionEvent -> {
            if(!enableCheckingCheckBox.isSelected()) {
                libraryFrequencySpinner.setEnabled(false);
            } else {
                libraryFrequencySpinner.setEnabled(true);
            }
        });

        manReverseBtn.addActionListener(actionEvent -> {
            List<Chapter> tempList = new ArrayList<>();
            for (int i = 0; i < manLinkListModel.size(); i++) {
                tempList.add(manLinkListModel.get(i));
            }
            Collections.reverse(tempList);
            manLinkListModel.removeAllElements();
            for (Chapter chapter: tempList) {
                manLinkListModel.addElement(chapter);
            }
        });
        libraryStopBtn.addActionListener(actionEvent -> {
            libraryStartBtn.setEnabled(true);
            libraryStartBtn.setVisible(true);
            libraryStopBtn.setEnabled(false);
            libraryStopBtn.setVisible(false);
            settings.setFrequency((Integer) libraryFrequencySpinner.getValue());
            settings.setPollingEnabled(false);
            settings.save();
            library.stopPolling();
        });
        libraryStartBtn.addActionListener(actionEvent -> {
            libraryStartBtn.setEnabled(false);
            libraryStartBtn.setVisible(false);
            libraryStopBtn.setEnabled(true);
            libraryStopBtn.setVisible(true);
            settings.setFrequency((Integer) libraryFrequencySpinner.getValue());
            settings.setPollingEnabled(true);
            settings.save();
            library.startPolling();
        });

        settingsGeneralBtn.addActionListener(actionEvent -> {
            settingsEmailPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);
            settingsTelegramPanel.setVisible(false);
            settingsNovelPanel.setVisible(false);

            settingsGeneralPanel.setVisible(true);
        });
        settingsLibraryBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);
            settingsTelegramPanel.setVisible(false);
            settingsNovelPanel.setVisible(false);

            settingsLibraryPanel.setVisible(true);
        });
        settingsEmailBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);
            settingsTelegramPanel.setVisible(false);
            settingsNovelPanel.setVisible(false);

            settingsEmailPanel.setVisible(true);
        });
        settingsTelegramBotBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsNovelPanel.setVisible(false);

            settingsTelegramPanel.setVisible(true);
        });
        settingsSourcesBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsTelegramPanel.setVisible(false);
            settingsNovelPanel.setVisible(false);

            settingsSourcesPanel.setVisible(true);
        });
        settingsNovelBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsTelegramPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);

            settingsNovelPanel.setVisible(true);
        });

        // Sources Panels
        sourcesJList.addListSelectionListener(listSelectionEvent -> {
            int selected = sourcesJList.getSelectedIndex();
            if(selected != -1) {
                sourcesLoginPanel.setVisible(true);
                if(sourcesListModel.get(selected).canHeadless()) {
                    sourceCanUseHeadlessPanel.setVisible(true);
                    String domain = sourcesListModel.get(selected).getName();
                    if(settings.getHeadlessList().contains(domain)) {
                        useHeadlessCheckBox.setSelected(true);
                    } else {
                        useHeadlessCheckBox.setSelected(false);
                    }
                } else {
                    sourceCanUseHeadlessPanel.setVisible(false);
                }
            } else {
                sourcesLoginPanel.setVisible(false);
            }
        });

        // Contribute button logic
        settingsContributeBtn.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI("https://www.paypal.com/paypalme/flameish"));
            } catch (URISyntaxException ex) {
                GrabberUtils.err(ex.getMessage(), ex);
            }
        });

        openBrowserButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            int selected = sourcesJList.getSelectedIndex();
            if(selected != -1) {
                if (guiDriver == null) guiDriver = new Driver("");
                guiDriver.driver.navigate().to(sourcesListModel.get(selected).getUrl());
                openBrowserButton.setEnabled(false);
                saveCookiesButton.setEnabled(true);
                // Keeps browser open until manually closed
                while (true) {
                    try {
                        guiDriver.driver.getTitle();
                        GrabberUtils.sleep(1000);
                    } catch (Exception ex) {
                        saveCookiesButton.setEnabled(false);
                        openBrowserButton.setEnabled(true);
                        guiDriver = null;
                        break;
                    }
                }
            }
        }));

        saveCookiesButton.addActionListener(e -> {
            int selected = sourcesJList.getSelectedIndex();
            if(selected != -1) {
                if (guiDriver != null) {
                    String sourceName = sourcesListModel.get(selected).getName();
                    Set<Cookie> cookies = guiDriver.driver.manage().getCookies();
                    Map<String, String> loginCookies = new HashMap();
                    for (Cookie cookie : cookies) {
                        loginCookies.put(cookie.getName(), cookie.getValue());
                    }
                    Accounts.getInstance().addAccount(sourceName, loginCookies);
                    guiDriver.close();
                    saveCookiesButton.setEnabled(false);
                    openBrowserButton.setEnabled(true);
                }
            }
        });

        editCookiesButton.addActionListener(e -> {
            int selected = sourcesJList.getSelectedIndex();
            if(selected != -1) {
                String domain = sourcesListModel.get(selected).getName();
                editCookies.main(domain);
            }

        });

        useHeadlessCheckBox.addActionListener(e -> {
            int selected = sourcesJList.getSelectedIndex();
            if(selected != -1) {
                String domain = sourcesListModel.get(selected).getName();
                if(useHeadlessCheckBox.isSelected()) {
                    settings.getHeadlessList().add(domain);
                    settings.save();
                } else {
                    settings.getHeadlessList().remove(domain);
                    settings.save();
                }
            }
        });

        settingsOutputFormatComboBox.addActionListener(e -> {
            // Show separate chapters checkbox if TXT selected
            if (settingsOutputFormatComboBox.getSelectedIndex() == 1) {
                settingsSeperateChaptersCheckBox.setVisible(true);
            } else {
                settingsSeperateChaptersCheckBox.setVisible(false);
            }
        });
        standardSaveLocationCheckBox.addActionListener(e -> {
            if (standardSaveLocationCheckBox.isSelected()) {
                settingsSavelocationField.setVisible(true);
                settingsBrowseSaveLocationBtn.setVisible(true);
            } else {
                settingsSavelocationField.setVisible(false);
                settingsBrowseSaveLocationBtn.setVisible(false);
            }
        });
        saveButton.addActionListener(e -> {
            if (settingsTeleApiTknField.getText().trim().isEmpty()) {
                showPopup("Telegram API token empty!", "warning");
            }
            else if (!settingsTeleMaxChapterPerDayField.getText().matches("\\d+")
                    && !settingsTeleMaxChapterPerDayField.getText().equals("-1")) {
                showPopup("Max. chapter per day must contain numbers.", "warning");
            }
            else if (!settingsTeleMaxChapterPerNovelField.getText().matches("\\d+")
                    && !settingsTeleMaxChapterPerNovelField.getText().equals("-1")) {
                showPopup("Max. chapter per novel must contain numbers.", "warning");
            }
            else if (!settingsTeleWaitTimeField.getText().matches("\\d+")) {
                showPopup("Wait time must contain numbers.", "warning");
            }
            else if ((Integer) settingsTeleDownloadLimitSpinner.getValue() < 1) {
                showPopup("Concurrent download limit can't be lower than 1", "warning");
            }  else {
                settings.setTelegramApiToken(settingsTeleApiTknField.getText().trim());
                settings.setTelegramMaxChapterPerDay(Integer.parseInt(settingsTeleMaxChapterPerDayField.getText()));
                settings.setTelegramNovelMaxChapter(Integer.parseInt(settingsTeleMaxChapterPerNovelField.getText()));
                settings.setTelegramWait(Integer.parseInt(settingsTeleWaitTimeField.getText()));
                settings.setTelegramDownloadLimit((Integer) settingsTeleDownloadLimitSpinner.getValue());
                settings.setTelegramImagesAllowed(settingsTeleImagesAllowedCheckBox.isSelected());
                settings.save();
            }
        });
        settingsTeleStartBtn.addActionListener(e -> {
            settingsTeleStartBtn.setEnabled(false);
            try {
                init.telegramBot = new Bot();
                init.telegramBot.start();
                settingsTeleStatusLbl.setText("Status: Running");
                settingsTeleStopBtn.setVisible(true);
                settingsTeleStartBtn.setVisible(false);
            } catch (InterruptedException ex) {
                showPopup(ex.getMessage(), "error");
                settingsTeleStartBtn.setEnabled(true);
            }
        });
        settingsTeleStopBtn.addActionListener(e -> {
            if (init.telegramBot != null) {
                init.telegramBot.stop();
                settingsTeleStatusLbl.setText("Status: Stopped");
                settingsTeleStartBtn.setVisible(true);
                settingsTeleStartBtn.setEnabled(true);
                settingsTeleStopBtn.setVisible(false);
            }
        });
        settingsTeleInfoBtn.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI("https://core.telegram.org/bots#3-how-do-i-create-a-bot"));
            } catch (URISyntaxException ex) {
                GrabberUtils.err(ex.getMessage(), ex);
            }
        });
        libraryOnlyShowNovelsWithCheckBox.addActionListener(e -> {
            settings.setLibraryShowOnlyUpdatable(libraryOnlyShowNovelsWithCheckBox.isSelected());
            settings.save();
            buildLibrary();
        });
        searchButton.addActionListener(e -> {
            buildLibrary();
        });
        librarySearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    buildLibrary();
                }
            }
        });
        libraryDoNotDisplayCoversCheckBox.addActionListener(e -> {
            settings.setLibraryNoCovers(libraryDoNotDisplayCoversCheckBox.isSelected());
            settings.save();
        });

        settingsChapterTitleComboBox.addActionListener(e -> {
            if (settingsChapterTitleComboBox.getSelectedIndex() == chapterTitleFormatOptions.length-1) {
                settingsNovelCustomChapterTitleField.setVisible(true);
            } else {
                settingsNovelCustomChapterTitleField.setVisible(false);
            }
        });

        settingsNovelSaveBtn.addActionListener(actionEvent -> {
            settings.setSaveLocation(settingsSavelocationField.getText());
            settings.setUseStandardLocation(standardSaveLocationCheckBox.isSelected());
            settings.setAutoGetImages(settingsAlwaysGetImagesCheckBox.isSelected());
            settings.setFilenameFormat(settingsNameOutputFormatComboBox.getSelectedIndex());
            settings.setOutputFormat(settingsOutputFormatComboBox.getSelectedIndex());
            settings.setSeparateChapters(settingsSeperateChaptersCheckBox.isSelected());
            settings.setChapterTitleFormat(settingsChapterTitleComboBox.getSelectedIndex());
            if (settingsChapterTitleComboBox.getSelectedIndex() == chapterTitleFormatOptions.length-1
                    && !settingsNovelCustomChapterTitleField.getText().contains("%s")) {
                showPopup("Custom chapter title template does not contain %s to insert chapter name", "warning");
                settingsNovelCustomChapterTitleField.setText("%s");
                settings.setChapterTitleTemplate("%s");
            } else {
                settings.setChapterTitleTemplate(settingsNovelCustomChapterTitleField.getText());
            }

            settings.save();
        });
    }

    /**
     * Checks various filter settings for a novel
     *
     * @return {@code true} if novel gets caught
     */
    public boolean checkLibFilter(LibraryNovel libNovel) {
        // Check for new releases if "Only new releases" is selected
        int chapterDiff = libNovel.getNewestChapterNumber() - libNovel.getLastLocalChapterNumber();
        if (chapterDiff == 0 && settings.isLibraryShowOnlyUpdatable()) {
            return true;
        }
        // Check if novel title contains search word
        if (!librarySearchField.getText().isEmpty() &&
                !libNovel.getMetadata().getTitle().toLowerCase().contains(librarySearchField.getText().toLowerCase())) {
            return true;
        }
        // Check if host domain of novel is valid for selected item from dropdown
        if (!libraryHostListComboBox.getSelectedItem().toString().equals("All")) {
            try {
                // Get pretty source name from source class
                Source source = GrabberUtils.getSource(GrabberUtils.getDomainName(libNovel.getNovelUrl()));
                if (source != null) {
                    // Check hostname is already in combobox
                    String hostname = source.getName();
                    if (!hostname.equals(libraryHostListComboBox.getSelectedItem().toString())) {
                        return true;
                    }
                }
            } catch (Exception e) {
                GrabberUtils.err(e.getMessage(), e);
            }
        }
        return false;
    }

    public void buildLibrary() {
        libraryPanel.removeAll();
        int gridRow = 0;
        int gridCol = 0;
        for(LibraryNovel libNovel: library.getNovels()) {
            if (checkLibFilter(libNovel)) continue;

            GridBagConstraints c;
            JPanel novelPane = new JPanel(new GridBagLayout());

            // Cover
            c = new GridBagConstraints();
            String novelCover = libNovel.getSaveLocation() + "/cover." + libNovel.getMetadata().getCoverFormat();
            JButton novelImage;
            if (!settings.isLibraryNoCovers()) {
                if(novelCover.isEmpty()) {
                    novelImage = new JButton(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
                } else {
                    // Cache ?
                    ImageIcon imageIcon = new ImageIcon(novelCover); // load the image to a imageIcon
                    Image image = imageIcon.getImage(); // transform it
                    Image newimg = image.getScaledInstance(120, 180,  Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(newimg);  // transform it back
                    novelImage = new JButton(imageIcon);
                }
                novelImage.setBorder(BorderFactory.createEmptyBorder());
                novelImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                // Show novel settings
                novelImage.addActionListener(actionEvent -> {
                    libraryNovelSettings.main(libNovel);
                });
                c.gridx = 0;
                c.gridy = 0;
                c.fill = GridBagConstraints.BOTH;
                c.gridheight = GridBagConstraints.REMAINDER;
                novelPane.add(novelImage, c);
            }

            // Name
            c = new GridBagConstraints();
            JLabel novelTitle = new JLabel("<html><p style=\"width:275px\">" + libNovel.getMetadata().getTitle() + "</p></html>");
            if (!libNovel.isCheckingActive()) {
                novelTitle.setForeground(Color.GRAY);
            }
            novelTitle.setFont(new Font("DejaVuSans", Font.BOLD, 17));
            novelTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    libraryNovelSettings.main(libNovel);
                }
            };
            novelTitle.addMouseListener(mouseListener);
            c.gridx = 1;
            c.gridy = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.insets = new Insets(0,12,0,0);
            novelPane.add(novelTitle, c);

            // Last chapter
            c = new GridBagConstraints();
            JLabel lastChapter = new JLabel("<html>Last chapter: <br>" +
                    "<p style=\"width:275px\">" + libNovel.getLastChapterName() + "</p></html>");
            c.gridx = 1;
            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.insets = new Insets(5,12,0,0);
            novelPane.add(lastChapter, c);

            // Newest chapter
            c = new GridBagConstraints();
            JLabel newestChapter = new JLabel("<html>Newest chapter: <br>" +
                    "<p style=\"width:275px\">" + libNovel.getNewestChapterName() + "</p></html>");
            c.gridx = 1;
            c.gridy = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.insets = new Insets(5,12,0,0);
            novelPane.add(newestChapter, c);

            c = new GridBagConstraints();
            // FlowLayout to remove border padding inside panel
            JPanel downloadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            c.gridx = 1;
            c.gridy = 3;
            c.anchor = GridBagConstraints.SOUTHWEST;
            c.insets = new Insets(0,10,0,0);
            novelPane.add(downloadPanel, c);

            // Set default text
            JLabel newChapterAmountLbl = new JLabel("No new chapters");
            int chapterDiff = libNovel.getNewestChapterNumber() - libNovel.getLastLocalChapterNumber();
            // Show manual download button if new chapters available
            if(chapterDiff > 0) {
                // Overwrite default text with new chapter amount
                newChapterAmountLbl.setText(chapterDiff + " new chapters");

                // Download button
                c = new GridBagConstraints();
                JButton getLatestChapterBtn = new JButton(new ImageIcon(getClass().getResource("/images/download_icon.png")));
                getLatestChapterBtn.setBorder(BorderFactory.createEmptyBorder());
                getLatestChapterBtn.setContentAreaFilled(false);
                getLatestChapterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                getLatestChapterBtn.setToolTipText("Download chapters from last downloaded to latest");
                getLatestChapterBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                        newChapterAmountLbl.setVisible(false);
                        getLatestChapterBtn.setIcon(new ImageIcon(getClass().getResource("/images/busy.gif")));

                        Novel novel = Novel.builder()
                                .novelLink(libNovel.getNovelUrl())
                                .saveLocation(libNovel.getSaveLocation())
                                .setSource(libNovel.getNovelUrl())
                                .firstChapter(libNovel.getLastLocalChapterNumber() + 1)
                                .lastChapter(libNovel.getNewestChapterNumber())
                                .useAccount(libNovel.isUseAccount())
                                .getImages(libNovel.isGetImages())
                                .displayChapterTitle(libNovel.isDisplayChapterTitle())
                                .waitTime(libNovel.getWaitTime())
                                .window("checker")
                                .build();
                        novel.check();
                        novel.downloadChapters();
                        novel.output();

                        if(libNovel.isUpdateLast()) {
                            libNovel.setLastChapterNumber(libNovel.getNewestChapterNumber());
                            libNovel.setLastChapterName(libNovel.getNewestChapterName());
                            lastChapter.setText("<html>Last chapter: <br>" +
                                    "<p style=\"width:275px\">" + libNovel.getLastChapterName() + "</p></html>");
                            library.writeLibraryFile();
                            // Show no new chapters available again
                            newChapterAmountLbl.setText("No new chapters");
                            getLatestChapterBtn.setVisible(false);
                        } else {
                            getLatestChapterBtn.setIcon(new ImageIcon(getClass().getResource("/images/download_icon.png")));
                            getLatestChapterBtn.setVisible(true);
                        }
                    } catch (ClassNotFoundException | IOException | InterruptedException ex) {
                        showPopup(ex.getMessage(), "error");
                        GrabberUtils.err(ex.getMessage(), ex);
                    } finally {
                        newChapterAmountLbl.setVisible(true);
                    }
                }));
                c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.anchor = GridBagConstraints.SOUTHWEST;
                downloadPanel.add(getLatestChapterBtn, c);
            }
            // Add chapter amount label to panel
            c.gridx = 1;
            c.gridy = 0;
            c.anchor = GridBagConstraints.SOUTH;
            downloadPanel.add(newChapterAmountLbl, c);

            // Add novel panel to library panel
            c = new GridBagConstraints();
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.gridx = gridCol++ % 2 == 0 ? 0 : 1;
            c.gridy = gridRow++ % 2 == 0 ? gridRow : gridRow-1;
            c.weightx = 0.5;
            c.weighty = 0.5;
            c.insets = new Insets( 10, 5, 0, 0);
            novelPane.setPreferredSize(new Dimension(500, 180));
            libraryPanel.add(novelPane, c);
        }

        libraryTab.validate();
        libraryTab.repaint();
    }

    public void buildLibHostComboBox() {
        // Get list of all hostnames of library novels
        Set<String> hostList = new LinkedHashSet<>();
        for (LibraryNovel libNovel: library.getNovels()) {
            hostList.add(GrabberUtils.getDomainName(libNovel.getNovelUrl()));
        }
        // Add entries to ComboBox
        libraryHostListComboBox.removeAllItems();
        libraryHostListComboBox.addItem("All");
        for (String hostName: hostList) {
            try {
                // Get pretty source name from source class
                Source source = GrabberUtils.getSource(hostName);
                if (source != null) {
                    // Check hostname is already in combobox
                    String hostname = source.getName();
                    if(((DefaultComboBoxModel)libraryHostListComboBox.getModel()).getIndexOf(hostname) == -1) {
                        libraryHostListComboBox.addItem(hostname);
                    }
                }
            } catch (Exception e) {
                GrabberUtils.err(e.getMessage(), e);
            }
        }
    }

    public void libraryIsChecking(boolean isChecking) {
        if (isChecking) {
            tabbedPane.setIconAt(2, new ImageIcon(getClass().getResource("/images/busy.gif")));
        } else {
            tabbedPane.setIconAt(2, null);
        }
    }

    private void initialize() {
        this.add(rootPanel);
        this.setTitle("Novel-Grabber " + init.versionNumber);
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        this.setIconImage(favicon.getImage());
        this.setMinimumSize(new Dimension(1000, 700));
        //Tray();
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //if (!SystemTray.isSupported()) {
        //    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //}
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }
/*
    private void Tray() {
        if (!SystemTray.isSupported()) {
            showPopup("SystemTray is not supported. Exiting...", "Error");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));

        ActionListener exitListener = e -> System.exit(0);
        ActionListener openWindow = e -> setVisible(true);

        PopupMenu popup = new PopupMenu();
        MenuItem topLable = new MenuItem("Novel-Grabber");
        popup.add(topLable);
        MenuItem aboutLabel = new MenuItem("About");
        aboutLabel.addActionListener(arg01 -> {
            InputStream in = getClass().getResourceAsStream("/about.txt");
            String text;
            try (Scanner s = new Scanner(in)) {
                text = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            }
            JTextArea textArea = new JTextArea();
            textArea.setText(text);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBounds(0, 0, 1000, 1000);
            JOptionPane.showOptionDialog(null, scrollPane, "About Novel-Grabber", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, null, null);

        });
        popup.add(aboutLabel);
        popup.addSeparator();
        MenuItem defaultItem2 = new MenuItem("Open");
        defaultItem2.addActionListener(openWindow);
        popup.add(defaultItem2);
        MenuItem defaultItem = new MenuItem("Exit");
        defaultItem.addActionListener(exitListener);
        popup.add(defaultItem);

        trayIcon = new TrayIcon(favicon.getImage(), "Novel-Grabber", popup);
        trayIcon.setToolTip("Novel-Grabber");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
    }


 */

    public void showFailedChapters(Novel novel) {
        failedChaptersWindow.main(novel);
    }
    public void appendText(String logWindow, String logMsg) {
        switch (logWindow) {
            case "auto":
                logArea.append(logMsg + NL);
                logArea.setCaretPosition(logArea.getText().length());
                break;
            case "manual":
                manLogArea.append(logMsg + NL);
                manLogArea.setCaretPosition(manLogArea.getText().length());
                break;
            case "update":
                updateTextArea.append(" - " + logMsg + NL);
                updateTextArea.setCaretPosition(updateTextArea.getText().length());
                break;
        }
    }

    public void setMaxProgress(String window, int progressAmount) {
        switch (window) {
            case "auto":
                progressBar.setMaximum(progressAmount);
                progressBar.setString("0 / " + progressAmount);
                progressBar.update(progressBar.getGraphics());
                break;
            case "manual":
                manProgressBar.setMaximum(progressAmount);
                manProgressBar.setString("0 / " + progressAmount);
                manProgressBar.update(manProgressBar.getGraphics());
                break;
        }
    }

    public void manDownloadInProgress(boolean running) {
        if(running) {
            manProgressBar.setValue(0);
            manProgressBar.setStringPainted(true);
            manPageCounter.setText("");
            manPageCounter.setVisible(true);
            manPageLbl.setVisible(true);
            manGrabChaptersButton.setEnabled(false);
            manGrabChaptersButton.setVisible(false);
            manStopButton.setEnabled(true);
            manStopButton.setVisible(true);
        } else {
            manGrabChaptersButton.setEnabled(true);
            manGrabChaptersButton.setVisible(true);
            manStopButton.setEnabled(false);
            manStopButton.setVisible(false);
            manProgressBar.setValue(0);
            manProgressBar.setStringPainted(false);
        }
    }
    public void autoDownloadInProgress(boolean running) {
        if(running) {
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            grabChaptersButton.setEnabled(false);
            grabChaptersButton.setVisible(false);
            pagesCountLbl.setText("");
            pagesCountLbl.setVisible(true);
            pagesLbl.setVisible(true);
            stopButton.setEnabled(true);
            stopButton.setVisible(true);
        } else {
            grabChaptersButton.setEnabled(true);
            grabChaptersButton.setVisible(true);
            stopButton.setEnabled(false);
            stopButton.setVisible(false);
            progressBar.setValue(0);
            progressBar.setStringPainted(false);
        }
    }


    public void updateMetadataDisplay() {
        autoBusyLabel.setVisible(false);
        NovelMetadata metadata = null;
        if (autoNovel != null) metadata = autoNovel.metadata;
        if(metadata != null) {
            grabChaptersButton.setEnabled(true);
            autoGetNumberButton.setEnabled(true);
            autoEditMetaBtn.setEnabled(true);
            autoEditBlacklistBtn.setEnabled(true);
            pagesCountLbl.setText("");
            pagesCountLbl.setVisible(false);
            pagesLbl.setVisible(false);
            if(library.isStarred(autoNovel.novelLink)) {
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/starred_icon.png")));
                autoStarredBtn.setEnabled(true);
                autoStarredBtn.setToolTipText("Remove novel from library");
            } else {
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/unstarred_icon.png")));
                autoStarredBtn.setEnabled(true);
                autoStarredBtn.setToolTipText("Add novel to library");
            }
            autoBookTitle.setText("<html><p style=\"width:200px\">"+metadata.getTitle()+"</p></html>");
            autoAuthor.setText(metadata.getAuthor());
            setBufferedCover(metadata.getBufferedCover());
            autoChapterAmount.setText(String.valueOf(autoNovel.chapterList.size()));
        } else {
            grabChaptersButton.setEnabled(false);
            autoGetNumberButton.setEnabled(false);
            autoEditMetaBtn.setEnabled(false);
            autoEditBlacklistBtn.setEnabled(false);
            pagesCountLbl.setText("");
            pagesCountLbl.setVisible(false);
            pagesLbl.setVisible(false);
            autoStarredBtn.setEnabled(false);
            autoBookTitle.setText("");
            autoAuthor.setText("");
            setBufferedCover(null);
            autoChapterAmount.setText("");
        }
    }

    public void updateProgress(String window) {
        switch (window) {
            case "auto":
                progressBar.setValue(progressBar.getValue() + 1);
                if (progressBar.getValue() <= progressBar.getMaximum()) {
                    progressBar.setString((progressBar.getValue()) + " / " + progressBar.getMaximum());
                }
                break;
            case "manual":
                manProgressBar.setValue(manProgressBar.getValue() + 1);
                if (manProgressBar.getValue() <= manProgressBar.getMaximum()) {
                    manProgressBar.setString((manProgressBar.getValue()) + " / " + manProgressBar.getMaximum());
                }
                break;
        }
    }


    /**
     * Updates word counter of novel and page counter on GUI.
     * (300 words per page)
     */
    public void updatePageCount(String window, int wordCount) {
        if(init.gui != null) {
            if(window.equals("auto")) {
                init.gui.pagesCountLbl.setText(String.valueOf(wordCount / 300));
            }
            if(window.equals("manual")) {
                init.gui.manPageCounter.setText(String.valueOf(wordCount / 300));
            }
        }
    }

    public void showPopup(String errorMsg, String kind) {
        switch (kind) {
            case "warning":
                JOptionPane.showMessageDialog(window, errorMsg, "Warning", JOptionPane.WARNING_MESSAGE);
                break;
            case "error":
                JOptionPane.showMessageDialog(window, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public void setBufferedCover(BufferedImage bufferedImage) {
        if (bufferedImage == null)
            coverImage.setIcon(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
        else
            coverImage.setIcon(new ImageIcon(new ImageIcon(bufferedImage).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
    }

    private void createUIComponents() {
        // Automatic Tab
        firstChapter = new JSpinner();
        firstChapter.setValue(1);

        lastChapter = new JSpinner();
        lastChapter.setValue(1);

        autoGetImages = new JCheckBox();
        autoGetImages.setSelected(settings.isAutoGetImages());

        autoSaveLocation = new JTextField();

        autoChapterToChapterNumberField = new JTextField("Number");
        autoChapterToChapterNumberField.setForeground(Color.GRAY);

        autoShowBlacklistedTagsBtn = new JButton(new ImageIcon(getClass().getResource("/images/block.png")));
        autoShowBlacklistedTagsBtn.setBorder(BorderFactory.createEmptyBorder());
        autoShowBlacklistedTagsBtn.setContentAreaFilled(false);
        autoShowBlacklistedTagsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        autoStarredBtn = new JButton(new ImageIcon(getClass().getResource("/images/unstarred_icon.png")));
        autoStarredBtn.setBorder(BorderFactory.createEmptyBorder());
        autoStarredBtn.setContentAreaFilled(false);
        autoStarredBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        autoCheckAvailability = new JButton(new ImageIcon(getClass().getResource("/images/check_icon.png")));
        autoCheckAvailability.setBorder(BorderFactory.createEmptyBorder());
        autoCheckAvailability.setContentAreaFilled(false);
        autoCheckAvailability.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        chapterListURL = new JTextField();
        // Listen for changes in the novel link field and disable the grabbing button
        chapterListURL.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            public void removeUpdate(DocumentEvent e) {
                change();
            }

            public void insertUpdate(DocumentEvent e) {
                change();
            }

            void change() {
                grabChaptersButton.setEnabled(false);
            }
        });

        autoVisitButton = new JButton(new ImageIcon(getClass().getResource("/images/website_icon.png")));
        autoVisitButton.setBorder(BorderFactory.createEmptyBorder());
        autoVisitButton.setContentAreaFilled(false);
        autoVisitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        autoBusyLabel = new JLabel(new ImageIcon(getClass().getResource("/images/busy.gif")));

        browseButton = new JButton(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        browseButton.setBorder(BorderFactory.createEmptyBorder());
        browseButton.setContentAreaFilled(false);
        browseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        coverImage = new JLabel(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
        coverImage.setBorder(BorderFactory.createEmptyBorder());

        autoEditMetadataButton = new JButton(new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        autoEditMetadataButton.setBorder(BorderFactory.createEmptyBorder());
        autoEditMetadataButton.setContentAreaFilled(false);
        autoEditMetadataButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        autoEditMetaBtn = new JButton(new ImageIcon(getClass().getResource("/images/edit.png")));
        autoEditMetaBtn.setBorder(BorderFactory.createEmptyBorder());
        autoEditMetaBtn.setContentAreaFilled(false);
        autoEditMetaBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        autoEditBlacklistBtn = new JButton(new ImageIcon(getClass().getResource("/images/block.png")));
        autoEditBlacklistBtn.setBorder(BorderFactory.createEmptyBorder());
        autoEditBlacklistBtn.setContentAreaFilled(false);
        autoEditBlacklistBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        autoGetNumberButton = new JButton(new ImageIcon(getClass().getResource("/images/list_icon.png")));
        autoGetNumberButton.setBorder(BorderFactory.createEmptyBorder());
        autoGetNumberButton.setContentAreaFilled(false);
        autoGetNumberButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        waitTime = new JTextField("0");
        waitTime.setHorizontalAlignment(SwingConstants.CENTER);

        logArea = new JTextArea();
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        autoLogScrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        autoBookDescArea = new JTextArea();
        autoBookDescArea.setLineWrap(true);
        autoBookDescArea.setWrapStyleWord(true);
        autoBookDescScrollPane = new JScrollPane(autoBookDescArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Manual Tab
        manSetMetadataButton = new JButton(new ImageIcon(getClass().getResource("/images/edit.png")));
        manSetMetadataButton.setBorder(BorderFactory.createEmptyBorder());
        manSetMetadataButton.setContentAreaFilled(false);
        manSetMetadataButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manBlackListedTags = new JButton(new ImageIcon(getClass().getResource("/images/block.png")));
        manBlackListedTags.setBorder(BorderFactory.createEmptyBorder());
        manBlackListedTags.setContentAreaFilled(false);
        manBlackListedTags.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manReverseBtn = new JButton(new ImageIcon(getClass().getResource("/images/reverse_icon.png")));
        manReverseBtn.setBorder(BorderFactory.createEmptyBorder());
        manReverseBtn.setContentAreaFilled(false);
        manReverseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manAddChapterButton = new JButton(new ImageIcon(getClass().getResource("/images/add_icon.png")));
        manAddChapterButton.setBorder(BorderFactory.createEmptyBorder());
        manAddChapterButton.setContentAreaFilled(false);
        manAddChapterButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manRemoveLinksBtn = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        manRemoveLinksBtn.setBorder(BorderFactory.createEmptyBorder());
        manRemoveLinksBtn.setContentAreaFilled(false);
        manRemoveLinksBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manDetectChaptersBtn = new JButton(new ImageIcon(getClass().getResource("/images/smart_icon.png")));
        manDetectChaptersBtn.setBorder(BorderFactory.createEmptyBorder());
        manDetectChaptersBtn.setContentAreaFilled(false);
        manDetectChaptersBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manLinkList = new JList<>(manLinkListModel);
        manLinkList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        manLinkList.setDropMode(DropMode.INSERT);
        manLinkList.setDragEnabled(true);
        manLinkList.setTransferHandler(new ListItemTransferHandler());
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Chapter selectedChapter = manLinkList.getSelectedValue();
                    manLinkScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    selectedChapter.saveChapter(manNovel);
                    manLinkScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    chapterPreview.main(selectedChapter);
                }
            }
        };
        manLinkList.addMouseListener(mouseListener);
        manLinkScrollPane = new JScrollPane(manLinkList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        manLogArea = new JTextArea();
        manLogArea.setLineWrap(true);
        manLogArea.setWrapStyleWord(true);
        manLogScrollPane = new JScrollPane(manLogArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        manJsoupInfoButton = new JButton(new ImageIcon(getClass().getResource("/images/info_icon.png")));
        manJsoupInfoButton.setBorder(BorderFactory.createEmptyBorder());
        manJsoupInfoButton.setContentAreaFilled(false);
        manJsoupInfoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manSaveLocation = new JTextField();

        manBrowseLocationButton = new JButton(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        manBrowseLocationButton.setBorder(BorderFactory.createEmptyBorder());
        manBrowseLocationButton.setContentAreaFilled(false);
        manBrowseLocationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manWaitTime = new JTextField("0");
        manWaitTime.setHorizontalAlignment(SwingConstants.CENTER);

        // Library Tab
        libraryScrollPane = new JScrollPane(libraryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        libraryScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        libraryOnlyShowNovelsWithCheckBox = new JCheckBox();
        libraryOnlyShowNovelsWithCheckBox.setSelected(settings.isLibraryShowOnlyUpdatable());

        // Settings Tab

        // General settings
        settingsGeneralBtn = new JButton("General", new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        settingsGeneralBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsGeneralBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsGeneralBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsGeneralBtn.setContentAreaFilled(false);
        settingsGeneralBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsNovelBtn = new JButton("Novel", new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        settingsNovelBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsNovelBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsNovelBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsNovelBtn.setContentAreaFilled(false);
        settingsNovelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsEmailBtn = new JButton("Email", new ImageIcon(getClass().getResource("/images/email_icon.png")));
        settingsEmailBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsEmailBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsEmailBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsEmailBtn.setContentAreaFilled(false);
        settingsEmailBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsTelegramBotBtn = new JButton("Telegram Bot", new ImageIcon(getClass().getResource("/images/bot_icon.png")));
        settingsTelegramBotBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsTelegramBotBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsTelegramBotBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsTelegramBotBtn.setContentAreaFilled(false);
        settingsTelegramBotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsLibraryBtn = new JButton("Library", new ImageIcon(getClass().getResource("/images/library_icon.png")));
        settingsLibraryBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsLibraryBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsLibraryBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsLibraryBtn.setContentAreaFilled(false);
        settingsLibraryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsSourcesBtn = new JButton("Sources", new ImageIcon(getClass().getResource("/images/webpage_icon.png")));
        settingsSourcesBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsSourcesBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsSourcesBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsSourcesBtn.setContentAreaFilled(false);
        settingsSourcesBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsContributeBtn = new JButton("Contribute", new ImageIcon(getClass().getResource("/images/heart_icon.png")));
        settingsContributeBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsContributeBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsContributeBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsContributeBtn.setContentAreaFilled(false);
        settingsContributeBtn.setToolTipText("Buy the dev a coffee");
        settingsContributeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsBrowserComboBox = new JComboBox(Driver.browserList);
        if(settings.getBrowser().isEmpty()) {
            String browserSelection = (String)JOptionPane.showInputDialog(this,
                    "A browser is required for logins and browser based grabbing in some cases. \n\n" +
                            "Alternatively you can select \"Headless\" to use an in-build one, it is unable to perform logins through. \n\n" +
                            "You can change your selection in the settings at any point.\n\n",
                    "Pick your browser",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    Driver.browserList,
                    "Chrome");
            settings.setBrowser(browserSelection);
            settings.save();
        }
        settingsBrowserComboBox.setSelectedItem(settings.getBrowser());

        settingsNotificationWhenFinishedCheckBox = new JCheckBox();
        settingsNotificationWhenFinishedCheckBox.setSelected(settings.isShowNovelFinishedNotification());

        settingsGuiThemeComboBox = new JComboBox(guiThemes);
        settingsGuiThemeComboBox.setSelectedIndex(settings.getGuiTheme());

        String[] installedFontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        settingsGuiFontComboBox = new JComboBox(installedFontFamilies);
        int selectedFontIndex = Arrays.asList(installedFontFamilies).indexOf(settings.getFontName());
        settingsGuiFontComboBox.setSelectedIndex(selectedFontIndex);

        // Novel settings
        settingsAlwaysGetImagesCheckBox = new JCheckBox();
        settingsAlwaysGetImagesCheckBox.setSelected(settings.isAutoGetImages());

        settingsSavelocationField = new JTextField();
        settingsSavelocationField.setVisible(false);
        settingsSavelocationField.setText(settings.getSaveLocation());

        settingsBrowseSaveLocationBtn = new JButton(new ImageIcon(getClass().getResource("/images/edit.png")));
        settingsBrowseSaveLocationBtn.setVisible(false);
        settingsBrowseSaveLocationBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsBrowseSaveLocationBtn.setContentAreaFilled(false);
        settingsBrowseSaveLocationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        standardSaveLocationCheckBox = new JCheckBox();
        standardSaveLocationCheckBox.setSelected(settings.isUseStandardLocation());

        if(settings.isUseStandardLocation()) {
            settingsSavelocationField.setVisible(true);
            settingsBrowseSaveLocationBtn.setVisible(true);
            autoSaveLocation.setText(settings.getSaveLocation());
            manSaveLocation.setText(settings.getSaveLocation());
        }

        settingsNameOutputFormatComboBox = new JComboBox(epubFilenameFormats);
        settingsNameOutputFormatComboBox.setSelectedIndex(settings.getFilenameFormat());

        settingsOutputFormatComboBox = new JComboBox(epubFormats);
        settingsOutputFormatComboBox.setSelectedIndex(settings.getOutputFormat());

        settingsSeperateChaptersCheckBox = new JCheckBox();
        settingsSeperateChaptersCheckBox.setVisible(false);
        if (settingsOutputFormatComboBox.getSelectedIndex() == 1) {
            settingsSeperateChaptersCheckBox.setVisible(true);
            settingsSeperateChaptersCheckBox.setSelected(settings.isSeparateChapters());
        }

        settingsChapterTitleComboBox = new JComboBox(chapterTitleFormatOptions);
        if (settings.getChapterTitleFormat() > chapterTitleFormatOptions.length) {
            GrabberUtils.err("[CONFIG]chapterTitleFormat is outside available range.");
            settings.setChapterTitleFormat(0);
        }
        settingsChapterTitleComboBox.setSelectedIndex(settings.getChapterTitleFormat());

        settingsNovelCustomChapterTitleField = new JTextField(settings.getChapterTitleTemplate());
        boolean isCustomChapterTitle = settings.getChapterTitleFormat() == chapterTitleFormatOptions.length-1;
        settingsNovelCustomChapterTitleField.setVisible(isCustomChapterTitle);

        // Telegram settings
        settingsTeleApiTknField = new JTextField(settings.getTelegramApiToken());
        settingsTeleMaxChapterPerDayField = new JTextField(String.valueOf(settings.getTelegramMaxChapterPerDay()));
        settingsTeleMaxChapterPerNovelField = new JTextField(String.valueOf(settings.getTelegramNovelMaxChapter()));
        settingsTeleWaitTimeField = new JTextField(String.valueOf(settings.getTelegramWait()));
        settingsTeleDownloadLimitSpinner = new JSpinner();
        settingsTeleDownloadLimitSpinner.setValue(settings.getTelegramDownloadLimit());
        JComponent teleDownloadSpinnerComp = settingsTeleDownloadLimitSpinner.getEditor();
        JFormattedTextField teleDownloadSpinnerField = (JFormattedTextField) teleDownloadSpinnerComp.getComponent(0);
        teleDownloadSpinnerField.setColumns(4);
        DefaultFormatter teleDownloadSpinnerFormatter = (DefaultFormatter) teleDownloadSpinnerField.getFormatter();
        teleDownloadSpinnerFormatter.setCommitsOnValidEdit(true);

        settingsTeleImagesAllowedCheckBox =  new JCheckBox();
        settingsTeleImagesAllowedCheckBox.setSelected(settings.isTelegramImagesAllowed());

        settingsTeleInfoBtn = new JButton(new ImageIcon(getClass().getResource("/images/info_icon.png")));
        settingsTeleInfoBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsTeleInfoBtn.setContentAreaFilled(false);
        settingsTeleInfoBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Email settings
        emailHostField = new JTextField(settings.getHost());

        emailUserField = new JTextField(settings.getUsername());

        emailReceiver = new JTextField(settings.getReceiverEmail());

        emailPasswordField = new JPasswordField(settings.getPassword());
        emailPortField = new JTextField(String.valueOf(settings.getPort()));

        emailSLLComboBox = new JComboBox(sslList);
        int emailSslIndex = 0;
        switch(settings.getSsl()) {
            case "SMTP":
                emailSslIndex = 0;
                break;
            case "SMTPS":
                emailSslIndex = 1;
                break;
            case "SMTP_TLS":
                emailSslIndex = 2;
                break;
        }
        emailSLLComboBox.setSelectedIndex(emailSslIndex);

        // Library settings
        enableCheckingCheckBox = new JCheckBox();
        enableCheckingCheckBox.setSelected(settings.isPollingEnabled());

        libraryStopBtn = new JButton();
        libraryStopBtn.setVisible(false);
        if(settings.isPollingEnabled()) {
            libraryStopBtn.setVisible(true);
        }

        libraryStartBtn = new JButton();
        libraryStartBtn.setVisible(false);
        if(!settings.isPollingEnabled()) {
            libraryStartBtn.setVisible(true);
        }

        libraryFrequencySpinner = new JSpinner();
        libraryFrequencySpinner.setValue(settings.getFrequency());
        JComponent comp = libraryFrequencySpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        field.setColumns(4);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        libraryFrequencySpinner.addChangeListener(e -> settings.setFrequency((Integer) libraryFrequencySpinner.getValue()));

        libraryDoNotDisplayCoversCheckBox = new JCheckBox();
        libraryDoNotDisplayCoversCheckBox.setSelected(settings.isLibraryNoCovers());

        libraryHostListComboBox =  new JComboBox();
        buildLibHostComboBox();

        //Sources
        for(Source source: GrabberUtils.getSources()) {
            sourcesListModel.addElement(source);
        }
        sourcesJList = new JList<>(sourcesListModel);
        sourcesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourcesScrollPane = new JScrollPane(sourcesJList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.setMinimumSize(new Dimension(1080, 472));
        rootPanel.setPreferredSize(new Dimension(1060, 472));
        tabbedPane = new JTabbedPane();
        tabbedPane.setEnabled(true);
        rootPanel.add(tabbedPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        autoTab = new JPanel();
        autoTab.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Automatic", autoTab);
        autoLogPane = new JPanel();
        autoLogPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        autoTab.add(autoLogPane, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        autoLogScrollPane.setHorizontalScrollBarPolicy(31);
        autoLogScrollPane.setVerticalScrollBarPolicy(22);
        autoLogPane.add(autoLogScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logArea.setEditable(false);
        autoLogScrollPane.setViewportView(logArea);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 10, 0, 10), -1, -1));
        panel1.setFocusable(true);
        autoTab.add(panel1, new GridConstraints(2, 0, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        panel1.add(progressBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        grabChaptersButton = new JButton();
        grabChaptersButton.setEnabled(false);
        grabChaptersButton.setText("Grab chapters");
        panel1.add(grabChaptersButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopButton = new JButton();
        stopButton.setEnabled(false);
        stopButton.setText("Stop");
        stopButton.setVisible(false);
        panel1.add(stopButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 9, new Insets(0, 10, 0, 0), -1, -1));
        autoTab.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Chapter range", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        chapterAllCheckBox = new JCheckBox();
        chapterAllCheckBox.setText("All");
        chapterAllCheckBox.setToolTipText("");
        panel2.add(chapterAllCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Chapter:");
        panel2.add(label1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(firstChapter, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("-");
        panel2.add(label2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel2.add(lastChapter, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), null, 0, false));
        toLastChapter = new JCheckBox();
        toLastChapter.setText("Last");
        panel2.add(toLastChapter, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 10), -1, -1));
        autoTab.add(panel3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(null, "Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer4 = new Spacer();
        panel3.add(spacer4, new GridConstraints(1, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Wait time:");
        panel3.add(label3, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        waitTime.setText("0");
        waitTime.setToolTipText("Wait time between each chapter call in milliseconds. Please use appropriate wait times to not flood the host server.");
        panel3.add(waitTime, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        autoGetImages.setText("Get images");
        autoGetImages.setToolTipText("Download potential images from a chapter");
        panel3.add(autoGetImages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        displayChapterTitleCheckBox = new JCheckBox();
        displayChapterTitleCheckBox.setText("Add chapter title");
        panel3.add(displayChapterTitleCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        useAccountCheckBox = new JCheckBox();
        useAccountCheckBox.setText("Use account");
        panel3.add(useAccountCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 14, new Insets(10, 10, 0, 0), -1, -1));
        autoTab.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(385, -1), null, 0, false));
        NovelUrlLbl = new JLabel();
        NovelUrlLbl.setText("Novel URL:");
        panel4.add(NovelUrlLbl, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Save location:");
        panel4.add(label4, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel4.add(chapterListURL, new GridConstraints(0, 3, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel4.add(autoSaveLocation, new GridConstraints(3, 3, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel4.add(spacer5, new GridConstraints(4, 1, 1, 13, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        autoLastChapterLbl = new JLabel();
        autoLastChapterLbl.setText("Last chapter URL:");
        autoLastChapterLbl.setVisible(false);
        panel4.add(autoLastChapterLbl, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoLastChapterURL = new JTextField();
        autoLastChapterURL.setVisible(false);
        panel4.add(autoLastChapterURL, new GridConstraints(2, 3, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoFirstChapterLbl = new JLabel();
        autoFirstChapterLbl.setText("First chapter URL:");
        autoFirstChapterLbl.setVisible(false);
        panel4.add(autoFirstChapterLbl, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoCheckAvailability.setText("");
        autoCheckAvailability.setToolTipText("Check availability of novel");
        panel4.add(autoCheckAvailability, new GridConstraints(0, 13, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        browseButton.setText("");
        browseButton.setToolTipText("Browse files");
        panel4.add(browseButton, new GridConstraints(3, 13, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(10, 0, 0, 10), -1, -1));
        autoTab.add(panel5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(5, 15, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 5, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        coverImage.setBackground(new Color(-8752017));
        coverImage.setForeground(new Color(-4038459));
        coverImage.setText("");
        panel7.add(coverImage, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 133), new Dimension(100, 133), new Dimension(100, 133), 1, false));
        final Spacer spacer6 = new Spacer();
        panel7.add(spacer6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel7.add(spacer7, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(6, 7, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(0, 1, 5, 14, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Title:");
        panel8.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer8 = new Spacer();
        panel8.add(spacer8, new GridConstraints(0, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        autoBookTitle = new JLabel();
        autoBookTitle.setText("");
        panel8.add(autoBookTitle, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Author:");
        panel8.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoAuthor = new JLabel();
        autoAuthor.setText("");
        panel8.add(autoAuthor, new GridConstraints(1, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Chapter(s):");
        panel8.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoChapterAmount = new JLabel();
        autoChapterAmount.setText("");
        panel8.add(autoChapterAmount, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(100, -1), 0, false));
        autoBusyLabel.setText("");
        autoBusyLabel.setVisible(false);
        panel8.add(autoBusyLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer9 = new Spacer();
        panel8.add(spacer9, new GridConstraints(4, 1, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        pagesLbl = new JLabel();
        pagesLbl.setText("Pages:");
        pagesLbl.setVisible(false);
        panel8.add(pagesLbl, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pagesCountLbl = new JLabel();
        pagesCountLbl.setText("");
        pagesCountLbl.setVisible(false);
        panel8.add(pagesCountLbl, new GridConstraints(3, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer10 = new Spacer();
        panel8.add(spacer10, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        autoGetNumberButton.setEnabled(false);
        autoGetNumberButton.setText("");
        autoGetNumberButton.setToolTipText("Edit chapter order");
        panel8.add(autoGetNumberButton, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, 30), null, 0, false));
        autoStarredBtn.setEnabled(false);
        autoStarredBtn.setText("");
        autoStarredBtn.setToolTipText("Add to librarySettings.");
        autoStarredBtn.setVisible(true);
        panel8.add(autoStarredBtn, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, 30), null, 0, false));
        autoEditMetaBtn.setEnabled(false);
        autoEditMetaBtn.setText("");
        autoEditMetaBtn.setToolTipText("Edit novel metadata");
        panel8.add(autoEditMetaBtn, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoEditBlacklistBtn.setEnabled(false);
        autoEditBlacklistBtn.setText("");
        autoEditBlacklistBtn.setToolTipText("Edit blacklisted tags");
        panel8.add(autoEditBlacklistBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer11 = new Spacer();
        panel5.add(spacer11, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        manTab = new JPanel();
        manTab.setLayout(new GridLayoutManager(5, 5, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Manual", manTab);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        manTab.add(panel9, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(null, "Chapter select", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel10, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        chaptersFromLinksRadioButton = new JRadioButton();
        chaptersFromLinksRadioButton.setText("Table of Content");
        chaptersFromLinksRadioButton.setToolTipText("Download chapters from a table of contents page");
        panel10.add(chaptersFromLinksRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chapterToChapterRadioButton = new JRadioButton();
        chapterToChapterRadioButton.setText("Chapter to Chapter");
        chapterToChapterRadioButton.setToolTipText("Download chapters by following a \"next chapter button\"");
        panel10.add(chapterToChapterRadioButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer12 = new Spacer();
        panel10.add(spacer12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer13 = new Spacer();
        panel10.add(spacer13, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer14 = new Spacer();
        panel10.add(spacer14, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 2, new Insets(0, 10, 0, 10), -1, -1));
        panel9.add(panel11, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        chapterFromListPanel = new JPanel();
        chapterFromListPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        chapterFromListPanel.setVisible(false);
        panel11.add(chapterFromListPanel, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        manTocURLlbl = new JLabel();
        manTocURLlbl.setText("Table of Contents URL:");
        manTocURLlbl.setVisible(true);
        chapterFromListPanel.add(manTocURLlbl, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manNovelURL = new JTextField();
        manNovelURL.setEnabled(true);
        manNovelURL.setVisible(true);
        chapterFromListPanel.add(manNovelURL, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        getLinksButton = new JButton();
        getLinksButton.setEnabled(true);
        getLinksButton.setText("Get links");
        getLinksButton.setVisible(true);
        chapterFromListPanel.add(getLinksButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chapterToChapterPanel = new JPanel();
        chapterToChapterPanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        chapterToChapterPanel.setVisible(false);
        panel11.add(chapterToChapterPanel, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("First chapter URL:");
        chapterToChapterPanel.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        firstChapterField = new JTextField();
        chapterToChapterPanel.add(firstChapterField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Last chapter URL:");
        chapterToChapterPanel.add(label9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lastChapterField = new JTextField();
        chapterToChapterPanel.add(lastChapterField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Next chapter button [css Selector]:");
        chapterToChapterPanel.add(label10, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextChapterButtonField = new JTextField();
        chapterToChapterPanel.add(nextChapterButtonField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        manChapterToChapterNumberField = new JTextField();
        manChapterToChapterNumberField.setText("1");
        chapterToChapterPanel.add(manChapterToChapterNumberField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, 1, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("<html><p style=\"width:800px\">Chapter to chapter navigation will follow the \"next chapter button\" links till the specified last chapter while downloading each traversed chapter.</p></html>");
        chapterToChapterPanel.add(label11, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 0, 0), -1, -1));
        manTab.add(panel12, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        panel12.add(tabbedPane1, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Links", panel13);
        panel13.add(manLinkScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        manLinkList.setLayoutOrientation(1);
        manLinkList.setSelectionMode(2);
        manLinkList.setVisibleRowCount(0);
        manLinkScrollPane.setViewportView(manLinkList);
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Log", panel14);
        panel14.add(manLogScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        manLogScrollPane.setViewportView(manLogArea);
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        manTab.add(panel15, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manProgressBar = new JProgressBar();
        panel15.add(manProgressBar, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manGrabChaptersButton = new JButton();
        manGrabChaptersButton.setText("Grab chapters");
        panel15.add(manGrabChaptersButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manStopButton = new JButton();
        manStopButton.setEnabled(false);
        manStopButton.setText("Stop");
        manStopButton.setVisible(false);
        panel15.add(manStopButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        manTab.add(panel16, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel17.setBorder(BorderFactory.createTitledBorder(null, "Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label12 = new JLabel();
        label12.setText("Wait time:");
        panel17.add(label12, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel17.add(manWaitTime, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        manUseAccountCheckBox = new JCheckBox();
        manUseAccountCheckBox.setText("Use Account");
        manUseAccountCheckBox.setToolTipText("You can set the cookies for manual grabbing on \"Manual\" source in the options.");
        panel17.add(manUseAccountCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manGetImages = new JCheckBox();
        manGetImages.setText("Get images");
        panel17.add(manGetImages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manUseHeaderlessBrowser = new JCheckBox();
        manUseHeaderlessBrowser.setText("Use Headerless Browser");
        manUseHeaderlessBrowser.setToolTipText("Novel-Grabber will visit the websites in a browser.");
        panel17.add(manUseHeaderlessBrowser, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manDispalyChapterTitleCheckbox = new JCheckBox();
        manDispalyChapterTitleCheckbox.setText("Display chapter title");
        panel17.add(manDispalyChapterTitleCheckbox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel18, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setText("Save location:");
        panel18.add(label13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel18.add(manSaveLocation, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        manBrowseLocationButton.setText("");
        manBrowseLocationButton.setToolTipText("Browse files");
        panel18.add(manBrowseLocationButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Chapter container:");
        panel18.add(label14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manChapterContainer = new JTextField();
        manChapterContainer.setText("");
        panel18.add(manChapterContainer, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        manJsoupInfoButton.setText("");
        panel18.add(manJsoupInfoButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manDetectChapterContainerCheckBox = new JCheckBox();
        manDetectChapterContainerCheckBox.setText("Auto detect");
        panel18.add(manDetectChapterContainerCheckBox, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer15 = new Spacer();
        panel18.add(spacer15, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        manPageLbl = new JLabel();
        manPageLbl.setText("Pages:");
        manPageLbl.setVisible(false);
        panel18.add(manPageLbl, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manPageCounter = new JLabel();
        manPageCounter.setText("");
        manPageCounter.setVisible(false);
        panel18.add(manPageCounter, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(1, 9, new Insets(0, 10, 0, 10), -1, -1));
        manTab.add(panel19, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manRemoveLinksBtn.setEnabled(false);
        manRemoveLinksBtn.setText("");
        manRemoveLinksBtn.setToolTipText("Remove link(s)");
        panel19.add(manRemoveLinksBtn, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(30, 30), null, 0, false));
        final Spacer spacer16 = new Spacer();
        panel19.add(spacer16, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        manAddChapterButton.setText("");
        manAddChapterButton.setToolTipText("Add links");
        panel19.add(manAddChapterButton, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manDetectChaptersBtn.setEnabled(false);
        manDetectChaptersBtn.setText("");
        manDetectChaptersBtn.setToolTipText("Detect chapters [experimental]");
        panel19.add(manDetectChaptersBtn, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manSetMetadataButton.setEnabled(true);
        manSetMetadataButton.setText("");
        manSetMetadataButton.setToolTipText("Edit novel metadata");
        panel19.add(manSetMetadataButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manBlackListedTags.setEnabled(true);
        manBlackListedTags.setText("");
        manBlackListedTags.setToolTipText("Edit blacklisted tags");
        panel19.add(manBlackListedTags, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manReverseBtn.setEnabled(false);
        manReverseBtn.setText("");
        manReverseBtn.setToolTipText("Reverse list");
        panel19.add(manReverseBtn, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        manChapterAmountLbl = new JLabel();
        manChapterAmountLbl.setText("Label");
        manChapterAmountLbl.setVisible(false);
        panel19.add(manChapterAmountLbl, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer17 = new Spacer();
        panel19.add(spacer17, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        libraryTab = new JPanel();
        libraryTab.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), 0, 0));
        libraryTab.setAlignmentX(0.0f);
        libraryTab.setAlignmentY(0.0f);
        tabbedPane.addTab("Library", libraryTab);
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 5, new Insets(5, 15, 5, 15), 0, 0));
        libraryTab.add(panel20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        libraryOnlyShowNovelsWithCheckBox.setText("Only new releases");
        libraryOnlyShowNovelsWithCheckBox.setToolTipText("Only show novels if the have new chapter releases");
        panel20.add(libraryOnlyShowNovelsWithCheckBox, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer18 = new Spacer();
        panel20.add(spacer18, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        librarySearchField = new JTextField();
        librarySearchField.setRequestFocusEnabled(true);
        panel20.add(librarySearchField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(350, -1), null, 0, false));
        searchButton = new JButton();
        searchButton.setText("Search");
        panel20.add(searchButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel20.add(libraryHostListComboBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        libraryNovelPanel = new JPanel();
        libraryNovelPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 10, 0, 10), -1, -1));
        libraryNovelPanel.setAlignmentX(0.0f);
        libraryNovelPanel.setAlignmentY(0.0f);
        libraryTab.add(libraryNovelPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        libraryScrollPane.setAutoscrolls(false);
        libraryScrollPane.setBackground(new Color(-12512248));
        libraryNovelPanel.add(libraryScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        libraryScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        libraryPanel = new JPanel();
        libraryPanel.setLayout(new GridBagLayout());
        libraryPanel.setEnabled(true);
        libraryPanel.setOpaque(true);
        libraryScrollPane.setViewportView(libraryPanel);
        settingsTab = new JPanel();
        settingsTab.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane.addTab("Settings", settingsTab);
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(11, 1, new Insets(10, 10, 0, 5), -1, -1));
        settingsTab.add(panel21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsLibraryBtn.setText("Library");
        panel21.add(settingsLibraryBtn, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsEmailBtn.setText("Email");
        panel21.add(settingsEmailBtn, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel21.add(separator1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel21.add(separator2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsGeneralBtn.setText("General");
        panel21.add(settingsGeneralBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsContributeBtn.setText("Contribute");
        panel21.add(settingsContributeBtn, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel21.add(separator3, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsSourcesBtn.setText("Sources");
        panel21.add(settingsSourcesBtn, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator4 = new JSeparator();
        panel21.add(separator4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsTelegramBotBtn.setText("Telegram Bot");
        panel21.add(settingsTelegramBotBtn, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator5 = new JSeparator();
        panel21.add(separator5, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(5, 1, new Insets(5, 0, 10, 10), -1, -1));
        settingsTab.add(panel22, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsGeneralPanel = new JPanel();
        settingsGeneralPanel.setLayout(new GridLayoutManager(10, 4, new Insets(15, 15, 15, 15), -1, -1));
        settingsGeneralPanel.setVisible(true);
        panel22.add(settingsGeneralPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsGeneralPanel.setBorder(BorderFactory.createTitledBorder(null, "General settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label15 = new JLabel();
        label15.setText("EPUB filename format:");
        settingsGeneralPanel.add(label15, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 28), null, 0, false));
        settingsGeneralPanel.add(settingsNameOutputFormatComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer19 = new Spacer();
        settingsGeneralPanel.add(spacer19, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("EPUB  format:");
        settingsGeneralPanel.add(label16, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 28), null, 0, false));
        settingsGeneralPanel.add(settingsOutputFormatComboBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsSeperateChaptersCheckBox.setText("Seperate chapters");
        settingsSeperateChaptersCheckBox.setVisible(false);
        settingsGeneralPanel.add(settingsSeperateChaptersCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setText("Browser:");
        label17.setVisible(true);
        settingsGeneralPanel.add(label17, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 28), null, 0, false));
        settingsBrowserComboBox.setToolTipText("Pick your browser if you have selected 'Use Headerless Browser'");
        settingsGeneralPanel.add(settingsBrowserComboBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        settingsGeneralPanel.add(panel23, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel23.add(settingsSavelocationField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, -1), null, 0, false));
        final Spacer spacer20 = new Spacer();
        panel23.add(spacer20, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsBrowseSaveLocationBtn.setText("");
        settingsBrowseSaveLocationBtn.setToolTipText("Browse files");
        panel23.add(settingsBrowseSaveLocationBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        standardSaveLocationCheckBox.setText("Use default save location:");
        settingsGeneralPanel.add(standardSaveLocationCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 28), null, 0, false));
        settingsAlwaysGetImagesCheckBox.setText("Always get images");
        settingsAlwaysGetImagesCheckBox.setToolTipText("Download potential images from a chapter");
        settingsGeneralPanel.add(settingsAlwaysGetImagesCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 28), null, 0, false));
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        settingsGeneralPanel.add(panel24, new GridConstraints(8, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsSaveBtn = new JButton();
        settingsSaveBtn.setText("Save");
        panel24.add(settingsSaveBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer21 = new Spacer();
        panel24.add(spacer21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer22 = new Spacer();
        settingsGeneralPanel.add(spacer22, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsNotificationWhenFinishedCheckBox.setText("Notification when finished");
        settingsGeneralPanel.add(settingsNotificationWhenFinishedCheckBox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Theme:");
        settingsGeneralPanel.add(label18, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsGeneralPanel.add(settingsGuiThemeComboBox, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Font:");
        settingsGeneralPanel.add(label19, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsGeneralPanel.add(settingsGuiFontComboBox, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsLibraryPanel = new JPanel();
        settingsLibraryPanel.setLayout(new GridLayoutManager(4, 5, new Insets(15, 15, 15, 15), -1, -1));
        settingsLibraryPanel.setVisible(false);
        panel22.add(settingsLibraryPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsLibraryPanel.setBorder(BorderFactory.createTitledBorder(null, "Library settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer23 = new Spacer();
        settingsLibraryPanel.add(spacer23, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(4, 5, new Insets(0, 0, 0, 0), -1, -1));
        settingsLibraryPanel.add(panel25, new GridConstraints(0, 0, 2, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer24 = new Spacer();
        panel25.add(spacer24, new GridConstraints(0, 4, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer25 = new Spacer();
        panel25.add(spacer25, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Check frequency (minutes):");
        panel25.add(label20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel25.add(libraryFrequencySpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        libraryStartBtn.setText("Start");
        panel25.add(libraryStartBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        libraryStopBtn.setText("Stop");
        panel25.add(libraryStopBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        libraryDoNotDisplayCoversCheckBox.setText("Do not display covers");
        libraryDoNotDisplayCoversCheckBox.setToolTipText("Will improve performance");
        panel25.add(libraryDoNotDisplayCoversCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer26 = new Spacer();
        settingsLibraryPanel.add(spacer26, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsEmailPanel = new JPanel();
        settingsEmailPanel.setLayout(new GridLayoutManager(2, 3, new Insets(5, 15, 15, 15), -1, -1));
        settingsEmailPanel.setVisible(false);
        panel22.add(settingsEmailPanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsEmailPanel.setBorder(BorderFactory.createTitledBorder(null, "Email settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new GridLayoutManager(4, 6, new Insets(5, 5, 5, 5), -1, -1));
        settingsEmailPanel.add(panel26, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel26.setBorder(BorderFactory.createTitledBorder(null, "Server connection", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label21 = new JLabel();
        label21.setText("Server hostname:");
        panel26.add(label21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel26.add(emailHostField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        final JLabel label22 = new JLabel();
        label22.setText("Port:");
        panel26.add(label22, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label23 = new JLabel();
        label23.setText("Username:");
        panel26.add(label23, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label24 = new JLabel();
        label24.setText("Password:");
        panel26.add(label24, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel26.add(emailUserField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        panel26.add(emailPasswordField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label25 = new JLabel();
        label25.setText("SSL:");
        panel26.add(label25, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel26.add(emailSLLComboBox, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        emailSaveBtn = new JButton();
        emailSaveBtn.setText("Save");
        panel26.add(emailSaveBtn, new GridConstraints(3, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer27 = new Spacer();
        panel26.add(spacer27, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panel26.add(emailPortField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(20, -1), null, 0, false));
        final Spacer spacer28 = new Spacer();
        panel26.add(spacer28, new GridConstraints(0, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label26 = new JLabel();
        label26.setText("Send emails to:");
        panel26.add(label26, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel26.add(emailReceiver, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer29 = new Spacer();
        settingsEmailPanel.add(spacer29, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        settingsSourcesPanel = new JPanel();
        settingsSourcesPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 5), -1, -1));
        settingsSourcesPanel.setVisible(false);
        panel22.add(settingsSourcesPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsSourcesPanel.setBorder(BorderFactory.createTitledBorder(null, "Sources", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel27 = new JPanel();
        panel27.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        settingsSourcesPanel.add(panel27, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel27.add(sourcesScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sourcesScrollPane.setViewportView(sourcesJList);
        final Spacer spacer30 = new Spacer();
        settingsSourcesPanel.add(spacer30, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel28 = new JPanel();
        panel28.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        settingsSourcesPanel.add(panel28, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sourcesLoginPanel = new JPanel();
        sourcesLoginPanel.setLayout(new GridLayoutManager(1, 7, new Insets(0, 0, 5, 0), -1, -1));
        sourcesLoginPanel.setVisible(false);
        panel28.add(sourcesLoginPanel, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sourcesLoginPanel.setBorder(BorderFactory.createTitledBorder(null, "Login", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        openBrowserButton = new JButton();
        openBrowserButton.setText("Open Browser");
        sourcesLoginPanel.add(openBrowserButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editCookiesButton = new JButton();
        editCookiesButton.setText("Edit Cookies");
        sourcesLoginPanel.add(editCookiesButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer31 = new Spacer();
        sourcesLoginPanel.add(spacer31, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer32 = new Spacer();
        sourcesLoginPanel.add(spacer32, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer33 = new Spacer();
        sourcesLoginPanel.add(spacer33, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        saveCookiesButton = new JButton();
        saveCookiesButton.setEnabled(false);
        saveCookiesButton.setText("Save Cookies");
        saveCookiesButton.setVisible(true);
        sourcesLoginPanel.add(saveCookiesButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer34 = new Spacer();
        sourcesLoginPanel.add(spacer34, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer35 = new Spacer();
        panel28.add(spacer35, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sourceCanUseHeadlessPanel = new JPanel();
        sourceCanUseHeadlessPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        sourceCanUseHeadlessPanel.setVisible(false);
        panel28.add(sourceCanUseHeadlessPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sourceCanUseHeadlessPanel.setBorder(BorderFactory.createTitledBorder(null, "Grabbing", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        useHeadlessCheckBox = new JCheckBox();
        useHeadlessCheckBox.setText("Use Headless");
        sourceCanUseHeadlessPanel.add(useHeadlessCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer36 = new Spacer();
        sourceCanUseHeadlessPanel.add(spacer36, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsTelegramPanel = new JPanel();
        settingsTelegramPanel.setLayout(new GridLayoutManager(8, 6, new Insets(15, 15, 15, 15), -1, -1));
        settingsTelegramPanel.setVisible(false);
        panel22.add(settingsTelegramPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsTelegramPanel.setBorder(BorderFactory.createTitledBorder(null, "Telegram Bot", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final Spacer spacer37 = new Spacer();
        settingsTelegramPanel.add(spacer37, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel29 = new JPanel();
        panel29.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        settingsTelegramPanel.add(panel29, new GridConstraints(6, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel29.add(saveButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer38 = new Spacer();
        panel29.add(spacer38, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsTeleStartBtn = new JButton();
        settingsTeleStartBtn.setText("Start");
        panel29.add(settingsTeleStartBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsTeleStatusLbl = new JLabel();
        settingsTeleStatusLbl.setText("");
        panel29.add(settingsTeleStatusLbl, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsTeleStopBtn = new JButton();
        settingsTeleStopBtn.setText("Stop");
        settingsTeleStopBtn.setVisible(false);
        panel29.add(settingsTeleStopBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer39 = new Spacer();
        settingsTelegramPanel.add(spacer39, new GridConstraints(1, 3, 5, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        settingsTeleMaxChapterPerNovelField.setVisible(true);
        settingsTelegramPanel.add(settingsTeleMaxChapterPerNovelField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        settingsTeleWaitTimeField.setText("0");
        settingsTeleWaitTimeField.setToolTipText("Wait time between each chapter call in milliseconds. Please use appropriate wait times to not flood the host server.");
        settingsTeleWaitTimeField.setVisible(true);
        settingsTelegramPanel.add(settingsTeleWaitTimeField, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JPanel panel30 = new JPanel();
        panel30.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        settingsTelegramPanel.add(panel30, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer40 = new Spacer();
        panel30.add(spacer40, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        panel30.add(settingsTeleApiTknField, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, -1), null, 0, false));
        settingsTeleInfoBtn.setText("");
        settingsTeleInfoBtn.setToolTipText("You can get an API token from @BotFather");
        panel30.add(settingsTeleInfoBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label27 = new JLabel();
        label27.setText("Telegram API Token");
        label27.setToolTipText("Your Telegram API Token");
        settingsTelegramPanel.add(label27, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label28 = new JLabel();
        label28.setText("Max. chapter per day");
        label28.setToolTipText("Limit how many chapter can be downloaded in a day by a user. (-1 for unlimited)");
        settingsTelegramPanel.add(label28, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label29 = new JLabel();
        label29.setText("Max. chapter per novel");
        label29.setToolTipText("Limit downloades to novels of a certain chapter length. (-1 for unlimited)");
        settingsTelegramPanel.add(label29, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label30 = new JLabel();
        label30.setText("Wait time");
        label30.setToolTipText("Set a static wait time between each chapter in miliseconds (0 for no wait time)");
        settingsTelegramPanel.add(label30, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsTelegramPanel.add(settingsTeleMaxChapterPerDayField, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        final JLabel label31 = new JLabel();
        label31.setText("Concurrent downloads");
        settingsTelegramPanel.add(label31, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        settingsTelegramPanel.add(settingsTeleDownloadLimitSpinner, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        settingsTeleImagesAllowedCheckBox.setText("Images allowed");
        settingsTelegramPanel.add(settingsTeleImagesAllowedCheckBox, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
