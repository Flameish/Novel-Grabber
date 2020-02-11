package gui;

import checker.chapterChecker;
import grabber.AutoNovel;
import grabber.HostSettings;
import grabber.ManNovel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import updater.updater;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class GUI extends JFrame {
    public static String versionNumber = "2.3.3";
    public static String appdataPath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Novel-Grabber";
    public static DefaultListModel<String> listModelChapterLinks = new DefaultListModel<>();
    public static DefaultListModel<String> listModelCheckerLinks = new DefaultListModel<>();
    public static List<String> blacklistedTags = new ArrayList<>();
    public static String[] chapterToChapterArgs = new String[3];
    public static TrayIcon trayIcon;
    public static Integer chapterToChapterNumber = 1;
    private static String[] exportFormats = {"EPUB", "Calibre"};
    private static String[] browserList = {"Chrome", "Firefox", "Edge", "Opera", "IE"};
    private static MenuItem defaultItem0;
    private final String NL = System.getProperty("line.separator");
    public static AutoNovel autoNovel = null;
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
    public JComboBox exportSelection;
    public JTextField waitTime;
    public JProgressBar progressBar;
    public JButton autoGetNumberButton;
    public JTextField manNovelURL;
    public JLabel autoBookTitle;
    public JLabel autoAuthor;
    public JLabel autoChapterAmount;
    public JComboBox manExportSelection;
    public JTextField manWaitTime;
    public JCheckBox manGetImages;
    public JCheckBox manInvertOrder;
    public JTextField manSaveLocation;
    public JTextField manChapterContainer;
    public JLabel updaterStatus;
    public JLabel checkStatusLbl;
    public JButton checkStopPollingBtn;
    public JLabel autoBookSubjects;
    private JList<String> manLinkList;
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
    public JTextArea autoBookDescArea;
    private JScrollPane autoBookDescScrollPane;
    private JButton autoEditMetadataButton;


    public GUI() {
        initialize();
        loadDefaultCheckerList();

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

        autoVisitButton.addActionListener(arg0 -> {
            try {
                String toOpenHostSite;
                if (autoHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", "").equals("isohungrytls")) {
                    toOpenHostSite = "https://isohungrytls.com/";
                } else {
                    HostSettings emptyNovel = new HostSettings(
                            Objects.requireNonNull(autoHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", ""), "");
                    toOpenHostSite = emptyNovel.host;
                }
                URI uri = new URI(toOpenHostSite);
                openWebpage(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        autoShowBlacklistedTagsBtn.addActionListener(arg0 -> {
            DefaultListModel<String> tempListModel = new DefaultListModel<>();
            JList<String> tempJList = new JList<>(tempListModel);

            HostSettings tempSettings = new HostSettings(autoHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", ""), "");
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
            if (!autoNovel.autoChapterToChapter) {
                if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                        && (((Integer) firstChapter.getValue() < 1)
                        || ((Integer) lastChapter.getValue()) < 1)) {
                    showPopup("Chapter numbers can't be lower than 1.", "warning");
                    return;
                }
                if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                        && lastChapter.getValue() < firstChapter.getValue()) {
                    showPopup("Last chapter can't be lower than first chapter.", "warning");
                    return;
                }
                if ((!chapterAllCheckBox.isSelected()) && (toLastChapter.isSelected())
                        && ((Integer) firstChapter.getValue()) < 1) {
                    showPopup("First chapter number can't be lower than 1.", "warning");
                    return;
                }
            } else {
                if (autoFirstChapterURL.getText().isEmpty() && !useHeaderlessBrowserCheckBox.isSelected()) {
                    showPopup("First chapter URL is empty.", "warning");
                    return;
                }
                if (autoLastChapterURL.getText().isEmpty() && !useHeaderlessBrowserCheckBox.isSelected()) {
                    showPopup("Last chapter URL is empty.", "warning");
                    return;
                }
            }
            if (waitTime.getText().isEmpty()) {
                showPopup("Wait time cannot be empty.", "warning");
                return;
            }
            if (!waitTime.getText().matches("\\d+") && !waitTime.getText().isEmpty()) {
                showPopup("Wait time must contain numbers.", "warning");
                return;
            }
            if ((!saveLocation.getText().isEmpty()) && (!chapterListURL.getText().isEmpty())) {
                pagesLbl.setVisible(true);
                pagesCountLbl.setVisible(true);
                grabChaptersButton.setEnabled(false);
                grabChaptersButton.setVisible(false);
                stopButton.setEnabled(true);
                stopButton.setVisible(true);
                // Chapter grabbing
                try {
                    autoNovel.startDownload();
                } catch (NullPointerException | IllegalArgumentException err) {
                    appendText("auto", err.getMessage());
                    err.printStackTrace();
                } finally {
                    progressBar.setStringPainted(false);
                    progressBar.setValue(0);
                    grabChaptersButton.setEnabled(true);
                    grabChaptersButton.setVisible(true);
                    stopButton.setEnabled(false);
                    stopButton.setVisible(false);
                }
            }
        }));
        autoCheckAvailability.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            if (!chapterListURL.getText().isEmpty()) {
                autoBusyLabel.setVisible(true);
                autoNovel = new AutoNovel(this);
                if (!autoNovel.chapterLinks.isEmpty()) {
                    grabChaptersButton.setEnabled(true);
                    autoGetNumberButton.setEnabled(true);
                    autoEditMetaBtn.setEnabled(true);
                    autoEditBlacklistBtn.setEnabled(true);
                    pagesCountLbl.setText("");
                    pagesCountLbl.setVisible(false);
                    pagesLbl.setVisible(false);
                }
                if (autoNovel.autoChapterToChapter) {
                    grabChaptersButton.setEnabled(true);
                    autoEditMetaBtn.setEnabled(true);
                    autoEditBlacklistBtn.setEnabled(true);
                }
                autoBusyLabel.setVisible(false);
            }
        }));
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
        // Get chapter number
        autoGetNumberButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> getChapterNumber.main(this)));

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
        getLinksButton.addActionListener(e -> {
            if (manNovelURL.getText().isEmpty()) {
                JOptionPane.showMessageDialog(window, "URL field is empty.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                manNovelURL.requestFocusInWindow();
            }
            if (!manNovelURL.getText().isEmpty()) {
                try {
                    ManNovel.retrieveLinks(this);
                } catch (NullPointerException | IllegalArgumentException | IOException err) {
                    err.printStackTrace();
                } finally {
                    if (!listModelChapterLinks.isEmpty()) {
                        manRemoveLinksButton.setEnabled(true);
                    }
                }
            }
        });

        manSetMetadataButton.addActionListener(arg0 -> manSetMetadata.main());

        manRemoveLinksButton.addActionListener(arg0 -> {
            if (!listModelChapterLinks.isEmpty()) {
                int[] indices = manLinkList.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    listModelChapterLinks.removeElementAt(indices[i]);
                    ManNovel.chapterLinks.remove(indices[i]);
                }
                if (listModelChapterLinks.isEmpty()) {
                    manRemoveLinksButton.setEnabled(false);
                }
                appendText("manual", indices.length + " links removed.");
            }
        });
        // manual chapter download
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
                    try {
                        manProgressBar.setStringPainted(true);
                        manNovel = new ManNovel(this, "chapterToChapter");
                        // Exception handling
                    } catch (NullPointerException | IllegalArgumentException err) {
                        appendText("manual", err.getMessage());
                        err.printStackTrace();
                    } finally {
                        manProgressBar.setStringPainted(false);
                        manProgressBar.setValue(0);
                        manGrabChaptersButton.setEnabled(true);
                        manGrabChaptersButton.setVisible(true);
                        manStopButton.setEnabled(false);
                        manStopButton.setVisible(false);
                    }
                }
                // input validation
            } else {
                if (manNovelURL.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(window, "URL field is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manNovelURL.requestFocusInWindow();
                } else if (manSaveLocation.getText().isEmpty()) {
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
                        && (!manNovelURL.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty())
                        && (!manWaitTime.getText().isEmpty())) {
                    manGrabChaptersButton.setEnabled(false);
                    manGrabChaptersButton.setVisible(false);
                    manStopButton.setEnabled(true);
                    manStopButton.setVisible(true);
                    manProgressBar.setStringPainted(true);
                    try {
                        manNovel = new ManNovel(this, "chaptersFromList");
                        // Exception handling
                    } catch (NullPointerException | IllegalArgumentException err) {
                        appendText("manual", err.getMessage());
                        err.printStackTrace();
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
        manBlackListedTags.addActionListener(e -> manSetBlacklistedTags.main());
        chapterToChapterButton.addActionListener(e -> ChapterToChapter.main());
        updateButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            updaterStatus.setVisible(true);
            updateButton.setVisible(false);
            checkForUpdatesButton.setVisible(false);
            updaterStatus.setText("Updating...");
            updater.updateJar();
        }));
        checkAddNewEntryBtn.addActionListener(arg0 -> {
            String host = (String) JOptionPane.showInputDialog(this,
                    "Pick host:", "Add a autoNovel to check", JOptionPane.PLAIN_MESSAGE, null, HostSettings.websites, "wuxiaworld");
            String checkUrl = JOptionPane.showInputDialog(this,
                    "Novel URL:", "Add a autoNovel to check", JOptionPane.PLAIN_MESSAGE);
            if (!(checkUrl == null) && !(host == null)) {
                if (!host.isEmpty() && !checkUrl.isEmpty()) {
                    if (HostSettings.autoChapterToChapterWebsitesList.contains(host)) {
                        appendText("checker", host + " is not supported.");
                        return;
                    }
                    host = host.toLowerCase().replace(" ", "");
                    listModelCheckerLinks.addElement("[" + checkUrl + "]");
                    chapterChecker.hosts.add(host);
                    chapterChecker.urls.add(checkUrl);
                    checkRemoveEntry.setEnabled(true);
                    checkPollStartBtn.setEnabled(true);
                }
            }
        });
        checkPollStartBtn.addActionListener(g -> {
            if (chapterChecker.urls.isEmpty() || chapterChecker.hosts.isEmpty()) {
                showPopup("No checkers defined", "warning");
            } else {
                startPolling();
            }
        });

        checkStopPollingBtn.addActionListener(g -> stopPolling());

        checkRemoveEntry.addActionListener(gc -> {
            int[] indices = checkerList.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                listModelCheckerLinks.removeElementAt(indices[i]);
                chapterChecker.hosts.remove(indices[i]);
                chapterChecker.urls.remove(indices[i]);
            }
            if (listModelCheckerLinks.isEmpty()) {
                checkRemoveEntry.setEnabled(false);
                checkPollStartBtn.setEnabled(false);
            }
        });
        autoEditMetadataButton.addActionListener(e -> {

        });
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
        autoHostSelection.addItemListener(e -> {
            String selection = autoHostSelection.getSelectedItem().toString();
            if (HostSettings.headerlessBrowserWebsitesList.contains(selection)) {
                useHeaderlessBrowserCheckBox.setSelected(true);
                useHeaderlessBrowserCheckBox.setEnabled(false);
            } else {
                useHeaderlessBrowserCheckBox.setEnabled(true);
            }
            if (HostSettings.autoChapterToChapterWebsitesList.contains(selection)) {
                chapterAllCheckBox.setEnabled(false);
                firstChapter.setEnabled(false);
                lastChapter.setEnabled(false);
                toLastChapter.setEnabled(false);
                checkInvertOrder.setEnabled(false);
                autoChapterToChapterNumberField.setVisible(true);
                autoFirstChapterLbl.setVisible(true);
                autoFirstChapterURL.setVisible(true);
                autoLastChapterLbl.setVisible(true);
                autoLastChapterURL.setVisible(true);
            } else {
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
            }
            if (HostSettings.noHeaderlessBrowserWebsitesList.contains(selection)) {
                useHeaderlessBrowserCheckBox.setSelected(false);
                useHeaderlessBrowserCheckBox.setEnabled(false);
            }
        });

        autoEditBlacklistBtn.addActionListener(e -> autoSetBlacklistedTags.main(autoNovel));
        autoEditMetaBtn.addActionListener(e -> autoEditMetadata.main(autoNovel));
        checkForUpdatesButton.addActionListener(e -> Executors.newSingleThreadExecutor().execute(this::checkForNewReleases));
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
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                GUI window = new GUI();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
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

    private void initialize() {
        add(rootPanel);
        setTitle("Novel-Grabber " + versionNumber);
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        setMinimumSize(new Dimension(923, 683));
        Tray();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (!SystemTray.isSupported()) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            tabbedPane.setEnabledAt(2, false);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    private void Tray() {
        if (!SystemTray.isSupported()) {
            showPopup("SystemTray is not supported. Exiting...", "Error");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/favicon.png"));

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
        defaultItem0 = new MenuItem("checker not active");
        defaultItem0.addActionListener(arg0 -> {
            if (chapterChecker.checkerRunning) stopPolling();
        });
        popup.add(defaultItem0);
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

    public void stopPolling() {
        checkStatusLbl.setText("Stopping polling...");
        checkStopPollingBtn.setEnabled(false);
        chapterChecker.checkerRunning = false;
        defaultItem0.setLabel("checker not active");
        Executors.newSingleThreadExecutor().execute(() -> chapterChecker.killTask(this));
    }

    private void startPolling() {
        appendText("checker", "Started polling.");
        checkPollStartBtn.setEnabled(false);
        checkPollStartBtn.setVisible(false);
        checkStopPollingBtn.setVisible(true);
        checkStopPollingBtn.setEnabled(false);
        checkAddNewEntryBtn.setEnabled(false);
        checkRemoveEntry.setEnabled(false);
        defaultItem0.setLabel("Stop polling");
        checkBusyIcon.setVisible(true);
        checkStatusLbl.setVisible(true);
        Executors.newSingleThreadExecutor().execute(() -> chapterChecker.chapterPolling(this));
    }

    public void resetCheckerGUIButtons() {
        if (chapterChecker.urls.isEmpty()) {
            checkRemoveEntry.setEnabled(false);
            checkPollStartBtn.setEnabled(false);
        } else {
            checkRemoveEntry.setEnabled(true);
            checkPollStartBtn.setEnabled(true);
        }
        checkPollStartBtn.setVisible(true);
        checkAddNewEntryBtn.setEnabled(true);
        checkBusyIcon.setVisible(false);
        checkStopPollingBtn.setVisible(false);
        checkStatusLbl.setVisible(false);
    }

    private void loadDefaultCheckerList() {
        File filepath = new File(appdataPath + File.separator + "default.json");
        if (filepath.exists()) {
            checkDefaultFileLabel.setVisible(false);
            checkDefaultFileLabel.setEnabled(false);
            JSONParser parser = new JSONParser();
            try {
                JSONArray a = (JSONArray) parser.parse(new FileReader(filepath));
                for (Object o : a) {
                    JSONObject checker = (JSONObject) o;
                    chapterChecker.hosts.add((String) checker.get("HOST"));
                    chapterChecker.urls.add((String) checker.get("URL"));
                    listModelCheckerLinks.addElement("[" + checker.get("URL") + "]");
                }
            } catch (IOException | ParseException ec) {
                ec.printStackTrace();
            }
            if (!listModelCheckerLinks.isEmpty()) {
                appendText("checker", "Loaded default checker list.");
                startPolling();
            }
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

    public void setMaxProgress(String progressBarSelect, int progressAmount) {
        switch (progressBarSelect) {
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

    public void updateProgress(String progressBarSelect) {
        switch (progressBarSelect) {
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

    private void checkForNewReleases() {
        updaterStatus.setVisible(true);
        updaterStatus.setText("Checking for new releases...");
        checkForUpdatesButton.setVisible(false);
        try {
            Document doc = Jsoup.connect("https://github.com/Flameish/Novel-Grabber/releases").get();
            Element versionString = doc.select("a[title]").first();
            String oldVersionString = versionNumber;
            String newVersionString = versionString.attr("title");
            if (updater.compareStrings(oldVersionString, newVersionString) == -1) {
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
            coverImage.setIcon(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
        else
            coverImage.setIcon(new ImageIcon(new ImageIcon(bufferedImage).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
    }

    private void createUIComponents() {
        // Automatic Tab
        autoHostSelection = new JComboBox(HostSettings.websites);

        autoBrowserCombobox = new JComboBox(browserList);

        autoChapterToChapterNumberField = new JTextField("Number");
        autoChapterToChapterNumberField.setForeground(Color.GRAY);

        autoShowBlacklistedTagsBtn = new JButton(new ImageIcon(getClass().getResource("/images/list_icon.png")));
        autoShowBlacklistedTagsBtn.setBorder(BorderFactory.createEmptyBorder());
        autoShowBlacklistedTagsBtn.setContentAreaFilled(false);

        autoCheckAvailability = new JButton(new ImageIcon(getClass().getResource("/images/check_icon.png")));
        autoCheckAvailability.setBorder(BorderFactory.createEmptyBorder());
        autoCheckAvailability.setContentAreaFilled(false);

        chapterListURL = new JTextField();
        // Listen for changes in the text
        chapterListURL.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            void warn() {
                grabChaptersButton.setEnabled(false);
            }
        });

        autoVisitButton = new JButton(new ImageIcon(getClass().getResource("/images/website_icon.png")));
        autoVisitButton.setBorder(BorderFactory.createEmptyBorder());
        autoVisitButton.setContentAreaFilled(false);

        autoBusyLabel = new JLabel(new ImageIcon(getClass().getResource("/images/busy.gif")));

        browseButton = new JButton(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        browseButton.setBorder(BorderFactory.createEmptyBorder());
        browseButton.setContentAreaFilled(false);

        coverImage = new JLabel(new ImageIcon(getClass().getResource("/images/cover_placeholder.png")));
        coverImage.setBorder(BorderFactory.createEmptyBorder());

        autoEditMetadataButton = new JButton(new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        autoEditMetadataButton.setBorder(BorderFactory.createEmptyBorder());
        autoEditMetadataButton.setContentAreaFilled(false);

        autoEditMetaBtn = new JButton(new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        autoEditMetaBtn.setBorder(BorderFactory.createEmptyBorder());
        autoEditMetaBtn.setContentAreaFilled(false);

        autoEditBlacklistBtn = new JButton(new ImageIcon(getClass().getResource("/images/list_icon.png")));
        autoEditBlacklistBtn.setBorder(BorderFactory.createEmptyBorder());
        autoEditBlacklistBtn.setContentAreaFilled(false);

        autoGetNumberButton = new JButton(new ImageIcon(getClass().getResource("/images/search_icon.png")));
        autoGetNumberButton.setBorder(BorderFactory.createEmptyBorder());
        autoGetNumberButton.setContentAreaFilled(false);

        exportSelection = new JComboBox<>(exportFormats);

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

        manSetMetadataButton = new JButton(new ImageIcon(getClass().getResource("/images/settings_icon.png")));
        manSetMetadataButton.setBorder(BorderFactory.createEmptyBorder());
        manSetMetadataButton.setContentAreaFilled(false);

        manBlackListedTags = new JButton(new ImageIcon(getClass().getResource("/images/list_icon.png")));
        manBlackListedTags.setBorder(BorderFactory.createEmptyBorder());
        manBlackListedTags.setContentAreaFilled(false);

        manRemoveLinksButton = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        manRemoveLinksButton.setBorder(BorderFactory.createEmptyBorder());
        manRemoveLinksButton.setContentAreaFilled(false);

        manLinkList = new JList<>(listModelChapterLinks);
        manLinkScrollPane = new JScrollPane(manLinkList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        manLogArea = new JTextArea();
        manLogArea.setLineWrap(true);
        manLogArea.setWrapStyleWord(true);
        manLogScrollPane = new JScrollPane(manLogArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        manJsoupInfoButton = new JButton(new ImageIcon(getClass().getResource("/images/info_icon.png")));
        manJsoupInfoButton.setBorder(BorderFactory.createEmptyBorder());
        manJsoupInfoButton.setContentAreaFilled(false);

        manBrowseLocationButton = new JButton(new ImageIcon(getClass().getResource("/images/folder_icon.png")));
        manBrowseLocationButton.setBorder(BorderFactory.createEmptyBorder());
        manBrowseLocationButton.setContentAreaFilled(false);

        manExportSelection = new JComboBox<>(exportFormats);

        manWaitTime = new JTextField("0");
        manWaitTime.setHorizontalAlignment(SwingConstants.CENTER);

        // Update Tab
        updateTextArea = new JTextArea();
        updateTextArea.setLineWrap(true);
        updateTextArea.setWrapStyleWord(true);
        updateScrollPane = new JScrollPane(updateTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Checker
        checkRemoveEntry = new JButton(new ImageIcon(getClass().getResource("/images/remove_icon.png")));
        checkRemoveEntry.setBorder(BorderFactory.createEmptyBorder());
        checkRemoveEntry.setContentAreaFilled(false);

        checkAddNewEntryBtn = new JButton(new ImageIcon(getClass().getResource("/images/add_icon.png")));
        checkAddNewEntryBtn.setBorder(BorderFactory.createEmptyBorder());
        checkAddNewEntryBtn.setContentAreaFilled(false);

        checkPollStartBtn = new JButton(new ImageIcon(getClass().getResource("/images/start_icon.png")));
        checkPollStartBtn.setBorder(BorderFactory.createEmptyBorder());
        checkPollStartBtn.setContentAreaFilled(false);

        checkStopPollingBtn = new JButton(new ImageIcon(getClass().getResource("/images/stop_icon.png")));
        checkStopPollingBtn.setBorder(BorderFactory.createEmptyBorder());
        checkStopPollingBtn.setContentAreaFilled(false);

        checkerList = new JList(listModelCheckerLinks);
        checkerListScrollPane = new JScrollPane(checkerList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        checkBusyIcon = new JLabel(new ImageIcon(getClass().getResource("/images/busy.gif")));

        checkerLogArea = new JTextArea();
        checkerLogArea.setLineWrap(true);
        checkerLogArea.setWrapStyleWord(true);
        checkerLogScrollPane = new JScrollPane(checkerLogArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPopupMenu checkPopUp = new JPopupMenu();
        addPopup(checkerList, checkPopUp);

        JMenuItem checkLoadFromFile = new JMenuItem("Load Checkers from file");
        checkLoadFromFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(appdataPath));
            chooser.setDialogTitle("Open File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.json", "json", "JSON");
            chooser.setFileFilter(filter);
            String filepath;
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                listModelCheckerLinks.clear();
                chapterChecker.hosts.clear();
                chapterChecker.urls.clear();
                filepath = chooser.getSelectedFile().toString();
                JSONParser parser = new JSONParser();
                try {
                    JSONArray a = (JSONArray) parser.parse(new FileReader(filepath));
                    for (Object o : a) {
                        JSONObject checker = (JSONObject) o;
                        chapterChecker.hosts.add((String) checker.get("HOST"));
                        chapterChecker.urls.add((String) checker.get("URL"));
                        listModelCheckerLinks.addElement("[" + checker.get("URL") + "]");
                    }
                } catch (IOException | ParseException ec) {
                    ec.printStackTrace();
                }
                if (!listModelCheckerLinks.isEmpty()) {
                    checkRemoveEntry.setEnabled(true);
                    checkPollStartBtn.setEnabled(true);
                }
                appendText("checker", "Loaded checkers from file.");
            }
        });
        checkLoadFromFile.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        checkPopUp.add(checkLoadFromFile);

        JSeparator separator_12 = new JSeparator();
        checkPopUp.add(separator_12);

        JMenuItem checkToFile = new JMenuItem("Save to file");
        checkToFile.setToolTipText(
                "<html><p width=\"300\">Save checkers to file. \"default.txt\" will be loaded and started on startup.</p></html>");
        checkToFile.addActionListener(e -> {
            if (chapterChecker.urls.isEmpty()) {
                showPopup("checker list is empty", "warning");
                return;
            }
            File dir = new File(appdataPath);
            if (!dir.exists()) dir.mkdirs();
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(appdataPath));
            chooser.setDialogTitle("Save As");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.json", "json", "JSON");
            chooser.setFileFilter(filter);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String filepath = chooser.getSelectedFile().toString();
                if (!filepath.endsWith(".json")) filepath = filepath + ".json";
                chapterChecker.writeDataToJSON(filepath, false);
                if ("default.json".equals(filepath)) {
                    checkDefaultFileLabel.setVisible(false);
                    checkDefaultFileLabel.setEnabled(false);
                    appendText("checker", "Set new default checkers list.");
                }
                appendText("checker", "Saved checkers to file.");
            }
        });
        checkToFile.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        checkPopUp.add(checkToFile);
    }
}
