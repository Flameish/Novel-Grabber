package gui;

import grabber.*;
import system.data.accounts.Account;
import system.data.accounts.Accounts;
import system.data.library.LibrarySettings;
import system.data.library.LibraryNovel;
import system.data.*;
import system.init;
import system.library.LibrarySystem;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    private static final String[] headerlessBrowserWebsites = {"FoxTeller","MoonQuill"};
    private static final String[] noHeaderlessBrowserWebsites = {"WattPad", "FanFiction", "FanFiktion"};
    private static final String[] loginWebsites = {"Booklat","Wuxiaworld"};
    private static final String[] settingsMenus = {"Accounts","General", "Library", "Email", "Update"};
    public static List<String> headerlessBrowserWebsitesList = Arrays.asList(headerlessBrowserWebsites);
    public static List<String> noHeaderlessBrowserWebsitesList = Arrays.asList(noHeaderlessBrowserWebsites);
    public static List<String> loginWebsitesList = Arrays.asList(loginWebsites);
    public static DefaultListModel<Chapter> manLinkListModel = new DefaultListModel<>();
    public static DefaultListModel<String> accountWebsiteListModel = new DefaultListModel<>();
    public static DefaultListModel<String> settingsMenuModel = new DefaultListModel<>();
    public static List<String> blacklistedTags = new ArrayList<>();
    public static TrayIcon trayIcon;
    public static Integer chapterToChapterNumber = 1;
    private static String[] browserList = {"Chrome", "Firefox", "Edge", "Opera", "IE"};
    private static String[] epubFilenameFormats = {"<author> - <title>", "<title> - <author>", "<title>"};
    private static String[] sslList = {"SMTP","SMTPS","SMTP TLS",};
    private static MenuItem defaultItem0;
    private final String NL = System.getProperty("line.separator");
    private static final Settings settings = Settings.getInstance();
    private static final LibrarySettings librarySettings = LibrarySettings.getInstance();
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
    private JPanel settingsAccountsPanel;
    private JPanel settingsHeadlessPanel;
    private JButton emailSaveBtn;
    private JComboBox settingsOutputFormatComboBox;
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
    private JButton manChapterPreviewBtn;
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
    private JButton settingsAccountsBtn;
    private JButton settingsGeneralBtn;
    private JButton settingsLibraryBtn;
    private JButton settingsEmailBtn;
    private JButton settingsUpdateBtn;
    private JPanel accountEditPanel;
    private JButton settingsContributeBtn;
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
                autoBusyLabel.setVisible(true);

                // Create novel object with settings
                autoNovel = Novel.builder()
                        .novelLink(chapterListURL.getText())
                        .window("auto")
                        .browser(settings.getBrowser())
                        .useAccount(useAccountCheckBox.isSelected())
                        .build();
                autoNovel.check();
                updateMetadataDisplay();
                autoBusyLabel.setVisible(false);
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
            pagesLbl.setVisible(true);
            pagesCountLbl.setVisible(true);
            grabChaptersButton.setEnabled(false);
            grabChaptersButton.setVisible(false);
            stopButton.setEnabled(true);
            stopButton.setVisible(true);
            try {
                autoNovel = Novel.modifier(autoNovel)
                        .saveLocation(autoSaveLocation.getText())
                        .waitTime(Integer.parseInt(waitTime.getText()))
                        .displayChapterTitle(displayChapterTitleCheckBox.isSelected())
                        .removeStyling(autoRemoveStyling.isSelected())
                        .getImages(autoGetImages.isSelected())
                        .browser(settings.getBrowser())
                        .useAccount(useAccountCheckBox.isSelected())
                        .build();
                // Get chapter range
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
                        .firstChapter(tempFirstChapter)
                        .lastChapter(tempLastChapter)
                        .build();
                autoNovel.downloadChapters(); // Throws exception if grabbing was stopped
                autoNovel.output();
            } catch (Exception  err) {
                appendText("auto", "[ERROR]"+err.getMessage());
                err.printStackTrace();
                autoNovel.killTask = false;
            }
            progressBar.setStringPainted(false);
            progressBar.setValue(0);
            grabChaptersButton.setEnabled(true);
            grabChaptersButton.setVisible(true);
            stopButton.setEnabled(false);
            stopButton.setVisible(false);

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
                            .window("manual")
                            .useHeadless(manUseHeaderlessBrowser.isSelected())
                            .browser(settings.getBrowser())
                            .build();
                    manNovel.check();
                } catch (NullPointerException err) {
                    err.printStackTrace();
                    appendText("manual", "[ERROR]" + err.getMessage());
                } finally {
                    if (!manLinkListModel.isEmpty()) {
                        manRemoveLinksBtn.setEnabled(true);
                        manChapterPreviewBtn.setEnabled(true);
                        manDetectChaptersBtn.setEnabled(true);
                        manReverseBtn.setEnabled(true);
                        manChapterAmountLbl.setVisible(true);
                        manChapterAmountLbl.setText("Chapters: "+manLinkListModel.size());
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
                    manPageCounter.setText("");
                    manPageCounter.setVisible(true);
                    manPageLbl.setVisible(true);
                    manGrabChaptersButton.setEnabled(false);
                    manGrabChaptersButton.setVisible(false);
                    manStopButton.setEnabled(true);
                    manStopButton.setVisible(true);
                    manProgressBar.setStringPainted(true);
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
                                .build();
                        manNovel.processChaptersToChapters(firstChapterField.getText(), lastChapterField.getText(), nextChapterButtonField.getText(), manChapterToChapterNumberField.getText());
                        manNovel.output();
                    } catch (Exception err) {
                        appendText("manual", "[ERROR]"+err.getMessage());
                        err.printStackTrace();
                        manNovel.killTask = false;
                    } finally {
                        manProgressBar.setStringPainted(false);
                        manProgressBar.setValue(0);
                        manGrabChaptersButton.setEnabled(true);
                        manGrabChaptersButton.setVisible(true);
                        manStopButton.setEnabled(false);
                        manStopButton.setVisible(false);
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
                    manPageCounter.setText("");
                    manPageCounter.setVisible(true);
                    manPageLbl.setVisible(true);
                    manGrabChaptersButton.setEnabled(false);
                    manGrabChaptersButton.setVisible(false);
                    manStopButton.setEnabled(true);
                    manStopButton.setVisible(true);
                    manProgressBar.setStringPainted(true);
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
                                .build();
                        manNovel.downloadChapters();
                        manNovel.output();
                    } catch (Exception err) {
                        appendText("manual", "[ERROR]"+err.getMessage());
                        err.printStackTrace();
                        manNovel.killTask = false;
                    } finally {
                        manProgressBar.setStringPainted(false);
                        manProgressBar.setValue(0);
                        manGrabChaptersButton.setEnabled(true);
                        manGrabChaptersButton.setVisible(true);
                        manStopButton.setEnabled(false);
                        manStopButton.setVisible(false);
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
                    manChapterPreviewBtn.setEnabled(false);
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
                ex.printStackTrace();
            }
        });

        manAddChapterButton.addActionListener(actionEvent -> {
            editChapterList.main("manual");
            if (!manLinkListModel.isEmpty()) {
                manRemoveLinksBtn.setEnabled(true);
                manChapterPreviewBtn.setEnabled(true);
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

        manChapterPreviewBtn.addActionListener(actionEvent -> {
            if(manLinkList.getSelectedIndex() >= 0) {
                Chapter chapter = manLinkListModel.getElementAt(manLinkList.getSelectedIndex());
                chapter.saveChapter(manNovel);
                chapterPreview.main(chapter.chapterContent);
            }
        });



        // Accounts
        accountWebsiteList.addListSelectionListener(listSelectionEvent -> {
            accountEditPanel.setVisible(true);
            Border border = BorderFactory.createTitledBorder(accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex()));
            accountEditPanel.setBorder(border);
            Account account = Accounts.getInstance().getAccount(accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex()));
            accountUsernameField.setText(account.getUsername());
            accountPasswordField.setText(account.getPassword());
            accountAddBtn.setVisible(true);
            if(!accountUsernameField.getText().isEmpty()) {
                accountAddBtn.setText("Edit");
            } else {
                accountAddBtn.setText("Add");
            }
        });
        accountAddBtn.addActionListener(actionEvent -> {
            String domain = accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex());
            Account account = Accounts.getInstance().getAccount(domain);
            account.setUsername(accountUsernameField.getText());
            account.setPassword(accountPasswordField.getText());
            Accounts.getInstance().addAccount(account);
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
            settings.setFilenameFormat(settingsOutputFormatComboBox.getSelectedIndex());
            settings.save();
        });

        autoStarredBtn.addActionListener(actionEvent -> {
            if(librarySettings.isStarred(autoNovel.novelLink)) {
                librarySettings.removeStarred(autoNovel.novelLink);
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/unstarred_icon.png")));
            } else {
                librarySettings.setStarred(autoNovel);
                librarySettings.save();
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
            init.librarySystem.scheduler.shutdown();
        });
        libraryStartBtn.addActionListener(actionEvent -> {
            libraryStartBtn.setEnabled(false);
            libraryStartBtn.setVisible(false);
            libraryStopBtn.setEnabled(true);
            libraryStopBtn.setVisible(true);
            settings.setFrequency((Integer) libraryFrequencySpinner.getValue());
            settings.setPollingEnabled(true);
            settings.save();
            init.librarySystem = new LibrarySystem();
        });
        settingsAccountsBtn.addActionListener(actionEvent -> {
            settingsGeneralPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);

            settingsAccountsPanel.setVisible(true);
        });
        settingsGeneralBtn.addActionListener(actionEvent -> {
            settingsAccountsPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);

            settingsGeneralPanel.setVisible(true);
        });
        settingsLibraryBtn.addActionListener(actionEvent -> {
            settingsAccountsPanel.setVisible(false);
            settingsGeneralPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);

            settingsLibraryPanel.setVisible(true);
        });
        settingsEmailBtn.addActionListener(actionEvent -> {
            settingsAccountsPanel.setVisible(false);
            settingsGeneralPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);

            settingsEmailPanel.setVisible(true);
        });
        settingsContributeBtn.addActionListener(e -> {
            try {
                GrabberUtils.openWebpage(new URI("https://www.paypal.com/paypalme/flameish"));
            } catch (URISyntaxException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }
        });
    }

    public void buildLibrary() {
        //libraryPanel.setVisible(false);
        libraryPanel.removeAll();
        libraryPanel.setVisible(true);
        int gridRow = 0;
        int gridCol = 0;
        for(LibraryNovel libNovel: librarySettings.getStarredNovels()) {
            JPanel novelPane = new JPanel();
            novelPane.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridBagLayout());

            JLabel novelTitle = new JLabel(libNovel.getTitle());
            novelTitle.setFont(new Font("Calibri", Font.BOLD, 17));
            novelTitle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            novelTitle.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    GrabberUtils.openWebpage(URI.create(libNovel.getNovelUrl()));
                }

            });

            JLabel lastChapter = new JLabel("Last chapter: "+ libNovel.getLastLocalChapterNumber());
            JLabel newestChapter = new JLabel("Newest chapter: "+ libNovel.getNewestChapterNumber());

            JButton changeCLIBtn = new JButton(new ImageIcon(getClass().getResource("/images/settings_icon.png")));
            changeCLIBtn.setBorder(BorderFactory.createEmptyBorder());
            changeCLIBtn.setContentAreaFilled(false);
            changeCLIBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            changeCLIBtn.setToolTipText("Change the CLI command to adjust download parameters");
            changeCLIBtn.addActionListener(actionEvent -> {
                libraryNovelSettings.main(libNovel);
            });

            JButton removeFromFavBtn = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
            removeFromFavBtn.setBorder(BorderFactory.createEmptyBorder());
            removeFromFavBtn.setContentAreaFilled(false);
            removeFromFavBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            removeFromFavBtn.setToolTipText("Remove novel from system.library");
            removeFromFavBtn.addActionListener(actionEvent -> {
                librarySettings.removeStarred(libNovel.getNovelUrl());
                librarySettings.save();
                buildLibrary();
            });

            JButton getLatestChapterBtn = new JButton(new ImageIcon(getClass().getResource("/images/download_icon.png")));
            getLatestChapterBtn.setBorder(BorderFactory.createEmptyBorder());
            getLatestChapterBtn.setContentAreaFilled(false);
            getLatestChapterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            getLatestChapterBtn.setToolTipText("Download chapters from last downloaded to latest");
            getLatestChapterBtn.addActionListener(actionEvent -> {
                int lastDownloadedChapter = Integer.parseInt(lastChapter.getText().substring(lastChapter.getText().lastIndexOf(" ")+1));
                int lastestChapter = Integer.parseInt(newestChapter.getText().substring(newestChapter.getText().lastIndexOf(" ")+1));
                if(lastDownloadedChapter != lastestChapter) {
                    String adjustedCliString = libNovel.getCliString()+" -window checker -chapters "+(lastDownloadedChapter+1)+" last";
                    String[] args = CLI.createArgsFromString(adjustedCliString);
                    CLI.downloadNovel(CLI.createParamsFromArgs(args));

                    libNovel.setLastChapter(lastestChapter);
                    buildLibrary();
                }
            });

            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new GridBagLayout());

            JLabel novelImage;
            String novelCover = LibrarySettings.libraryFolder + "/"+ libNovel.getTitle()+"/"+ libNovel.getCover();
            if(novelCover.isEmpty()) {
                novelImage = new JLabel(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
            } else {
                ImageIcon imageIcon = new ImageIcon(novelCover); // load the image to a imageIcon
                Image image = imageIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(100, 133,  Image.SCALE_SMOOTH); // scale it the smooth way
                imageIcon = new ImageIcon(newimg);  // transform it back
                novelImage = new JLabel(imageIcon);
            }
            novelImage.setBorder(BorderFactory.createEmptyBorder());
            imagePanel.add(novelImage);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            infoPanel.add(novelTitle, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.gridx = 1;
            gbc.gridy = 0;
            infoPanel.add(getLatestChapterBtn, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.gridx = 2;
            gbc.gridy = 0;
            infoPanel.add(changeCLIBtn, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.gridx = 3;
            gbc.gridy = 0;
            infoPanel.add(removeFromFavBtn, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 1;
            infoPanel.add(lastChapter, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 2;
            infoPanel.add(newestChapter, gbc);

            gbc.fill = GridBagConstraints.NONE;
            //gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            gbc.gridy = 0;
            novelPane.add(imagePanel, gbc);

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.insets = new Insets( 0, 10, 0, 0);
            novelPane.add(infoPanel, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = gridCol++ % 2 == 0 ? 0 : 1;
            gbc.gridy = gridRow++ % 2 == 0 ? gridRow : gridRow-1;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weighty = 1;
            gbc.insets = new Insets( 10, 10, 10, 10);
            libraryPanel.add(novelPane, gbc);
            //libraryPanel.add(novelPane);
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

    public void updateMetadataDisplay() {
        autoBusyLabel.setVisible(false);
        NovelMetadata metadata = autoNovel.metadata;
        if(metadata != null) {
            grabChaptersButton.setEnabled(true);
            autoGetNumberButton.setEnabled(true);
            autoEditMetaBtn.setEnabled(true);
            autoEditBlacklistBtn.setEnabled(true);
            pagesCountLbl.setText("");
            pagesCountLbl.setVisible(false);
            pagesLbl.setVisible(false);
            if(librarySettings.isStarred(autoNovel.novelLink)) {
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/starred_icon.png")));
                autoStarredBtn.setEnabled(true);
                autoStarredBtn.setToolTipText("Remove novel from system.library");
            } else {
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/images/unstarred_icon.png")));
                autoStarredBtn.setEnabled(true);
                autoStarredBtn.setToolTipText("Add novel to system.library");
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

        manChapterPreviewBtn = new JButton(new ImageIcon(getClass().getResource("/images/preview_icon.png")));
        manChapterPreviewBtn.setBorder(BorderFactory.createEmptyBorder());
        manChapterPreviewBtn.setContentAreaFilled(false);
        manChapterPreviewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        manLinkList = new JList<>(manLinkListModel);
        manLinkList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        manLinkList.setDropMode(DropMode.INSERT);
        manLinkList.setDragEnabled(true);
        manLinkList.setTransferHandler(new ListItemTransferHandler());
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

        // Settins Tab
        settingsAccountsBtn = new JButton("Accounts", new ImageIcon(getClass().getResource("/images/accounts_icon.png")));
        settingsAccountsBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
        settingsAccountsBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        settingsAccountsBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsAccountsBtn.setContentAreaFilled(false);
        settingsAccountsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

        settingsBrowserComboBox = new JComboBox(browserList);
        if(settings.getBrowser().isEmpty()) {
            String browserSelection = (String)JOptionPane.showInputDialog(this, "Please select your browser:","Browser selection",JOptionPane.PLAIN_MESSAGE, null, browserList,"Crhome");
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

        settingsOutputFormatComboBox = new JComboBox(epubFilenameFormats);
        settingsOutputFormatComboBox.setSelectedIndex(settings.getFilenameFormat());

        accountPasswordField = new JPasswordField();
        for(String accountDomain: loginWebsitesList) {
            accountWebsiteListModel.addElement(accountDomain);
        }
        accountWebsiteList = new JList<>(accountWebsiteListModel);
        accountWebsiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountWebsiteScrollPane = new JScrollPane(accountWebsiteList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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

        // Library
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

    }
}
