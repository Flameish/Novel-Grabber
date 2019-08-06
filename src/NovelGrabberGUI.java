import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import static java.lang.System.out;

/*
 *  Window display and handling.
 */
public class NovelGrabberGUI {
    static DefaultListModel<String> listModelChapterLinks = new DefaultListModel<>();
    static DefaultListModel<String> listModelCheckerLinks = new DefaultListModel<>();
    private static final JList<String> checkList = new JList<>(listModelCheckerLinks);
    static String versionNumber = "v1.8.0";
    static TrayIcon trayIcon;
    private static final String NL = System.getProperty("line.separator");
    private static JFrame frmNovelGrabber;
    static String appdataPath = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Novel-Grabber";
    static JCheckBox getImages;
    private static final JList<String> chapterLinkList = new JList<>(listModelChapterLinks);
    static JLabel updateProcessLbl;
    static JTextField singleChapterLink;
    static JTextField chapterListURL;
    static JTextField saveLocation;
    static JComboBox<String> allChapterHostSelection;
    static JComboBox<String> singleChapterHostSelection;
    static JTextField firstChapter;
    static JTextField lastChapter;
    static JTextField waitTime;
    static JCheckBox manGetImages;
    static JCheckBox useNumeration;
    static JCheckBox checkInvertOrder;
    static JCheckBox chapterAllCheckBox;
    static JCheckBox manCreateToc = new JCheckBox("Create ToC");
    static JTextField manSaveLocation;
    static JTextField manWaitTime;
    static JTextField manChapterContainer;
    static JCheckBox manCheckInvertOrder;
    static JCheckBox manUseNumeration = new JCheckBox("Chapter numeration");
    static JTextField manChapterListURL;
    private static JTextArea logArea;
    private static JTextArea manLogField;
    static JProgressBar progressBar;
    private static JProgressBar manProgressBar;
    static String[] chapterToChapterArgs = new String[3];
    static JButton checkStopPollingBtn;
    static JLabel checkStatusLbl;
    private static JLabel checkBusyIcon;
    private static JButton checkRemoveEntry;
    static private JTextArea checkLogField;
    private JButton checkHideLogBtn;
    private static JButton checkShowLogBtn;
    private static JButton checkAddNewEntryBtn;
    private static JLabel checkDefaultFileLabel;
    private static MenuItem defaultItem0;
    private static JButton checkPollStartBtn;
    private JPanel updatePane;
    static private JTextArea updateLogField;
    private static JLabel updateStatusLbl;
    private JTabbedPane tabbedPane;
    private JScrollPane updateLogScrollpane;
    private JLabel updateLogLbl;
    private JButton manShowTagsBtn;
    private DefaultListModel<String> tempListModel;
    static JCheckBox createTocCheckBox;
    private static List<String> blacklistedTags = new ArrayList<>();
    static JCheckBox toLastChapter;

    /**
     * Create the application.
     */
    private NovelGrabberGUI() {
        initialize();
        checkForNewReleases();
        loadDefaultCheckerList();
    }

    /**
     * Launch the application
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                NovelGrabberGUI window = new NovelGrabberGUI();
                frmNovelGrabber.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    static void appendText(String logWindow, String logMsg) {
        switch (logWindow) {
            case "auto":
                logArea.append(logMsg + NL);
                logArea.setCaretPosition(logArea.getText().length());
                break;
            case "manual":
                manLogField.append(logMsg + NL);
                manLogField.setCaretPosition(manLogField.getText().length());
                break;
            case "checker":
                checkLogField.append(logMsg + NL);
                checkLogField.setCaretPosition(checkLogField.getText().length());
                break;
            case "update":
                updateLogField.append(" - " + logMsg + NL);
                updateLogField.setCaretPosition(updateLogField.getText().length());
                break;
        }
    }

    static void updateProgress(String progressBarSelect) {
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

    static void setMaxProgress(String progressBarSelect, int progressAmount) {
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

    static void showPopup(String errorMsg, String kind) {
        switch (kind) {
            case "warning":
                JOptionPane.showMessageDialog(frmNovelGrabber, errorMsg, "Warning", JOptionPane.WARNING_MESSAGE);
                break;
            case "error":
                JOptionPane.showMessageDialog(frmNovelGrabber, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    static void stopPolling() {
        checkStatusLbl.setText("Stopping polling...");
        checkStopPollingBtn.setEnabled(false);
        chapterChecker.checkerRunning = false;
        defaultItem0.setLabel("Checker not active");
        Executors.newSingleThreadExecutor().execute(chapterChecker::killTask);
    }

    private static void startPolling() {
        appendText("checker", "Started polling.");
        checkPollStartBtn.setEnabled(false);
        checkPollStartBtn.setVisible(false);
        checkStopPollingBtn.setVisible(true);
        checkStopPollingBtn.setEnabled(false);
        checkAddNewEntryBtn.setEnabled(false);
        checkRemoveEntry.setEnabled(false);
        checkStopPollingBtn.setText("Stop checking");
        defaultItem0.setLabel("Stop polling");
        checkBusyIcon.setVisible(true);
        checkStatusLbl.setVisible(true);
        Executors.newSingleThreadExecutor().execute(chapterChecker::chapterPolling);
    }

    static void resetCheckerGUIButtons() {
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

    private static void loadDefaultCheckerList() {
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

    /*
     *Looks up the latest version String on github releases. Displays the Update Tab if a new version was released.    /**
     * TODO: future 2+.x.x/ x.10+.x releases might break the comparison
     */
    private void checkForNewReleases() {
        try {
            Document doc = Jsoup.connect("https://github.com/Flameish/Novel-Grabber/releases").get();
            Element versionString = doc.select("a[title]").first();
            String newVersionString = versionString.attr("title");
            int oldVersionNumber = Integer.parseInt(versionNumber.replaceAll("\\D+", ""));
            int newVersionNumber = Integer.parseInt(newVersionString.replaceAll("\\D+", ""));
            if (newVersionNumber > oldVersionNumber) {
                tabbedPane.addTab("Update", null, updatePane, null);
                updateStatusLbl.setText("A new update of Novel-Grabber was released. The latest version is: " + newVersionString);
                frmNovelGrabber.setTitle("Novel-Grabber " + versionNumber + " - New version released");
                updateLogLbl.setText("Changes in " + newVersionString + ":");
                Element releaseDesc = doc.select(".markdown-body").first();
                Elements descLines = releaseDesc.select("li");
                for (Element s : descLines) {
                    NovelGrabberGUI.appendText("update", s.text());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Tray() {
        if (!SystemTray.isSupported()) {
            showPopup("SystemTray is not supported. Exiting...", "Error");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/favicon.png"));

        ActionListener exitListener = e -> {
            out.println("Exiting...");
            System.exit(0);
        };
        ActionListener openWindow = e -> frmNovelGrabber.setVisible(true);

        PopupMenu popup = new PopupMenu();
        MenuItem topLable = new MenuItem("Novel-Grabber");
        popup.add(topLable);
        MenuItem aboutLabel = new MenuItem("About");
        aboutLabel.addActionListener(arg01 -> {
            InputStream in = getClass().getResourceAsStream("files/about.txt");
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
        defaultItem0 = new MenuItem("Checker not active");
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

    /**
     * Initialize the contents of the frame.
     */

    private void initialize() {
        // tooltip style
        int dismissDelay;
        dismissDelay = Integer.MAX_VALUE;
        ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);
        UIManager.put("ToolTip.background", new ColorUIResource(Color.white));
        String toolTipStyle = "<html><p width=\"300\">";
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        Tray();
        frmNovelGrabber = new JFrame();
        frmNovelGrabber.setResizable(false);
        frmNovelGrabber.setIconImage(favicon.getImage());
        frmNovelGrabber.setTitle("Novel-Grabber " + versionNumber);
        frmNovelGrabber.setBounds(100, 100, 588, 650);
        frmNovelGrabber.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (!SystemTray.isSupported()) {
            frmNovelGrabber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        frmNovelGrabber.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                frmNovelGrabber.setVisible(false);
            }
        });
        frmNovelGrabber.setBackground(Color.LIGHT_GRAY);
        frmNovelGrabber.getContentPane().setLayout(null);
        //tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFocusable(false);
        tabbedPane.setBounds(0, 0, 594, 634);
        frmNovelGrabber.getContentPane().add(tabbedPane);
        //Automatic Pane
        JPanel automaticPane = new JPanel();
        automaticPane.setLayout(null);
        tabbedPane.addTab("Automatic", null, automaticPane, null);

        JPanel allChapterPane = new JPanel();
        allChapterPane.setBounds(10, 5, 557, 473);
        automaticPane.add(allChapterPane);
        allChapterPane.setBorder(BorderFactory.createTitledBorder("Get multiple chapters"));
        allChapterPane.setLayout(null);

        chapterListURL = new JTextField();
        chapterListURL.setBounds(152, 19, 390, 25);
        allChapterPane.add(chapterListURL);
        chapterListURL.setToolTipText("");
        chapterListURL.setColumns(10);

        JLabel lblNovelChapterList = new JLabel("Table of Contents URL:");
        lblNovelChapterList.setLabelFor(chapterListURL);
        lblNovelChapterList.setBounds(10, 19, 116, 25);
        allChapterPane.add(lblNovelChapterList);
        lblNovelChapterList.setFont(new Font("Tahoma", Font.PLAIN, 11));


        JLabel lblNewLabel = new JLabel("Host website:");
        lblNewLabel.setBounds(10, 49, 80, 25);
        allChapterPane.add(lblNewLabel);
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));

        allChapterHostSelection = new JComboBox<>(Novel.websites);
        allChapterHostSelection.setFocusable(false);
        allChapterHostSelection.setBounds(152, 49, 260, 25);
        allChapterPane.add(allChapterHostSelection);

        ImageIcon showTagsIcon = new ImageIcon(getClass().getResource("/images/list_icon.png"));
        JButton showTagsBtn = new JButton(showTagsIcon);
        showTagsBtn.setFocusPainted(false);
        showTagsBtn.setBorder(BorderFactory.createEmptyBorder());
        showTagsBtn.setContentAreaFilled(false);
        showTagsBtn.setBounds(422, 49, 25, 25);
        showTagsBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        showTagsBtn.addActionListener(arg0 -> {
            tempListModel = new DefaultListModel<>();
            JList<String> tempJList = new JList<>(tempListModel);

            Novel scummyNovelToGetTags = new Novel(allChapterHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", ""), "");
            JScrollPane tagScrollPane = new JScrollPane(tempJList);
            tagScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            tagScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tagScrollPane.setBounds(0, 0, 532, 180);
            if (!(scummyNovelToGetTags.getBlacklistedTags() == null)) {
                for (String alreadyBlacklistedTags : scummyNovelToGetTags.getBlacklistedTags()) {
                    tempListModel.addElement(alreadyBlacklistedTags);
                }
            }
            JOptionPane.showOptionDialog(null, tagScrollPane, "Tags to be removed", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, null, null);
        });
        allChapterPane.add(showTagsBtn);

        JButton browseBtn = new JButton("Browse...");
        browseBtn.setFocusPainted(false);
        browseBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        browseBtn.addActionListener(arg0 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                saveLocation.setText(chooser.getSelectedFile().toString());
            }
        });
        browseBtn.setBounds(456, 79, 86, 27);
        allChapterPane.add(browseBtn);

        JLabel saveLocationLbl = new JLabel("Save directory:");
        saveLocationLbl.setBounds(10, 80, 103, 25);
        allChapterPane.add(saveLocationLbl);
        saveLocationLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));

        saveLocation = new JTextField();
        saveLocation.setBounds(152, 80, 294, 25);
        allChapterPane.add(saveLocation);
        saveLocation.setToolTipText("");
        saveLocation.setColumns(10);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Zurich Ex BT", Font.PLAIN, 13));
        logArea.setBounds(-22, 11, 235, 41);
        allChapterPane.add(logArea);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(10, 267, 532, 163);
        allChapterPane.add(scrollPane);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        addPopup(logArea, popupMenu);

        JMenuItem saveLogBtn = new JMenuItem("Save log to file");
        saveLogBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        saveLogBtn.addActionListener(arg0 -> {
            if (!logArea.getText().isEmpty()) {
                String fileName = "log.txt";
                try (PrintStream out = new PrintStream(saveLocation.getText() + File.separator + fileName,
                        "UTF-8")) {
                    out.print(logArea.getText());
                } catch (IOException e) {
                    out.println(e.getMessage());
                }
            } else {
                showPopup("Log is empty", "warning");
            }

        });
        popupMenu.add(saveLogBtn);

        JMenuItem mntmClearLog = new JMenuItem("Clear log");
        mntmClearLog.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        mntmClearLog.addActionListener(e -> {
            if (!logArea.getText().isEmpty()) {
                logArea.setText(null);
            }
        });

        JSeparator separator_1 = new JSeparator();
        popupMenu.add(separator_1);
        popupMenu.add(mntmClearLog);

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 436, 409, 25);
        progressBar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        progressBar.setForeground(new Color(0, 128, 128));
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setString("");
        allChapterPane.add(progressBar);

        JButton grabChapters = new JButton("Grab chapters");
        grabChapters.setFocusPainted(false);
        grabChapters.setFont(new Font("Tahoma", Font.PLAIN, 11));
        grabChapters.setBounds(429, 435, 113, 26);
        allChapterPane.add(grabChapters);

        JPanel chapterSelect = new JPanel();
        chapterSelect.setBounds(10, 109, 532, 45);
        chapterSelect.setBorder(BorderFactory.createTitledBorder("Select chapters to download"));
        allChapterPane.add(chapterSelect);
        chapterSelect.setLayout(null);

        chapterAllCheckBox = new JCheckBox("All");
        chapterAllCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
        chapterAllCheckBox.setFocusable(false);
        chapterAllCheckBox.setBounds(81, 12, 62, 23);
        chapterSelect.add(chapterAllCheckBox);
        chapterAllCheckBox.addItemListener(arg0 -> {
            if (chapterAllCheckBox.isSelected()) {
                firstChapter.setEnabled(false);
                lastChapter.setEnabled(false);
                toLastChapter.setEnabled(false);
            }
            if (!chapterAllCheckBox.isSelected()) {
                firstChapter.setEnabled(true);
                lastChapter.setEnabled(true);
                toLastChapter.setEnabled(true);
            }
        });

        JLabel lblChapter = new JLabel("Chapter range:");
        lblChapter.setBounds(190, 16, 113, 14);
        chapterSelect.add(lblChapter);

        firstChapter = new JTextField();
        firstChapter.setBounds(305, 13, 60, 20);
        firstChapter.setColumns(10);
        firstChapter.setHorizontalAlignment(JTextField.CENTER);
        chapterSelect.add(firstChapter);

        JLabel lblTo = new JLabel("-");
        lblTo.setBounds(376, 11, 6, 20);
        lblTo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        chapterSelect.add(lblTo);

        lastChapter = new JTextField();
        lastChapter.setBounds(393, 13, 60, 20);
        lastChapter.setColumns(10);
        lastChapter.setHorizontalAlignment(JTextField.CENTER);
        chapterSelect.add(lastChapter);

        toLastChapter = new JCheckBox("Last");
        toLastChapter.setFont(new Font("Tahoma", Font.PLAIN, 11));
        toLastChapter.setFocusable(false);
        toLastChapter.setBounds(460, 13, 60, 20);
        chapterSelect.add(toLastChapter);
        toLastChapter.addItemListener(arg0 -> {
            if (toLastChapter.isSelected()) {
                lastChapter.setEnabled(false);
                chapterAllCheckBox.setEnabled(false);
            }
            if (!toLastChapter.isSelected()) {
                lastChapter.setEnabled(true);
                chapterAllCheckBox.setEnabled(true);
            }
        });

        JPanel optionSelect = new JPanel();
        optionSelect.setBounds(10, 165, 532, 95);
        optionSelect.setBorder(BorderFactory.createTitledBorder("Option select"));
        allChapterPane.add(optionSelect);
        optionSelect.setLayout(null);

        createTocCheckBox = new JCheckBox("Create ToC");
        createTocCheckBox.setFocusPainted(false);
        createTocCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
        createTocCheckBox.setBounds(6, 20, 81, 23);
        createTocCheckBox.setSelected(true);
        optionSelect.add(createTocCheckBox);
        createTocCheckBox.setToolTipText(toolTipStyle
                + "Will create a \"Table of Contents\" file which can be used to convert all chapter files into a single epub file in calibre.</p></html>");


        useNumeration = new JCheckBox("Chapter numeration");
        useNumeration = new JCheckBox("Chapter numeration");
        useNumeration.setFocusPainted(false);
        useNumeration.setToolTipText(toolTipStyle
                + "Will add a chapter number in front of the chapter name to keep them in order when converting.</p></html>");
        useNumeration.setFont(new Font("Tahoma", Font.PLAIN, 11));
        useNumeration.setBounds(6, 40, 121, 23);
        optionSelect.add(useNumeration);

        checkInvertOrder = new JCheckBox("Invert chapter order");
        checkInvertOrder.setFocusPainted(false);
        checkInvertOrder.setToolTipText(
                "<html><p width=\"300\">Invert  the chapter order and download the last chapter first. Useful if sites list the highest chapter at the top</p></html>");
        checkInvertOrder.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkInvertOrder.setBounds(6, 60, 129, 23);
        optionSelect.add(checkInvertOrder);

        getImages = new JCheckBox("Get images");
        getImages.setFocusPainted(false);
        getImages.setToolTipText(
                "<html><p width=\"300\">Download and display images.</p></html>");
        getImages.setFont(new Font("Tahoma", Font.PLAIN, 11));
        getImages.setBounds(151, 20, 150, 23);
        optionSelect.add(getImages);

        JLabel sleepLbl = new JLabel("Wait time:");
        sleepLbl.setBounds(389, 67, 66, 14);
        sleepLbl.setToolTipText(
                "<html><p width=\"300\">Time in miliseconds to wait before each chapter grab. (1000 for 1 second) Please select an appropriate wait time for the host.</p></html>");
        optionSelect.add(sleepLbl);

        waitTime = new JTextField();
        waitTime.setHorizontalAlignment(SwingConstants.CENTER);
        waitTime.setColumns(10);
        waitTime.setBounds(456, 64, 66, 20);
        waitTime.setText("0");
        optionSelect.add(waitTime);

        JButton btnVisitWebsite = new JButton("Visit...");
        btnVisitWebsite.addActionListener(arg0 -> {
            try {
                Novel emptyNovel = new Novel(
                        Objects.requireNonNull(allChapterHostSelection.getSelectedItem()).toString().toLowerCase().replace(" ", ""), "");
                URI uri = new URI(emptyNovel.getHost());
                openWebpage(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        btnVisitWebsite.setFocusPainted(false);
        btnVisitWebsite.setBounds(456, 48, 86, 27);
        allChapterPane.add(btnVisitWebsite);

        // Get chapter number
        JPanel singleChapterPane = new JPanel();
        singleChapterPane.setBounds(10, 489, 557, 95);
        automaticPane.add(singleChapterPane);
        singleChapterPane.setBorder(BorderFactory.createTitledBorder("Display chapter number of chapter"));
        singleChapterPane.setLayout(null);

        JButton getChapterBtn = new JButton("Get number");
        getChapterBtn.setFocusPainted(false);
        getChapterBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        getChapterBtn.setBounds(429, 58, 113, 27);
        singleChapterPane.add(getChapterBtn);

        singleChapterLink = new JTextField();
        singleChapterLink.setBounds(133, 25, 409, 25);
        singleChapterPane.add(singleChapterLink);
        singleChapterLink.setColumns(10);

        JLabel lblchapterURL = new JLabel("Chapter URL:");
        lblchapterURL.setBounds(10, 24, 100, 25);
        singleChapterPane.add(lblchapterURL);
        lblchapterURL.setFont(new Font("Tahoma", Font.PLAIN, 11));

        JLabel label = new JLabel("Host website:");
        label.setFont(new Font("Tahoma", Font.PLAIN, 11));
        label.setBounds(10, 59, 73, 25);
        singleChapterPane.add(label);

        singleChapterHostSelection = new JComboBox<>(Novel.websites);
        singleChapterHostSelection.setFocusable(false);
        singleChapterHostSelection.setBounds(133, 59, 286, 25);
        singleChapterPane.add(singleChapterHostSelection);

        // Manual Tab
        JPanel manualPane = new JPanel();
        tabbedPane.addTab("Manual", null, manualPane, null);
        manualPane.setLayout(null);

        JPanel chapterLinkPane = new JPanel();
        chapterLinkPane.setBounds(10, 5, 557, 305);
        manualPane.add(chapterLinkPane);
        chapterLinkPane.setBorder(BorderFactory.createTitledBorder("Chapter links select"));
        chapterLinkPane.setLayout(null);

        JLabel lblManualToc = new JLabel("Table Of Contents URL:");
        lblManualToc.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblManualToc.setBounds(13, 17, 158, 25);
        chapterLinkPane.add(lblManualToc);

        JSeparator manLinkPaneSeperator = new JSeparator();
        chapterLinkPane.add(manLinkPaneSeperator);
        manLinkPaneSeperator.setBounds(13, 70, 533, 5);

        manChapterListURL = new JTextField();
        manChapterListURL.setBounds(178, 14, 260, 25);
        chapterLinkPane.add(manChapterListURL);
        manChapterListURL.setColumns(10);

        JButton removeLinks = new JButton("Remove links");
        removeLinks.setFont(new Font("Tahoma", Font.PLAIN, 11));
        removeLinks.setEnabled(false);
        removeLinks.setFocusPainted(false);
        removeLinks.addActionListener(arg0 -> {
            if (!listModelChapterLinks.isEmpty()) {
                int[] indices = chapterLinkList.getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    listModelChapterLinks.removeElementAt(indices[i]);
                    manFetchChapters.chapterURLs.remove(indices[i]);
                }
                if (listModelChapterLinks.isEmpty()) {
                    removeLinks.setEnabled(false);
                }
                appendText("manual", indices.length + " links removed.");
            }
        });
        removeLinks.setBounds(448, 77, 99, 25);
        chapterLinkPane.add(removeLinks);

        JTabbedPane linkSelectTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        linkSelectTabbedPane.setFocusable(false);
        linkSelectTabbedPane.setBounds(10, 86, 537, 207);

        chapterLinkPane.add(linkSelectTabbedPane);

        JPanel manLinkSelect = new JPanel();
        manLinkSelect.setBackground(Color.WHITE);
        manLinkSelect.setLayout(null);
        linkSelectTabbedPane.addTab("Link select", null, manLinkSelect, null);

        chapterLinkList.setBackground(Color.WHITE);
        chapterLinkList.setVisibleRowCount(-1);
        chapterLinkList.setLayoutOrientation(JList.VERTICAL_WRAP);
        chapterLinkList.setFixedCellWidth(268);
        chapterLinkPane.add(chapterLinkList);

        manLogField = new JTextArea();
        manLogField.setFocusable(false);
        manLogField.setBounds(0, 0, 532, 159);
        manLogField.setEditable(false);
        manLogField.setFont(new Font("Zurich Ex BT", Font.PLAIN, 13));

        JPanel manLogArea = new JPanel();
        manLogArea.add(manLogField);
        manLogArea.setLayout(null);
        linkSelectTabbedPane.addTab("Log", null, manLogArea, null);

        JScrollPane scrollPane_1 = new JScrollPane(chapterLinkList);
        scrollPane_1.setBounds(0, 0, 532, 179);
        manLinkSelect.add(scrollPane_1);

        JScrollPane manScrollPane = new JScrollPane(manLogField);
        manScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        manScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        manScrollPane.setBounds(0, 0, 532, 180);
        manLogArea.add(manScrollPane);

        JPopupMenu popupMenu_1 = new JPopupMenu();
        addPopup(manLogField, popupMenu_1);

        JMenuItem mntmSaveLogTo = new JMenuItem("Save log to file");
        mntmSaveLogTo.addActionListener(e -> {
            if (!manLogField.getText().isEmpty()) {
                String fileName = "manual log.txt";
                try (PrintStream out = new PrintStream(manSaveLocation.getText() + File.separator + fileName,
                        "UTF-8")) {
                    out.print(manLogField.getText());
                } catch (IOException ec) {
                    out.println(ec.getMessage());
                }
            } else {
                showPopup("Log is empty", "warning");
            }
        });
        mntmSaveLogTo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        popupMenu_1.add(mntmSaveLogTo);

        JSeparator separator_2 = new JSeparator();
        popupMenu_1.add(separator_2);

        JMenuItem mntmClearLog_1 = new JMenuItem("Clear log");
        mntmClearLog_1.addActionListener(e -> {
            if (!manLogField.getText().isEmpty()) {
                manLogField.setText(null);
            }
        });
        mntmClearLog_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        popupMenu_1.add(mntmClearLog_1);

        JButton retrieveLinks = new JButton("Retrieve Links");
        retrieveLinks.setFocusPainted(false);
        retrieveLinks.setFont(new Font("Tahoma", Font.PLAIN, 11));
        retrieveLinks.addActionListener(e -> {
            if (manChapterListURL.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frmNovelGrabber, "URL field is empty.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                manChapterListURL.requestFocusInWindow();
            }
            if (!manChapterListURL.getText().isEmpty()) {
                try {
                    retrieveLinks.setEnabled(false);
                    manFetchChapters.retrieveLinks();
                } catch (NullPointerException | IllegalArgumentException | IOException err) {
                    err.printStackTrace();
                } finally {
                    retrieveLinks.setEnabled(true);
                    if (!listModelChapterLinks.isEmpty()) {
                        removeLinks.setEnabled(true);
                    }
                }
            }
        });
        retrieveLinks.setBounds(448, 13, 99, 27);
        chapterLinkPane.add(retrieveLinks);

        JCheckBox manLinkToLink = new JCheckBox("Use Chapter-To-Chapter navigation");
        manLinkToLink.setFocusPainted(false);
        manLinkToLink.setToolTipText(
                "<html><p width=\"300\">Navigates a novel/website from chapter to chapter instead of getting" +
                        " all links from a Table of Contents page.</p></html>");
        manLinkToLink.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manLinkToLink.setBounds(10, 47, 200, 20);
        manLinkToLink.addActionListener(arg0 -> {
            if (manLinkToLink.isSelected()) {
                linkSelectTabbedPane.remove(manLinkSelect);
                manChapterListURL.setEnabled(false);
                manCheckInvertOrder.setEnabled(false);
                JPanel panel = new JPanel();
                panel.setLayout(null);
                panel.setPreferredSize(new Dimension(250, 130));

                JLabel firstChapterLbl = new JLabel("First Chapter URL:");
                firstChapterLbl.setBounds(0, 0, 250, 20);
                panel.add(firstChapterLbl);

                JTextField firstChapterField = new JTextField();
                firstChapterField.setBounds(0, 20, 250, 20);
                panel.add(firstChapterField);

                JLabel lastChapterLbl = new JLabel("Last Chapter URL:");
                lastChapterLbl.setBounds(0, 40, 250, 20);
                panel.add(lastChapterLbl);

                JTextField lastChapterField = new JTextField();
                lastChapterField.setBounds(0, 60, 250, 20);
                panel.add(lastChapterField);

                JLabel nextChapterBtnLbl = new JLabel("Next Chapter button selector:");
                nextChapterBtnLbl.setBounds(0, 80, 250, 20);
                panel.add(nextChapterBtnLbl);

                JTextField nextChapterBtnField = new JTextField();
                nextChapterBtnField.setBounds(0, 100, 250, 20);
                panel.add(nextChapterBtnField);

                JOptionPane.showConfirmDialog(chapterLinkPane, panel, "Chapter-To-Chapter", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (!((firstChapterField.getText().isEmpty() || firstChapterField.getText() == null) &&
                        (lastChapterField.getText().isEmpty() || lastChapterField.getText() == null) &&
                        (nextChapterBtnField.getText().isEmpty() || nextChapterBtnField.getText() == null))) {
                    chapterToChapterArgs[0] = firstChapterField.getText();
                    chapterToChapterArgs[1] = lastChapterField.getText();
                    chapterToChapterArgs[2] = nextChapterBtnField.getText();
                }
            } else {
                linkSelectTabbedPane.addTab("Link select", null, manLinkSelect, null);
                manChapterListURL.setEnabled(true);
                manCheckInvertOrder.setEnabled(true);
            }
        });
        chapterLinkPane.add(manLinkToLink);

        JPanel textSelectPane = new JPanel();
        textSelectPane.setBounds(10, 314, 557, 78);
        textSelectPane.setBorder(BorderFactory.createTitledBorder("Chapter text select"));
        manualPane.add(textSelectPane);
        textSelectPane.setLayout(null);

        JLabel lblChapterContainerSelector = new JLabel("Chapter container selector:");
        lblChapterContainerSelector.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblChapterContainerSelector.setBounds(20, 28, 153, 25);
        lblChapterContainerSelector.setToolTipText(toolTipStyle
                + "Input chapter wrapping <div> selector following jsoup conventions. For example: .fr-view/.chapter-text etc for <div> class names. #mw-content-text/#chapter-wrapper for <div> id names. More info on jsoup.org</p></html>");

        textSelectPane.add(lblChapterContainerSelector);

        manChapterContainer = new JTextField();
        manChapterContainer.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manChapterContainer.setBounds(177, 30, 200, 23);
        textSelectPane.add(manChapterContainer);
        manChapterContainer.setColumns(10);


        manProgressBar = new JProgressBar();
        manProgressBar.setBounds(15, 560, 430, 27);
        manualPane.add(manProgressBar);

        JButton manGrabChaptersBtn = new JButton("Grab Chapters");
        manGrabChaptersBtn.setFocusPainted(false);
        manGrabChaptersBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manGrabChaptersBtn.setBounds(455, 560, 112, 27);
        manualPane.add(manGrabChaptersBtn);

        JLabel lblSaveLocation = new JLabel("Save directory:");
        lblSaveLocation.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblSaveLocation.setBounds(20, 512, 118, 25);
        manualPane.add(lblSaveLocation);

        manSaveLocation = new JTextField();
        manSaveLocation.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manSaveLocation.setBounds(126, 514, 319, 25);
        manualPane.add(manSaveLocation);
        manSaveLocation.setColumns(10);

        JButton btnManBrowse = new JButton("Browse...");
        btnManBrowse.setFocusPainted(false);
        btnManBrowse.setFont(new Font("Tahoma", Font.PLAIN, 11));
        btnManBrowse.addActionListener(arg0 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose destination directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manSaveLocation.setText(chooser.getSelectedFile().toString());
            }
        });
        btnManBrowse.setBounds(455, 513, 112, 27);
        manualPane.add(btnManBrowse);

        JSeparator separator = new JSeparator();
        separator.setBounds(15, 549, 550, 5);
        manualPane.add(separator);

        JPanel manOptionPane = new JPanel();
        manOptionPane.setBounds(10, 394, 557, 95);
        manualPane.add(manOptionPane);
        manOptionPane.setLayout(null);
        manOptionPane.setBorder(BorderFactory.createTitledBorder("Option select"));

        manCreateToc = new JCheckBox("Create ToC");
        manCreateToc.setToolTipText(
                "<html><p width=\"300\">Will create a \"Table of Contents\" file which can be used to convert all chapter files into a single epub file in calibre.</p></html>");
        manCreateToc.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manCreateToc.setFocusPainted(false);
        manCreateToc.setSelected(true);
        manCreateToc.setBounds(6, 20, 81, 23);
        manOptionPane.add(manCreateToc);

        manUseNumeration = new JCheckBox("Chapter numeration");
        manUseNumeration.setToolTipText(
                "<html><p width=\"300\">Will add a chapter number in front of the chapter name to keep them in order when converting.</p></html>");
        manUseNumeration.setFocusPainted(false);
        manUseNumeration.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manUseNumeration.setBounds(6, 40, 121, 23);
        manOptionPane.add(manUseNumeration);

        manCheckInvertOrder = new JCheckBox("Invert chapter order");
        manCheckInvertOrder.setFocusPainted(false);
        manCheckInvertOrder.setToolTipText(
                "<html><p width=\"300\">Invert the chapter order and download the last chapter first. Useful if sites list the highest chapter at the top.</p></html>");
        manCheckInvertOrder.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manCheckInvertOrder.setBounds(6, 60, 139, 23);
        manOptionPane.add(manCheckInvertOrder);

        manGetImages = new JCheckBox("Get images");
        manGetImages.setFocusPainted(false);
        manGetImages.setToolTipText(
                "<html><p width=\"300\">Download and display images.</p></html>");
        manGetImages.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manGetImages.setBounds(151, 20, 150, 23);
        manOptionPane.add(manGetImages);

        JButton manRemoveTagsBtn = new JButton("Blacklisted Tags");
        manRemoveTagsBtn.setFocusPainted(false);
        manRemoveTagsBtn.setBounds(405, 15, 110, 24);
        manRemoveTagsBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        manRemoveTagsBtn.addActionListener(arg0 -> {
            String newBlacklistedTags = JOptionPane.showInputDialog(manualPane,
                    "Enter tags to be removed from the chapter container separated by \",\" \n" +
                            "Example:  \n a,div.someClass.2ndClass,script#IDname", "Blacklisted Tags", JOptionPane.PLAIN_MESSAGE);
            if (!(newBlacklistedTags == null || newBlacklistedTags.isEmpty())) {
                blacklistedTags.addAll(Arrays.asList(newBlacklistedTags.split(",")));
                manShowTagsBtn.setEnabled(true);
            }
        });
        manOptionPane.add(manRemoveTagsBtn);

        ImageIcon manShowTagsIcon = new ImageIcon(getClass().getResource("/images/list_icon.png"));
        manShowTagsBtn = new JButton(manShowTagsIcon);
        manShowTagsBtn.setFocusPainted(false);
        manShowTagsBtn.setBorder(BorderFactory.createEmptyBorder());
        manShowTagsBtn.setContentAreaFilled(false);
        manShowTagsBtn.setEnabled(false);
        manShowTagsBtn.setBounds(522, 15, 25, 25);
        manShowTagsBtn.addActionListener(arg0 -> {
            DefaultListModel<String> tempListModel = new DefaultListModel<>();
            JList<String> tempJList = new JList<>(tempListModel);

            JScrollPane manTagScrollPane = new JScrollPane(tempJList);
            manTagScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            manTagScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            manTagScrollPane.setBounds(0, 0, 532, 180);

            for (String alreadyBlacklistedTags : blacklistedTags) {
                tempListModel.addElement(alreadyBlacklistedTags);
            }

            JButton removeBtn = new JButton("Remove");
            removeBtn.addActionListener(arg1 -> {
                if (!tempListModel.isEmpty()) {
                    int[] indices = tempJList.getSelectedIndices();
                    for (int i = indices.length - 1; i >= 0; i--) {
                        tempListModel.removeElementAt(indices[i]);
                        blacklistedTags.remove(indices[i]);
                    }
                }
                if (tempListModel.isEmpty()) {
                    manShowTagsBtn.setEnabled(false);
                }
            });
            JButton[] buttons = {removeBtn};
            JOptionPane.showOptionDialog(null, manTagScrollPane, "Tags to be removed", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, buttons[0]);
        });
        manOptionPane.add(manShowTagsBtn);

        JLabel manWaitTimeLbl = new JLabel("Wait time:");
        manWaitTimeLbl.setToolTipText(
                "<html><p width=\"300\">Time in miliseconds to wait before each chapter grab. (1000 for 1 second) Please select an appropriate wait time for the host.</p></html>");
        manWaitTimeLbl.setBounds(414, 66, 66, 14);
        manOptionPane.add(manWaitTimeLbl);

        manWaitTime = new JTextField();
        manWaitTime.setText("0");
        manWaitTime.setHorizontalAlignment(SwingConstants.CENTER);
        manWaitTime.setColumns(10);
        manWaitTime.setBounds(481, 63, 66, 20);
        manOptionPane.add(manWaitTime);

        //Chapter checker
        JPanel checkerPane = new JPanel();
        checkerPane.setLayout(null);
        //Only display the checker tab if System Tray is supported on the OS
        if (SystemTray.isSupported()) tabbedPane.addTab("Checker", null, checkerPane, null);

        JPanel checkChapterPane = new JPanel();
        checkChapterPane.setBounds(10, 5, 557, 273);
        checkerPane.add(checkChapterPane);
        checkChapterPane.setBorder(BorderFactory.createTitledBorder("Get notified when a new chapter releases"));
        checkChapterPane.setLayout(null);

        JLabel checkHost = new JLabel("Novel URLs:");
        checkChapterPane.add(checkHost);
        checkHost.setBounds(12, 21, 116, 25);
        checkHost.setFont(new Font("Tahoma", Font.PLAIN, 13));

        chapterLinkPane.add(checkList);
        checkList.setBackground(Color.WHITE);
        checkList.setVisibleRowCount(-1);
        checkList.setFixedCellWidth(512);
        checkList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        checkList.setFont(new Font("Tahoma", Font.BOLD, 12));

        JScrollPane scrollPane_2 = new JScrollPane(checkList);
        scrollPane_2.setBounds(10, 45, 532, 179);
        checkChapterPane.add(scrollPane_2);
        scrollPane_2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane_2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        checkAddNewEntryBtn = new JButton("Add");
        checkAddNewEntryBtn.setFocusPainted(false);
        checkAddNewEntryBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkAddNewEntryBtn.setBounds(334, 15, 100, 26);
        checkChapterPane.add(checkAddNewEntryBtn);
        checkAddNewEntryBtn.addActionListener(arg0 -> {
            String host = (String) JOptionPane.showInputDialog(checkChapterPane,
                    "Pick host:", "Add a Novel to check", JOptionPane.PLAIN_MESSAGE, favicon, Novel.websites, "wuxiaworld");
            String checkUrl = JOptionPane.showInputDialog(checkChapterPane,
                    "Input Table of Contents URL:", "Add a Novel to check", JOptionPane.PLAIN_MESSAGE);
            if (!(checkUrl == null) && !(host == null)) {
                if (!host.isEmpty() && !checkUrl.isEmpty()) {
                    host = host.toLowerCase().replace(" ", "");
                    listModelCheckerLinks.addElement("[" + checkUrl + "]");
                    chapterChecker.hosts.add(host);
                    chapterChecker.urls.add(checkUrl);
                    checkRemoveEntry.setEnabled(true);
                    checkPollStartBtn.setEnabled(true);
                }
            }
        });
        ImageIcon busyGif = new ImageIcon(getClass().getResource("/images/busy.gif"));
        checkBusyIcon = new JLabel(busyGif);
        checkBusyIcon.setVisible(false);
        checkChapterPane.add(checkBusyIcon);
        checkBusyIcon.setBounds(10, 230, 28, 28);

        checkStatusLbl = new JLabel("Checking active.");
        checkStatusLbl.setVisible(false);
        checkChapterPane.add(checkStatusLbl);
        checkStatusLbl.setBounds(45, 230, 120, 28);

        checkPollStartBtn = new JButton("Start checking");
        checkPollStartBtn.setEnabled(false);
        checkPollStartBtn.setFocusPainted(false);
        checkPollStartBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkPollStartBtn.setBounds(443, 230, 100, 26);
        checkChapterPane.add(checkPollStartBtn);
        checkPollStartBtn.addActionListener(g -> {
            if (chapterChecker.urls.isEmpty() || chapterChecker.hosts.isEmpty()) {
                showPopup("No checkers defined", "warning");
            } else {
                startPolling();
            }
        });

        checkStopPollingBtn = new JButton("Stop checking");
        checkStopPollingBtn.setEnabled(false);
        checkStopPollingBtn.setVisible(false);
        checkStopPollingBtn.setFocusPainted(false);
        checkStopPollingBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkStopPollingBtn.setBounds(443, 230, 100, 26);
        checkChapterPane.add(checkStopPollingBtn);
        checkStopPollingBtn.addActionListener(ga -> stopPolling());

        checkRemoveEntry = new JButton("Remove");
        checkRemoveEntry.setEnabled(false);
        checkRemoveEntry.setFocusPainted(false);
        checkRemoveEntry.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkRemoveEntry.setBounds(443, 15, 100, 26);
        checkChapterPane.add(checkRemoveEntry);
        checkRemoveEntry.addActionListener(gc -> {
            int[] indices = checkList.getSelectedIndices();
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

        JPopupMenu checkPopUp = new JPopupMenu();
        addPopup(checkList, checkPopUp);

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
                showPopup("Checker list is empty", "warning");
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

        JPanel checkLogPane = new JPanel();
        checkLogPane.setVisible(false);
        checkLogPane.setBounds(10, 310, 557, 200);
        checkerPane.add(checkLogPane);
        checkLogPane.setBorder(BorderFactory.createTitledBorder("Log"));
        checkLogPane.setLayout(null);

        checkLogField = new JTextArea();
        checkLogField.setFocusable(false);
        checkLogField.setEditable(false);

        JScrollPane checkLogScrollpane = new JScrollPane(checkLogField);
        checkLogScrollpane.setBounds(10, 25, 532, 159);
        checkLogScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        checkLogScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        checkLogPane.add(checkLogScrollpane);

        checkShowLogBtn = new JButton("+");
        checkShowLogBtn.setFocusPainted(false);
        checkShowLogBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkShowLogBtn.setBounds(11, 281, 40, 26);
        checkerPane.add(checkShowLogBtn);
        checkShowLogBtn.addActionListener(gc -> {
            checkLogPane.setVisible(true);
            checkHideLogBtn.setVisible(true);
            checkHideLogBtn.setEnabled(true);
            checkShowLogBtn.setVisible(false);
            checkShowLogBtn.setEnabled(false);
        });

        checkHideLogBtn = new JButton("-");
        checkHideLogBtn.setVisible(false);
        checkHideLogBtn.setEnabled(false);
        checkHideLogBtn.setFocusPainted(false);
        checkHideLogBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkHideLogBtn.setBounds(11, 281, 40, 26);
        checkerPane.add(checkHideLogBtn);
        checkHideLogBtn.addActionListener(gc -> {
            checkLogPane.setVisible(false);
            checkShowLogBtn.setVisible(true);
            checkShowLogBtn.setEnabled(true);
            checkHideLogBtn.setVisible(false);
            checkHideLogBtn.setEnabled(false);
        });

        checkDefaultFileLabel = new JLabel("No default checker file defined.");
        checkDefaultFileLabel.setToolTipText(
                "<html><p width=\"300\">Save your checker file as \"default.txt\" and it will be loaded and started on startup.</p></html>");
        checkDefaultFileLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        checkDefaultFileLabel.setBounds(11, 570, 200, 26);
        checkerPane.add(checkDefaultFileLabel);


        //updater
        updatePane = new JPanel();
        updatePane.setLayout(null);

        JPanel updateAreaPane = new JPanel();
        updateAreaPane.setBounds(10, 5, 557, 280);
        updatePane.add(updateAreaPane);
        updateAreaPane.setBorder(BorderFactory.createTitledBorder("Update Novel-Grabber"));
        updateAreaPane.setLayout(null);

        updateStatusLbl = new JLabel();
        updateAreaPane.add(updateStatusLbl);
        updateStatusLbl.setBounds(13, 30, 420, 28);

        JButton updateUpdateBtn = new JButton("Update now");
        updateUpdateBtn.setFocusPainted(false);
        updateUpdateBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
        updateUpdateBtn.setBounds(443, 32, 100, 26);
        updateAreaPane.add(updateUpdateBtn);
        updateUpdateBtn.addActionListener(arg0 -> Executors.newSingleThreadExecutor().execute(updater::updateJar));

        JSeparator updateSeperator = new JSeparator();
        updateAreaPane.add(updateSeperator);
        updateSeperator.setBounds(13, 70, 530, 5);

        updateLogLbl = new JLabel();
        updateLogLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        updateAreaPane.add(updateLogLbl);
        updateLogLbl.setBounds(13, 73, 200, 28);

        updateLogField = new JTextArea();
        updateLogField.setWrapStyleWord(true);
        updateLogField.setLineWrap(true);
        updateLogField.setFocusable(false);
        updateLogField.setEditable(false);

        updateLogScrollpane = new JScrollPane(updateLogField);
        updateLogScrollpane.setBounds(12, 100, 532, 159);
        updateAreaPane.add(updateLogScrollpane);

        updateProcessLbl = new JLabel();
        updateProcessLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
        updateProcessLbl.setBounds(11, 570, 200, 26);
        updatePane.add(updateProcessLbl);

        // manual chapter download
        manGrabChaptersBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            manGrabChaptersBtn.setEnabled(false);
            // Chapter-To-Chapter
            // input validation
            if (manLinkToLink.isSelected()) {
                if (manSaveLocation.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frmNovelGrabber, "Save directory field is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manSaveLocation.requestFocusInWindow();
                } else if (manChapterContainer.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frmNovelGrabber, "Chapter container selector is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterContainer.requestFocusInWindow();
                } else if (manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time cannot be empty.", "warning");
                } else if (!manWaitTime.getText().matches("\\d+") && !manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time must contain numbers.", "warning");
                } else if ((!manSaveLocation.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty())
                        && (!manWaitTime.getText().isEmpty())) {
                    try {
                        manFetchChapters processChapters = new manFetchChapters("chapterToChapter", blacklistedTags);
                        // Exception handling
                    } catch (NullPointerException | IllegalArgumentException err) {
                        appendText("manual", err.getMessage());
                        err.printStackTrace();
                    } finally {
                        manProgressBar.setStringPainted(false);
                        manProgressBar.setValue(0);
                    }
                }
                manGrabChaptersBtn.setEnabled(true);
                // input validation
            } else {
                if (manChapterListURL.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frmNovelGrabber, "URL field is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterListURL.requestFocusInWindow();
                } else if (manSaveLocation.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frmNovelGrabber, "Save directory field is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manSaveLocation.requestFocusInWindow();
                } else if (manChapterContainer.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frmNovelGrabber, "Chapter container selector is empty.", "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    manChapterContainer.requestFocusInWindow();
                } else if (manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time cannot be empty.", "warning");
                } else if (!manWaitTime.getText().matches("\\d+") && !manWaitTime.getText().isEmpty()) {
                    showPopup("Wait time must contain numbers.", "warning");
                } else if ((!manSaveLocation.getText().isEmpty())
                        && (!manChapterListURL.getText().isEmpty())
                        && (!manChapterContainer.getText().isEmpty())
                        && (!manWaitTime.getText().isEmpty())) {
                    try {
                        manProgressBar.setStringPainted(true);
                        manFetchChapters processChapters = new manFetchChapters("chaptersFromList", blacklistedTags);
                        // Exception handling
                    } catch (NullPointerException | IllegalArgumentException err) {
                        appendText("manual", err.getMessage());
                        err.printStackTrace();
                    } finally {
                        manProgressBar.setStringPainted(false);
                        manProgressBar.setValue(0);
                    }
                }
                manGrabChaptersBtn.setEnabled(true);
            }
        }));

        // Get chapter number
        getChapterBtn.addActionListener(e -> Executors.newSingleThreadExecutor().execute(() -> {
            if (singleChapterLink.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frmNovelGrabber, "URL field is empty.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                singleChapterLink.requestFocusInWindow();
            } else if (!singleChapterLink.getText().isEmpty()) {
                try {
                    getChapterBtn.setEnabled(false);
                    autoFetchChapters.getChapterNumber(singleChapterHostSelection.getSelectedItem().toString().toLowerCase().replace(" ", ""), singleChapterLink.getText());
                } catch (NullPointerException | IllegalArgumentException err) {
                    appendText("auto", err.getMessage());
                    err.printStackTrace();
                } finally {
                    getChapterBtn.setEnabled(true);
                }
            }
        }));

        //Multiple chapters
        grabChapters.addActionListener(arg0 -> Executors.newSingleThreadExecutor().execute(() -> {
            grabChapters.setEnabled(false);
            // input validation
            if (chapterListURL.getText().isEmpty()) {
                showPopup("URL field is empty.", "warning");
                chapterListURL.requestFocusInWindow();
            } else if (saveLocation.getText().isEmpty()) {
                showPopup("Save directory field is empty.", "warning");
                saveLocation.requestFocusInWindow();
            } else if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && ((firstChapter.getText().isEmpty()) || (lastChapter.getText().isEmpty()))) {
                showPopup("No chapter range defined.", "warning");
            } else if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && (!firstChapter.getText().matches("\\d+") || !lastChapter.getText().matches("\\d+"))) {
                showPopup("Chapter range must contain numbers.", "warning");
            } else if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && ((Integer.parseInt(firstChapter.getText()) < 1)
                    || (Integer.parseInt(lastChapter.getText()) < 1))) {
                showPopup("Chapter numbers can't be lower than 1.", "warning");
            } else if ((!chapterAllCheckBox.isSelected()) && (!toLastChapter.isSelected())
                    && (Integer.parseInt(lastChapter.getText()) < Integer.parseInt(firstChapter.getText()))) {
                showPopup("Last chapter can't be lower than first chapter.", "warning");
            } else if ((!chapterAllCheckBox.isSelected()) && (toLastChapter.isSelected())
                    && (!firstChapter.getText().matches("\\d+"))) {
                showPopup("First chapter selection must contain numbers.", "warning");
            } else if ((!chapterAllCheckBox.isSelected()) && (toLastChapter.isSelected())
                    && (!firstChapter.getText().isEmpty() && Integer.parseInt(firstChapter.getText()) < 1)) {
                showPopup("First chapter number can't be lower than 1.", "warning");
            } else if (waitTime.getText().isEmpty()) {
                showPopup("Wait time cannot be empty.", "warning");
            } else if (!waitTime.getText().matches("\\d+") && !waitTime.getText().isEmpty()) {
                showPopup("Wait time must contain numbers.", "warning");
            } else if ((!saveLocation.getText().isEmpty())
                    && (!chapterListURL.getText().isEmpty())
            ) {
                // Chapter grabbing
                try {
                    autoFetchChapters processChapters = new autoFetchChapters();
                } catch (NullPointerException | IllegalArgumentException err) {
                    appendText("auto", err.getMessage());
                    err.printStackTrace();
                } finally {
                    progressBar.setStringPainted(false);
                    progressBar.setValue(0);
                }
            }
            grabChapters.setEnabled(true);
        }));
    }
}
