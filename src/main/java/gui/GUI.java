package gui;

import grabber.*;
import grabber.sources.Source;
import org.openqa.selenium.Cookie;
import grabber.Accounts;
import library.Library;
import library.LibraryNovel;
import system.Config;
import system.init;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private static String[] sslList = {"SMTP","SMTPS","SMTP TLS",};
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
    public JCheckBox autoRemoveStyling;
    public JCheckBox manNoStyling;
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
    private JCheckBox settingsAlwaysRemoveStylingCheckBox;
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
    private JButton startButton;
    private JButton stopButton1;
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
    private JButton manEditChapterOrder;
    public JTextArea autoBookDescArea;
    private JScrollPane autoBookDescScrollPane;
    private JButton autoEditMetadataButton;


    public GUI() {
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
                        .removeStyling(autoRemoveStyling.isSelected())
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
                                .removeStyling(manNoStyling.isSelected())
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
                                .removeStyling(manNoStyling.isSelected())
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
            settings.setSaveLocation(settingsSavelocationField.getText());
            settings.setUseStandardLocation(standardSaveLocationCheckBox.isSelected());
            settings.setRemoveStyling(settingsAlwaysRemoveStylingCheckBox.isSelected());
            settings.setAutoGetImages(settingsAlwaysGetImagesCheckBox.isSelected());
            settings.setBrowser(settingsBrowserComboBox.getSelectedItem().toString());
            settings.setFilenameFormat(settingsNameOutputFormatComboBox.getSelectedIndex());
            settings.setOutputFormat(settingsOutputFormatComboBox.getSelectedIndex());
            settings.setSeparateChapters(settingsSeperateChaptersCheckBox.isSelected());
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

            settingsGeneralPanel.setVisible(true);
        });
        settingsLibraryBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);

            settingsLibraryPanel.setVisible(true);
        });
        settingsEmailBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsSourcesPanel.setVisible(false);

            settingsEmailPanel.setVisible(true);
        });
        settingsSourcesBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);

            settingsSourcesPanel.setVisible(true);
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
    }

    public void buildLibrary() {
        libraryPanel.removeAll();
        int gridRow = 0;
        int gridCol = 0;
        for(LibraryNovel libNovel: library.getNovels()) {
            GridBagConstraints c;
            JPanel novelPane = new JPanel(new GridBagLayout());

            // Cover
            c = new GridBagConstraints();
            String novelCover = libNovel.getSaveLocation() + "/cover." + libNovel.getMetadata().getCoverFormat();
            JButton novelImage;
            if(novelCover.isEmpty()) {
                novelImage = new JButton(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
            } else {
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

            // Name
            c = new GridBagConstraints();
            JLabel novelTitle = new JLabel("<html><p style=\"width:275px\">" + libNovel.getMetadata().getTitle() + "</p></html>");
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

            // Show manual download button if new chapters available
            int chapterDiff = libNovel.getNewestChapterNumber() - libNovel.getLastLocalChapterNumber();
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
                                .useAccount(libNovel.isUseAccount())
                                .firstChapter(libNovel.getLastLocalChapterNumber() + 1)
                                .lastChapter(libNovel.getNewestChapterNumber())
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
        libraryPanel.validate();
        libraryPanel.repaint();
    }

    public void libraryIsChecking(boolean isChecking) {
        if (isChecking) {
            tabbedPane.setIconAt(2, new ImageIcon(getClass().getResource("/images/busy.gif")));
        } else {
            tabbedPane.setIconAt(2, null);
        }
    }

    private void initialize() {
        add(rootPanel);
        setTitle("Novel-Grabber " + init.versionNumber);
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        setMinimumSize(new Dimension(1000, 700));
        //Tray();
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //if (!SystemTray.isSupported()) {
        //    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //}
        addWindowListener(new WindowAdapter() {
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

        autoRemoveStyling = new JCheckBox();
        autoRemoveStyling.setSelected(settings.isRemoveStyling());

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

        // Settings Tab
        settingsGeneralBtn = new JButton("General", new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        settingsGeneralBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsGeneralBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsGeneralBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsGeneralBtn.setContentAreaFilled(false);
        settingsGeneralBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsEmailBtn = new JButton("Email", new ImageIcon(getClass().getResource("/images/email_icon.png")));
        settingsEmailBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsEmailBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsEmailBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsEmailBtn.setContentAreaFilled(false);
        settingsEmailBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsLibraryBtn = new JButton("Library", new ImageIcon(getClass().getResource("/images/library_icon.png")));
        settingsLibraryBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsLibraryBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsLibraryBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsLibraryBtn.setContentAreaFilled(false);
        settingsLibraryBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsContributeBtn = new JButton("Contribute", new ImageIcon(getClass().getResource("/images/heart_icon.png")));
        settingsContributeBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsContributeBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsContributeBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsContributeBtn.setContentAreaFilled(false);
        settingsContributeBtn.setToolTipText("Buy the dev a coffee");
        settingsContributeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        settingsSourcesBtn = new JButton("Contribute", new ImageIcon(getClass().getResource("/images/webpage_icon.png")));
        settingsSourcesBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsSourcesBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsSourcesBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsSourcesBtn.setContentAreaFilled(false);
        settingsSourcesBtn.setToolTipText("Buy the dev a coffee");
        settingsSourcesBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

        settingsAlwaysGetImagesCheckBox = new JCheckBox();
        settingsAlwaysGetImagesCheckBox.setSelected(settings.isAutoGetImages());

        settingsAlwaysRemoveStylingCheckBox = new JCheckBox();
        settingsAlwaysRemoveStylingCheckBox.setSelected(settings.isRemoveStyling());

        standardSaveLocationCheckBox = new JCheckBox();
        standardSaveLocationCheckBox.setSelected(settings.isUseStandardLocation());

        if(settings.isUseStandardLocation()) {
            autoSaveLocation.setText(settings.getSaveLocation());
            manSaveLocation.setText(settings.getSaveLocation());
        }

        settingsSavelocationField = new JTextField();
        settingsSavelocationField.setText(settings.getSaveLocation());

        settingsBrowseSaveLocationBtn = new JButton(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        settingsBrowseSaveLocationBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsBrowseSaveLocationBtn.setContentAreaFilled(false);
        settingsBrowseSaveLocationBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

        // Email settings
        emailHostField = new JTextField(settings.getHost());

        emailUserField = new JTextField(settings.getUsername());

        emailReceiver = new JTextField(settings.getReceiverEmail());

        emailPasswordField = new JPasswordField(settings.getPassword());
        emailPortField = new JTextField(String.valueOf(settings.getPort()));

        emailSLLComboBox = new JComboBox(sslList);
        int selectedIndex = 0;
        switch(settings.getSsl()) {
            case "SMTP":
                selectedIndex = 0;
                break;
            case "SMTPS":
                selectedIndex = 1;
                break;
            case "SMTP_TLS":
                selectedIndex = 2;
                break;
        }
        emailSLLComboBox.setSelectedIndex(selectedIndex);

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

        //Sources
        for(Source source: GrabberUtils.getSources()) {
            sourcesListModel.addElement(source);
        }
        sourcesJList = new JList<>(sourcesListModel);
        sourcesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourcesScrollPane = new JScrollPane(sourcesJList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
