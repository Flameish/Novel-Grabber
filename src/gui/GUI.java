package gui;

import grabber.*;
import updater.Updater;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    public static String versionNumber = "2.7.1";
    public static DefaultListModel<Chapter> manLinkListModel = new DefaultListModel<>();
    public static DefaultListModel<String> accountWebsiteListModel = new DefaultListModel<>();
    public static List<String> blacklistedTags = new ArrayList<>();
    public static String[] chapterToChapterArgs = new String[3];
    public static TrayIcon trayIcon;
    public static Integer chapterToChapterNumber = 1;
    private static String[] browserList = {"Chrome", "Firefox", "Edge", "Opera", "IE"};
    private static MenuItem defaultItem0;
    private final String NL = System.getProperty("line.separator");
    public static Novel autoNovel = null;
    public static ManNovel manNovel = null;
    public JComboBox autoHostSelection;
    public JTextField chapterListURL;
    public JTextField saveLocation;
    public JCheckBox chapterAllCheckBox;
    public JSpinner firstChapter;
    public JSpinner lastChapter;
    public JCheckBox toLastChapter;
    public JCheckBox getImages;
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
    private JPanel updateTab;
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
    public JCheckBox autoNoStyling;
    public JCheckBox manNoStyling;
    private JButton manAddChapterButton;
    private JList accountWebsiteList;
    private JTextField accountUsernameField;
    private JTextField accountPasswordField;
    private JButton accountAddBtn;
    private JScrollPane accountWebsiteScrollPane;
    public JCheckBox useAccountCheckBox;
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
            if (saveLocation.getText().isEmpty()) {
                showPopup("Save directory field is empty.", "warning");
                saveLocation.requestFocusInWindow();
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
                autoNovel.options.saveLocation = saveLocation.getText();
                //Optional
                autoNovel.options.waitTime =  Integer.parseInt(waitTime.getText());
                autoNovel.options.displayChapterTitle = displayChapterTitleCheckBox.isSelected();
                autoNovel.options.invertOrder = checkInvertOrder.isSelected();
                // Was set on "checking" but needs to be set again for potential changes
                autoNovel.options.headless = useHeaderlessBrowserCheckBox.isSelected();
                autoNovel.options.browser = autoBrowserCombobox.getSelectedItem().toString();
                autoNovel.options.useAccount = useAccountCheckBox.isSelected();
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
                autoNovel.createCoverPage();
                autoNovel.createToc();
                autoNovel.createDescPage();
                autoNovel.createEPUB();
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
                saveLocation.setText(chooser.getSelectedFile().toString());
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
            if (HostSettings.headerlessBrowserWebsitesList.contains(selection)) {
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
            if (HostSettings.noHeaderlessBrowserWebsitesList.contains(selection)) {
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

                        manNovel.createCoverPage();
                        manNovel.createToc();
                        manNovel.createDescPage();
                        manNovel.createEPUB();
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

                        manNovel.createCoverPage();
                        manNovel.createToc();
                        manNovel.createDescPage();
                        manNovel.createEPUB();
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
        accountAddBtn.addActionListener(actionEvent -> Accounts.addAccount(
                accountWebsiteListModel.get(accountWebsiteList.getSelectedIndex()),
                accountUsernameField.getText(),
                accountPasswordField.getText()
        ));
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
                checkerLogArea.append(logMsg + NL);
                checkerLogArea.setCaretPosition(checkerLogArea.getText().length());
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

        autoChapterToChapterNumberField = new JTextField("Number");
        autoChapterToChapterNumberField.setForeground(Color.GRAY);

        autoShowBlacklistedTagsBtn = new JButton(new ImageIcon(getClass().getResource("/files/images/block.png")));
        autoShowBlacklistedTagsBtn.setBorder(BorderFactory.createEmptyBorder());
        autoShowBlacklistedTagsBtn.setContentAreaFilled(false);

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

        // Account Tab
        accountPasswordField = new JPasswordField();
        for(String accountDomain: HostSettings.loginWebsitesList) {
            accountWebsiteListModel.addElement(accountDomain);
        }
        accountWebsiteList = new JList<>(accountWebsiteListModel);
        manLinkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountWebsiteScrollPane = new JScrollPane(accountWebsiteList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Update Tab
        updateTextArea = new JTextArea();
        updateTextArea.setLineWrap(true);
        updateTextArea.setWrapStyleWord(true);
        updateScrollPane = new JScrollPane(updateTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
