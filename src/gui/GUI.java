package gui;

import grabber.*;
import system.Config;
import system.init;
import system.persistent.Accounts;
import system.persistent.EmailConfig;
import system.persistent.Library;
import system.persistent.Settings;
import updater.Updater;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    public static String versionNumber = "2.8.0";
    private static final String[] headerlessBrowserWebsites = {"FicFun", "Dreame", "WuxiaWorld.site","FoxTeller"};
    private static final String[] noHeaderlessBrowserWebsites = {"WattPad", "FanFiction", "FanFiktion"};
    private static final String[] loginWebsites = {"Booklat","Wuxiaworld", "WattPad"};
    private static final String[] settingsMenus = {"Accounts","General", "Library", "Email", "Update"};
    public static List<String> headerlessBrowserWebsitesList = Arrays.asList(headerlessBrowserWebsites);
    public static List<String> noHeaderlessBrowserWebsitesList = Arrays.asList(noHeaderlessBrowserWebsites);
    public static List<String> loginWebsitesList = Arrays.asList(loginWebsites);
    public static DefaultListModel<Chapter> manLinkListModel = new DefaultListModel<>();
    public static DefaultListModel<String> accountWebsiteListModel = new DefaultListModel<>();
    public static DefaultListModel<String> settingsMenuModel = new DefaultListModel<>();
    public static List<String> blacklistedTags = new ArrayList<>();
    public static String[] chapterToChapterArgs = new String[3];
    public static TrayIcon trayIcon;
    public static Integer chapterToChapterNumber = 1;
    private static String[] browserList = {"Chrome", "Firefox", "Edge", "Opera", "IE"};
    private static String[] epubFilenameFormats = {"<author> - <title>", "<title> - <author>", "<title>"};
    private static String[] sslList = {"SMTP","SMTPS","SMTP TLS",};
    private static MenuItem defaultItem0;
    private final String NL = System.getProperty("line.separator");
    public static Novel autoNovel = null;
    public static ManNovel manNovel = null;
    public JComboBox autoHostSelection;
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
    private JButton manRemoveLinksButton;
    private JScrollPane manLinkScrollPane;
    private JTextArea manLogArea;
    private JScrollPane manLogScrollPane;
    private JButton chapterToChapterButton;
    private JButton manSetMetadataButton;
    private JButton updateButton;
    private JTextArea updateTextArea;
    private JScrollPane updateScrollPane;
    private JProgressBar manProgressBar;
    private JLabel updateStatusLbl;
    private JLabel updateLogLbl;
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
    private JPanel newReleaseDescriptionPanel;
    public JLabel pagesCountLbl;
    public JLabel pagesLbl;
    public JCheckBox useHeaderlessBrowserCheckBox;
    public JComboBox autoBrowserCombobox;
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
    private JPanel settingsUpdatePanel;
    private JCheckBox browserGUICheckBox;
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

                NovelOptions options = new NovelOptions();
                options.hostname = autoHostSelection.getSelectedItem().toString();
                options.headless = useHeaderlessBrowserCheckBox.isSelected();
                options.window = "auto";
                options.browser = autoBrowserCombobox.getSelectedItem().toString();
                options.hostname = autoHostSelection.getSelectedItem().toString();
                options.novelLink = chapterListURL.getText();
                options.useAccount = useAccountCheckBox.isSelected();
                options.headlessGUI = browserGUICheckBox.isSelected();
                autoNovel = new Novel(options);
                // Needed

                autoNovel.getChapterList();
                autoNovel.getMetadata();
                if (!autoNovel.chapters.isEmpty()) {
                    grabChaptersButton.setEnabled(true);
                    autoGetNumberButton.setEnabled(true);
                    autoEditMetaBtn.setEnabled(true);
                    autoEditBlacklistBtn.setEnabled(true);
                    pagesCountLbl.setText("");
                    pagesCountLbl.setVisible(false);
                    pagesLbl.setVisible(false);
                    if(Library.isStarred(autoNovel.novelLink)) {
                        autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/files/images/starred_icon.png")));
                        autoStarredBtn.setEnabled(true);
                        autoStarredBtn.setToolTipText("Remove novel from library");
                    } else {
                        autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/files/images/unstarred_icon.png")));
                        autoStarredBtn.setEnabled(true);
                        autoStarredBtn.setToolTipText("Add novel to library");
                    }
                }
                autoBusyLabel.setVisible(false);
            }
        }));

        // Start Auto-Novel chapter download and EPUB conversion
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
                    && ((Integer) lastChapter.getValue() > autoNovel.chapters.size())) {
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
                // Needed
                autoNovel.options.saveLocation = autoSaveLocation.getText();
                //Optional
                autoNovel.options.waitTime =  Integer.parseInt(waitTime.getText());
                autoNovel.options.displayChapterTitle = displayChapterTitleCheckBox.isSelected();
                autoNovel.options.invertOrder = checkInvertOrder.isSelected();
                // Was set on "checking" but needs to be set again for potential changes
                autoNovel.options.headless = useHeaderlessBrowserCheckBox.isSelected();
                autoNovel.options.browser = autoBrowserCombobox.getSelectedItem().toString();
                autoNovel.options.useAccount = useAccountCheckBox.isSelected();
                autoNovel.options.headlessGUI = browserGUICheckBox.isSelected();
                // Set chapter range
                if(chapterAllCheckBox.isSelected()) {
                    autoNovel.options.firstChapter = 1;
                    autoNovel.options.lastChapter = autoNovel.chapters.size();
                } else {
                    autoNovel.options.firstChapter = (int) firstChapter.getValue();
                    if(toLastChapter.isSelected()) {
                        autoNovel.options.lastChapter = autoNovel.chapters.size();
                    } else {
                        autoNovel.options.lastChapter = (int) lastChapter.getValue();
                    }
                }
                autoNovel.downloadChapters(); // Throws exception if grabbing was stopped
                EPUB epub = new EPUB(autoNovel);
                epub.createCoverPage();
                epub.createToc();
                epub.createDescPage();
                epub.writeEpub();
                autoNovel.report();
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

        autoEditBlacklistBtn.addActionListener(e -> autoSetBlacklistedTags.main(autoNovel));

        autoEditMetaBtn.addActionListener(e -> autoEditMetadata.main(autoNovel));


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

        autoVisitButton.addActionListener(arg0 -> {
            try {
                String toOpenHostSite;
                HostSettings emptyNovel = new HostSettings(
                        Objects.requireNonNull(autoHostSelection.getSelectedItem()).toString());
                toOpenHostSite = emptyNovel.url;
                URI uri = new URI(toOpenHostSite);
                openWebpage(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        autoShowBlacklistedTagsBtn.addActionListener(arg0 -> {
            DefaultListModel<String> tempListModel = new DefaultListModel<>();
            JList<String> tempJList = new JList<>(tempListModel);

            HostSettings tempSettings = new HostSettings(autoHostSelection.getSelectedItem().toString());
            JScrollPane tagScrollPane = new JScrollPane(tempJList);
            tagScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            tagScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tagScrollPane.setBounds(0, 0, 532, 180);
            if (!(tempSettings.blacklistedTags == null)) {
                for (String alreadyBlacklistedTags : tempSettings.blacklistedTags) {
                    tempListModel.addElement(alreadyBlacklistedTags);
                }
            }
            JOptionPane.showOptionDialog(null,
                    tagScrollPane,
                    "Blacklisted Tags:",
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.PLAIN_MESSAGE,
                    null, null, null);
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

        autoHostSelection.addItemListener(e -> {
            String selection = autoHostSelection.getSelectedItem().toString();
            if (headerlessBrowserWebsitesList.contains(selection)) {
                useHeaderlessBrowserCheckBox.setSelected(true);
                useHeaderlessBrowserCheckBox.setEnabled(false);
            } else {
                useHeaderlessBrowserCheckBox.setEnabled(true);
            }
            chapterAllCheckBox.setEnabled(true);
            firstChapter.setEnabled(true);
            lastChapter.setEnabled(true);
            toLastChapter.setEnabled(true);
            checkInvertOrder.setEnabled(true);
            autoChapterToChapterNumberField.setVisible(false);
            autoFirstChapterLbl.setVisible(false);
            autoFirstChapterURL.setVisible(false);
            autoLastChapterLbl.setVisible(false);
            autoLastChapterURL.setVisible(false);
            if (noHeaderlessBrowserWebsitesList.contains(selection)) {
                useHeaderlessBrowserCheckBox.setSelected(false);
                useHeaderlessBrowserCheckBox.setEnabled(false);
            }
        });


        // manual chapter download
        getLinksButton.addActionListener(e -> {
            if (manNovelURL.getText().isEmpty()) {
                JOptionPane.showMessageDialog(window, "URL field is empty.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                manNovelURL.requestFocusInWindow();
            }
            if (!manNovelURL.getText().isEmpty()) {
                try {
                    manNovel = new ManNovel(this);
                    manNovel.novelLink = manNovelURL.getText();
                    manNovel.options.headless = manUseHeaderlessBrowser.isSelected();
                    manNovel.options.window = "manual";
                    manNovel.options.browser = manBrowserCombobox.getSelectedItem().toString();
                    manNovel.retrieveLinks();
                } catch (NullPointerException | IllegalArgumentException | IOException err) {
                    err.printStackTrace();
                    appendText("manual", "[ERROR]" + err.getMessage());
                } finally {
                    if (!manLinkListModel.isEmpty()) {
                        manRemoveLinksButton.setEnabled(true);
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
                } else if (manChapterContainer.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window, "Chapter container selector is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterContainer.requestFocusInWindow();
                } else if (manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time cannot be empty.", "warning");
                } else if (!manWaitTime.getText().matches("\\d+") && !manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time must contain numbers.", "warning");
                } else if ((!manSaveLocation.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty())
                        && (!manWaitTime.getText().isEmpty())) {
                    manGrabChaptersButton.setEnabled(false);
                    manGrabChaptersButton.setVisible(false);
                    manStopButton.setEnabled(true);
                    manStopButton.setVisible(true);
                    manProgressBar.setStringPainted(true);
                    try {
                        // Needed
                        manNovel = new ManNovel(this);
                        manNovel.options.saveLocation = manSaveLocation.getText();
                        manNovel.host.chapterContainer = manChapterContainer.getText();
                        manNovel.host.blacklistedTags = GUI.blacklistedTags;
                        manNovel.options.window = "manual";
                        //Optional
                        manNovel.options.waitTime =  Integer.parseInt(manWaitTime.getText());
                        manNovel.options.displayChapterTitle = manDispalyChapterTitleCheckbox.isSelected();
                        manNovel.options.invertOrder = manInvertOrder.isSelected();
                        manNovel.options.headless = manUseHeaderlessBrowser.isSelected();
                        manNovel.options.browser = manBrowserCombobox.getSelectedItem().toString();

                        manNovel.manGetMetadata();
                        manNovel.processChaptersToChapters(chapterToChapterArgs);

                        EPUB epub = new EPUB(manNovel);
                        epub.createCoverPage();
                        epub.createToc();
                        epub.createDescPage();
                        epub.writeEpub();

                        manNovel.report();
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
                } else if (manChapterContainer.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window, "Chapter container selector is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterContainer.requestFocusInWindow();
                } else if (manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time cannot be empty.", "warning");
                } else if (!manWaitTime.getText().matches("\\d+") && !manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time must contain numbers.", "warning");
                } else if ((!manSaveLocation.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty())
                        && (!manWaitTime.getText().isEmpty())
                ) {
                    manGrabChaptersButton.setEnabled(false);
                    manGrabChaptersButton.setVisible(false);
                    manStopButton.setEnabled(true);
                    manStopButton.setVisible(true);
                    manProgressBar.setStringPainted(true);
                    try {
                        // Needed
                        if(!chaptersFromLinksRadioButton.isSelected()) manNovel = new ManNovel(this);
                        manNovel.options.saveLocation = manSaveLocation.getText();
                        manNovel.host.chapterContainer = manChapterContainer.getText();
                        manNovel.host.blacklistedTags = GUI.blacklistedTags;
                        manNovel.options.window = "manual";
                        //Optional
                        manNovel.options.waitTime =  Integer.parseInt(manWaitTime.getText());
                        manNovel.options.displayChapterTitle = manDispalyChapterTitleCheckbox.isSelected();
                        manNovel.options.invertOrder = manInvertOrder.isSelected();
                        manNovel.options.headless = manUseHeaderlessBrowser.isSelected();
                        manNovel.options.browser = manBrowserCombobox.getSelectedItem().toString();

                        // new Novel was created when retrieving links
                        manNovel.manGetMetadata();
                        manNovel.processChaptersFromList();

                        EPUB epub = new EPUB(manNovel);
                        epub.createCoverPage();
                        epub.createToc();
                        epub.createDescPage();
                        epub.writeEpub();

                        manNovel.report();
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
                manNovelURL.setVisible(true);
                getLinksButton.setVisible(true);
                manTocURLlbl.setVisible(true);
                chapterToChapterButton.setVisible(false);
                manInvertOrder.setEnabled(true);
            }
        });

        chapterToChapterRadioButton.addActionListener(e -> {
            if (chapterToChapterRadioButton.isSelected()) {
                chaptersFromLinksRadioButton.setSelected(false);
                manNovelURL.setVisible(false);
                getLinksButton.setVisible(false);
                manTocURLlbl.setVisible(false);
                chapterToChapterButton.setVisible(true);
                manInvertOrder.setEnabled(false);
            }
        });

        manRemoveLinksButton.addActionListener(arg0 -> {
            if (!manLinkListModel.isEmpty()) {
                int[] indices = manLinkList.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    manLinkListModel.removeElementAt(indices[i]);
                }
                if (manLinkListModel.isEmpty()) {
                    manRemoveLinksButton.setEnabled(false);
                }
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
        manSetMetadataButton.addActionListener(arg0 -> manSetMetadata.main());

        manBlackListedTags.addActionListener(e -> manSetBlacklistedTags.main());

        chapterToChapterButton.addActionListener(e -> manChapterToChapter.main());


        manStopButton.addActionListener(e -> {
            manStopButton.setEnabled(false);
            manNovel.killTask = true;
        });

        manJsoupInfoButton.addActionListener(e -> {
            try {
                openWebpage(new URI("https://jsoup.org/cookbook/extracting-data/selector-syntax"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        });

        manAddChapterButton.addActionListener(actionEvent -> {
            editChapterList.main("manual");
            if (!manLinkListModel.isEmpty()) {
                manRemoveLinksButton.setEnabled(true);
            }
        });

        updateButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            updaterStatus.setVisible(true);
            updateButton.setVisible(false);
            checkForUpdatesButton.setVisible(false);
            updaterStatus.setText("Downloading...");
            Updater.updateJar();
        }));

        checkForUpdatesButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(this::checkForNewReleases));

        accountWebsiteList.addListSelectionListener(listSelectionEvent -> {
            accountUsernameField.setText(Accounts.getUsername(accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex())));
            accountPasswordField.setText(Accounts.getPassword(accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex())));
            accountAddBtn.setVisible(true);
            if(!accountUsernameField.getText().isEmpty()) {
                accountAddBtn.setText("Edit");
            } else {
                accountAddBtn.setText("Add");
            }
        });
        // Add or Edit an account entry
        accountAddBtn.addActionListener(actionEvent -> Accounts.setAccount(
                accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex()),
                accountUsernameField.getText(),
                accountPasswordField.getText()
        ));
        settingsMenuList.addListSelectionListener(listSelectionEvent -> {
            String selectedMenu = settingsMenuModel.get(settingsMenuList.getSelectedIndex());
            settingsAccountsPanel.setVisible(false);
            settingsHeadlessPanel.setVisible(false);
            settingsUpdatePanel.setVisible(false);
            settingsGeneralPanel.setVisible(false);
            settingsEmailPanel.setVisible(false);
            settingsLibraryPanel.setVisible(false);
            if(selectedMenu.equals("Accounts")) settingsAccountsPanel.setVisible(true);
            if(selectedMenu.equals("Update")) settingsUpdatePanel.setVisible(true);
            if(selectedMenu.equals("Library")) settingsLibraryPanel.setVisible(true);
            if(selectedMenu.equals("Email")) settingsEmailPanel.setVisible(true);
            if(selectedMenu.equals("General")) {
                settingsGeneralPanel.setVisible(true);
                settingsHeadlessPanel.setVisible(true);
            }
        });
        browserGUICheckBox.addActionListener(actionEvent -> {
            Settings.setHeadlessSettings(autoBrowserCombobox.getSelectedItem().toString(), browserGUICheckBox.isSelected());

        });
        autoBrowserCombobox.addActionListener(actionEvent -> {
            Settings.setHeadlessSettings(autoBrowserCombobox.getSelectedItem().toString(), browserGUICheckBox.isSelected());

        });
        settingsAlwaysGetImagesCheckBox.addActionListener(actionEvent -> {
            Settings.setGeneralSettings(
                    settingsAlwaysGetImagesCheckBox.isSelected(),
                    settingsAlwaysRemoveStylingCheckBox.isSelected(),
                    settingsOutputFormatComboBox.getSelectedIndex(),
                    settingsSavelocationField.getText(),
                    standardSaveLocationCheckBox.isSelected()
            );
        });
        settingsAlwaysRemoveStylingCheckBox.addActionListener(actionEvent -> {
            Settings.setGeneralSettings(
                    settingsAlwaysGetImagesCheckBox.isSelected(),
                    settingsAlwaysRemoveStylingCheckBox.isSelected(),
                    settingsOutputFormatComboBox.getSelectedIndex(),
                    settingsSavelocationField.getText(),
                    standardSaveLocationCheckBox.isSelected()
                    );
        });
        settingsOutputFormatComboBox.addActionListener(actionEvent -> {
            Settings.setGeneralSettings(
                    settingsAlwaysGetImagesCheckBox.isSelected(),
                    settingsAlwaysRemoveStylingCheckBox.isSelected(),
                    settingsOutputFormatComboBox.getSelectedIndex(),
                    settingsSavelocationField.getText(),
                    standardSaveLocationCheckBox.isSelected()
            );
        });
        standardSaveLocationCheckBox.addActionListener(actionEvent -> {
            Settings.setGeneralSettings(
                    settingsAlwaysGetImagesCheckBox.isSelected(),
                    settingsAlwaysRemoveStylingCheckBox.isSelected(),
                    settingsOutputFormatComboBox.getSelectedIndex(),
                    settingsSavelocationField.getText(),
                    standardSaveLocationCheckBox.isSelected()
            );

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
        autoStarredBtn.addActionListener(actionEvent -> {
            if(Library.isStarred(autoNovel.novelLink)) {
                Library.removeStarred(autoNovel.novelLink);
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/files/images/unstarred_icon.png")));
            } else {
                Library.setStarred(autoNovel);
                autoStarredBtn.setIcon(new ImageIcon(getClass().getResource("/files/images/starred_icon.png")));
            }
        });
        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 2) {
                buildLibrary();
            }
        });
        emailSaveBtn.addActionListener(actionEvent -> {
            EmailConfig.saveEmailSettings(
                    emailHostField.getText(),
                    emailUserField.getText(),
                    emailPasswordField.getText(),
                    emailReceiver.getText(),
                    Integer.parseInt(emailPortField.getText()),
                    emailSLLComboBox.getSelectedIndex()
            );
        });
        sendNewChapterNotificationsCheckBox.addActionListener(actionEvent -> {
            Library.setNotifications(sendNewChapterNotificationsCheckBox.isSelected());
            sendEPUBAsAttachmentCheckBox.setSelected(false);
            Library.setUseAttachment(sendEPUBAsAttachmentCheckBox.isSelected());

        });
        sendEPUBAsAttachmentCheckBox.addActionListener(actionEvent -> {
            Library.setUseAttachment(sendEPUBAsAttachmentCheckBox.isSelected());
            sendNewChapterNotificationsCheckBox.setSelected(false);
            Library.setNotifications(sendNewChapterNotificationsCheckBox.isSelected());
            updateLastChapterNumberCheckBox.setSelected(false);
            Library.setUpdateLast(updateLastChapterNumberCheckBox.isSelected());

        });
        enableCheckingCheckBox.addActionListener(actionEvent -> {
            Library.setPolling(enableCheckingCheckBox.isSelected());
            if(!enableCheckingCheckBox.isSelected()) {
                libraryFrequencySpinner.setEnabled(false);
            } else {
                libraryFrequencySpinner.setEnabled(true);
            }
        });
        updateLastChapterNumberCheckBox.addActionListener(actionEvent -> {
            Library.setUpdateLast(updateLastChapterNumberCheckBox.isSelected());
        });
    }


    public void buildLibrary() {
        libraryPanel.setVisible(false);
        libraryPanel.removeAll();
        libraryPanel.setVisible(true);
        int gridRow = 0;
        int gridCol = 0;
        for(String novelUrl: Library.getLibrary()) {
            JPanel novelPane = new JPanel();
            novelPane.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            JPanel infoPanel = new JPanel();

            infoPanel.setLayout(new GridBagLayout());
            JLabel novelTitle = new JLabel(Library.getNovelTitle(novelUrl));
            novelTitle.setFont(new Font("Calibri", Font.BOLD, 17));

            JLabel thresholdLbl = new JLabel("Threshold:");
            JSpinner thresholdSpinner = new JSpinner();
            thresholdSpinner.setValue(Library.getThreshold(novelUrl));
            JComponent comp = thresholdSpinner.getEditor();
            JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
            field.setColumns(4);
            DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
            formatter.setCommitsOnValidEdit(true);
            thresholdSpinner.addChangeListener(e -> Library.setThreshold(novelUrl, (Integer) thresholdSpinner.getValue()));

            JCheckBox autoGrabbingCheckbox = new JCheckBox("Grab new chapters");
            autoGrabbingCheckbox.setSelected(Library.getAutoDownload(novelUrl));
            autoGrabbingCheckbox.setToolTipText("Automatically download chapters when threshold of new releases is met");
            autoGrabbingCheckbox.addActionListener(actionEvent -> {
                Library.setAutoDownload(novelUrl, autoGrabbingCheckbox.isSelected());
            });
            JLabel lastChapter = new JLabel("Last chapter: "+ Library.getLastChapter(novelUrl));
            JLabel newestChapter = new JLabel("Newest chapter: "+ Library.getNewestChapter(novelUrl));

            JButton changeCLIBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/settings_icon.png")));
            changeCLIBtn.setBorder(BorderFactory.createEmptyBorder());
            changeCLIBtn.setContentAreaFilled(false);
            changeCLIBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            changeCLIBtn.setToolTipText("Change the CLI command to adjust download parameters");
            changeCLIBtn.addActionListener(actionEvent -> {
                String cli = JOptionPane.showInputDialog("Change cli command:",Library.getCLICommand(novelUrl));
                if(cli != null) {
                    Library.setCLICommand(novelUrl, cli);
                }
            });

            JButton removeFromFavBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/remove_icon.png")));
            removeFromFavBtn.setBorder(BorderFactory.createEmptyBorder());
            removeFromFavBtn.setContentAreaFilled(false);
            removeFromFavBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            removeFromFavBtn.setToolTipText("Remove novel from library");
            removeFromFavBtn.addActionListener(actionEvent -> {
                Library.removeStarred(novelUrl);
                buildLibrary();
            });

            JButton getLatestChapterBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/download_icon.png")));
            getLatestChapterBtn.setBorder(BorderFactory.createEmptyBorder());
            getLatestChapterBtn.setContentAreaFilled(false);
            getLatestChapterBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            getLatestChapterBtn.setToolTipText("Download chapters from last downloaded to latest");
            getLatestChapterBtn.addActionListener(actionEvent -> {
                int lastDownloadedChapter = Integer.parseInt(lastChapter.getText().substring(lastChapter.getText().lastIndexOf(" ")+1));
                int lastestChapter = Integer.parseInt(newestChapter.getText().substring(newestChapter.getText().lastIndexOf(" ")+1));
                if(lastDownloadedChapter != lastestChapter) {
                    String cliString = Library.getCLICommand(novelUrl)+" -window checker -chapters "+(lastDownloadedChapter+1)+" "+lastestChapter;
                    String[] cliParams = cliString.split(" ");
                    init.processParams(init.getParamsFromString(cliParams));
                    Library.setLastChapter(novelUrl, lastestChapter);
                    buildLibrary();
                }
            });

            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new GridBagLayout());

            JLabel novelImage;
            String novelCover = (Config.home_path+ "/" + Config.home_folder +
                    "/"+ Library.getNovelTitle(novelUrl)+"/").replaceAll(" ","-")
                    + Library.getBookCover(novelUrl);
            if(novelCover.isEmpty()) {
                novelImage = new JLabel(new ImageIcon(getClass().getResource("/files/images/cover_placeholder.png")));
            } else {
                ImageIcon imageIcon = new ImageIcon(novelCover); // load the image to a imageIcon
                Image image = imageIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(100, 133,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
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

            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 3;
            infoPanel.add(autoGrabbingCheckbox, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            gbc.gridx = 0;
            gbc.gridy = 4;
            infoPanel.add(thresholdLbl, gbc);

            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.SOUTHWEST;
            gbc.gridx = 1;
            gbc.gridy = 4;
            infoPanel.add(thresholdSpinner, gbc);

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
        setTitle("Novel-Grabber " + versionNumber);
        ImageIcon favicon = new ImageIcon(getClass().getResource("/files/images/favicon.png"));
        setIconImage(favicon.getImage());
        setMinimumSize(new Dimension(1000, 700));
        Tray();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (!SystemTray.isSupported()) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    private static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        });
    }



    private void Tray() {
        if (!SystemTray.isSupported()) {
            showPopup("SystemTray is not supported. Exiting...", "Error");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/files/images/favicon.png"));

        ActionListener exitListener = e -> System.exit(0);
        ActionListener openWindow = e -> setVisible(true);

        PopupMenu popup = new PopupMenu();
        MenuItem topLable = new MenuItem("Novel-Grabber");
        popup.add(topLable);
        MenuItem aboutLabel = new MenuItem("About");
        aboutLabel.addActionListener(arg01 -> {
            InputStream in = getClass().getResourceAsStream("/files/about.txt");
            String text;
            try (java.util.Scanner s = new java.util.Scanner(in)) {
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

        trayIcon = new TrayIcon(image, "Novel-Grabber", popup);
        trayIcon.setToolTip("Novel-Grabber");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
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
            case "checker":
                System.out.println(logMsg);
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

    public static String[] getWebsites() {
        List<String> websitesList = new ArrayList<>();
        for (Object key: Config.siteSelectorsJSON.keySet()){
            websitesList.add(key.toString());
        }
        websitesList.remove("no_domain");
        Collections.sort(websitesList);
        String[] websites = new String[websitesList.size()];
        websitesList.toArray(websites);
        return websites;
    }

    private void checkForNewReleases() {
        updaterStatus.setVisible(true);
        updaterStatus.setText("Checking for new releases...");
        checkForUpdatesButton.setVisible(false);
        try {
            Document doc = Jsoup.connect("https://github.com/Flameish/Novel-Grabber/releases").get();
            Element versionString = doc.select("a[title]").first();
            String oldVersionString = versionNumber;
            String newVersionString = versionString.attr("title");
            if (Updater.compareStrings(oldVersionString, newVersionString) == -1) {
                updateTextArea.setText("");
                updateStatusLbl.setText("A new update of Novel-Grabber was released. The latest version is: " + newVersionString);
                setTitle("Novel-Grabber " + versionNumber + " - New version released");
                updateLogLbl.setText("Changes in " + newVersionString + ":");
                Element releaseDesc = doc.select(".markdown-body").first();
                Elements descLines = releaseDesc.select("li");
                for (Element s : descLines) {
                    appendText("update", s.text());
                }
                updateButton.setVisible(true);
                updateStatusLbl.setVisible(true);
                newReleaseDescriptionPanel.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updaterStatus.setVisible(false);
        checkForUpdatesButton.setVisible(true);
    }

    public void setBufferedCover(BufferedImage bufferedImage) {
        if (bufferedImage == null)
            coverImage.setIcon(new ImageIcon(getClass().getResource("/files/images/cover_placeholder.png")));
        else
            coverImage.setIcon(new ImageIcon(new ImageIcon(bufferedImage).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
    }

    private void createUIComponents() {
        // Automatic Tab
        autoHostSelection = new JComboBox(getWebsites());

        autoBrowserCombobox = new JComboBox(browserList);
        autoBrowserCombobox.setSelectedItem(Settings.getBrowser());

        autoGetImages = new JCheckBox();
        autoGetImages.setSelected(Settings.getImages());

        autoRemoveStyling = new JCheckBox();
        autoRemoveStyling.setSelected(Settings.getRemoveStyling());

        autoSaveLocation = new JTextField();

        autoChapterToChapterNumberField = new JTextField("Number");
        autoChapterToChapterNumberField.setForeground(Color.GRAY);

        autoShowBlacklistedTagsBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/block.png")));
        autoShowBlacklistedTagsBtn.setBorder(BorderFactory.createEmptyBorder());
        autoShowBlacklistedTagsBtn.setContentAreaFilled(false);

        autoStarredBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/unstarred_icon.png")));
        autoStarredBtn.setBorder(BorderFactory.createEmptyBorder());
        autoStarredBtn.setContentAreaFilled(false);

        autoCheckAvailability = new JButton(new ImageIcon(getClass().getResource("/files/images/check_icon.png")));
        autoCheckAvailability.setBorder(BorderFactory.createEmptyBorder());
        autoCheckAvailability.setContentAreaFilled(false);

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

        autoVisitButton = new JButton(new ImageIcon(getClass().getResource("/files/images/website_icon.png")));
        autoVisitButton.setBorder(BorderFactory.createEmptyBorder());
        autoVisitButton.setContentAreaFilled(false);

        autoBusyLabel = new JLabel(new ImageIcon(getClass().getResource("/files/images/busy.gif")));

        browseButton = new JButton(new ImageIcon(getClass().getResource("/files/images/folder_icon.png")));
        browseButton.setBorder(BorderFactory.createEmptyBorder());
        browseButton.setContentAreaFilled(false);

        coverImage = new JLabel(new ImageIcon(getClass().getResource("/files/images/cover_placeholder.png")));
        coverImage.setBorder(BorderFactory.createEmptyBorder());

        autoEditMetadataButton = new JButton(new ImageIcon(getClass().getResource("/files/images/settings_icon.png")));
        autoEditMetadataButton.setBorder(BorderFactory.createEmptyBorder());
        autoEditMetadataButton.setContentAreaFilled(false);

        autoEditMetaBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/edit.png")));
        autoEditMetaBtn.setBorder(BorderFactory.createEmptyBorder());
        autoEditMetaBtn.setContentAreaFilled(false);

        autoEditBlacklistBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/block.png")));
        autoEditBlacklistBtn.setBorder(BorderFactory.createEmptyBorder());
        autoEditBlacklistBtn.setContentAreaFilled(false);

        autoGetNumberButton = new JButton(new ImageIcon(getClass().getResource("/files/images/list_icon.png")));
        autoGetNumberButton.setBorder(BorderFactory.createEmptyBorder());
        autoGetNumberButton.setContentAreaFilled(false);

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
        manBrowserCombobox = new JComboBox(browserList);

        manSetMetadataButton = new JButton(new ImageIcon(getClass().getResource("/files/images/edit.png")));
        manSetMetadataButton.setBorder(BorderFactory.createEmptyBorder());
        manSetMetadataButton.setContentAreaFilled(false);

        manBlackListedTags = new JButton(new ImageIcon(getClass().getResource("/files/images/block.png")));
        manBlackListedTags.setBorder(BorderFactory.createEmptyBorder());
        manBlackListedTags.setContentAreaFilled(false);

        manAddChapterButton = new JButton(new ImageIcon(getClass().getResource("/files/images/add_icon.png")));
        manAddChapterButton.setBorder(BorderFactory.createEmptyBorder());
        manAddChapterButton.setContentAreaFilled(false);

        manRemoveLinksButton = new JButton(new ImageIcon(getClass().getResource("/files/images/remove_icon.png")));
        manRemoveLinksButton.setBorder(BorderFactory.createEmptyBorder());
        manRemoveLinksButton.setContentAreaFilled(false);

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

        manJsoupInfoButton = new JButton(new ImageIcon(getClass().getResource("/files/images/info_icon.png")));
        manJsoupInfoButton.setBorder(BorderFactory.createEmptyBorder());
        manJsoupInfoButton.setContentAreaFilled(false);

        manBrowseLocationButton = new JButton(new ImageIcon(getClass().getResource("/files/images/folder_icon.png")));
        manBrowseLocationButton.setBorder(BorderFactory.createEmptyBorder());
        manBrowseLocationButton.setContentAreaFilled(false);

        manWaitTime = new JTextField("0");
        manWaitTime.setHorizontalAlignment(SwingConstants.CENTER);

        // Settins Tab
        for(String settingsMenu: settingsMenus) {
            settingsMenuModel.addElement(settingsMenu);
        }
        settingsMenuList = new JList<>(settingsMenuModel);
        settingsMenuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        settingsMenuScrollPane = new JScrollPane(settingsMenuList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        browserGUICheckBox = new JCheckBox();
        browserGUICheckBox.setSelected(Settings.getBrowserShowGUI());

        settingsAlwaysGetImagesCheckBox = new JCheckBox();
        settingsAlwaysGetImagesCheckBox.setSelected(Settings.getImages());

        settingsAlwaysRemoveStylingCheckBox = new JCheckBox();
        settingsAlwaysRemoveStylingCheckBox.setSelected(Settings.getRemoveStyling());

        standardSaveLocationCheckBox = new JCheckBox();
        standardSaveLocationCheckBox.setSelected(Settings.getUseStandardLocation());

        if(Settings.getUseStandardLocation()) {
            autoSaveLocation.setText(Settings.getSavelocation());
        }

        settingsSavelocationField = new JTextField();
        settingsSavelocationField.setText(Settings.getSavelocation());

        settingsBrowseSaveLocationBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/folder_icon.png")));
        settingsBrowseSaveLocationBtn.setBorder(BorderFactory.createEmptyBorder());
        settingsBrowseSaveLocationBtn.setContentAreaFilled(false);

        settingsOutputFormatComboBox = new JComboBox(epubFilenameFormats);
        settingsOutputFormatComboBox.setSelectedIndex(Settings.getEPUBOutputFormat());

        accountPasswordField = new JPasswordField();
        for(String accountDomain: loginWebsitesList) {
            accountWebsiteListModel.addElement(accountDomain);
        }
        accountWebsiteList = new JList<>(accountWebsiteListModel);
        accountWebsiteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountWebsiteScrollPane = new JScrollPane(accountWebsiteList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Email settings
        emailHostField = new JTextField(EmailConfig.getHost());
        emailUserField = new JTextField(EmailConfig.getUsername());
        emailReceiver = new JTextField(EmailConfig.getReceiverEmail());
        emailPasswordField = new JPasswordField(EmailConfig.getPassword());
        emailPortField = new JTextField(String.valueOf(EmailConfig.getPort()));
        emailSLLComboBox = new JComboBox(sslList);
        emailSLLComboBox.setSelectedIndex(EmailConfig.getSSL());
        sendNewChapterNotificationsCheckBox = new JCheckBox();
        sendNewChapterNotificationsCheckBox.setSelected(Library.useNotifications());
        sendEPUBAsAttachmentCheckBox = new JCheckBox();
        sendEPUBAsAttachmentCheckBox.setSelected(Library.useAttachment());

        // Library
        enableCheckingCheckBox = new JCheckBox();
        enableCheckingCheckBox.setSelected(Library.getPolling());

        libraryFrequencySpinner = new JSpinner();
        libraryFrequencySpinner.setValue(Library.getFrequency());
        libraryFrequencySpinner = new JSpinner();
        libraryFrequencySpinner.setValue(Library.getFrequency());
        JComponent comp = libraryFrequencySpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        field.setColumns(4);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        libraryFrequencySpinner.addChangeListener(e -> Library.setFrequency((Integer) libraryFrequencySpinner.getValue()));


        updateLastChapterNumberCheckBox = new JCheckBox();
        updateLastChapterNumberCheckBox.setSelected(Library.getUpdateLast());
        // Update Tab
        updateTextArea = new JTextArea();
        updateTextArea.setLineWrap(true);
        updateTextArea.setWrapStyleWord(true);
        updateScrollPane = new JScrollPane(updateTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
