import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ToolTipManager;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

/*
 *  Window display and handling.
 */
public class NovelGrabberGUI {
	private final String versionNumber = "v1.1.01";
	private JFrame frmNovelGrabber;
	private JTextField chapterListURL;
	private JTextField destinationFolder;
	private JComboBox<String> websiteSelection1;
	private JComboBox<String> websiteSelection2;
	private JComboBox<String> fileTypeComboBox;
	private static JTextArea logArea;
	private static JProgressBar progressBar;
	private static JProgressBar manProgressBar;
	private JTextField chapterURL;
	public static final String NL = System.getProperty("line.separator");
	public static DefaultListModel listModelChapterLinks = new DefaultListModel();
	public static JList chapterLinkList = new JList(listModelChapterLinks);
	private static String[] fileTypes = { "HTML", "TXT" };
	private JTextField firstChapter;
	private JTextField lastChapter;
	private JTextField manChapterListURL;
	private JTextField manDestinationFolder;
	private JComboBox manFileType;
	private JCheckBox manCreateToc = new JCheckBox("Create ToC");
	private JCheckBox manChapterNumeration = new JCheckBox("Chapter numeration");
	private JTextField manChapterContainer;
	private JTextField manSentenceSelector;

	/**
	 * Launch the application
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					NovelGrabberGUI window = new NovelGrabberGUI();
					window.frmNovelGrabber.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public NovelGrabberGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		// tooltip style
		int dismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
		dismissDelay = Integer.MAX_VALUE;
		ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);
		UIManager.put("ToolTip.background", new ColorUIResource(Color.white));
		String toolTipStyle = "<html><p width=\"300\">";

		frmNovelGrabber = new JFrame();
		frmNovelGrabber.setResizable(false);
		frmNovelGrabber.setTitle("Novel-Grabber " + versionNumber);
		frmNovelGrabber.setBounds(100, 100, 588, 563);
		frmNovelGrabber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNovelGrabber.getContentPane().setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 582, 534);
		frmNovelGrabber.getContentPane().add(tabbedPane);

		JPanel automaticPane = new JPanel();
		automaticPane.setLayout(null);
		tabbedPane.addTab("Automatic", null, automaticPane, null);

		JPanel allChapterPane = new JPanel();
		allChapterPane.setBounds(10, 5, 557, 398);
		automaticPane.add(allChapterPane);
		allChapterPane.setBorder(BorderFactory.createTitledBorder("Get multiple chapters"));
		allChapterPane.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 361, 409, 25);
		allChapterPane.add(progressBar);
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		progressBar.setForeground(new Color(0, 128, 128));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setString("");

		JButton getAllChaptersBtn = new JButton("Grab chapters");
		getAllChaptersBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getAllChaptersBtn.setBounds(429, 361, 113, 26);
		allChapterPane.add(getAllChaptersBtn);

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

		JLabel lblDestinationDirectory = new JLabel("Save directory:");
		lblDestinationDirectory.setBounds(10, 80, 103, 25);
		allChapterPane.add(lblDestinationDirectory);
		lblDestinationDirectory.setFont(new Font("Tahoma", Font.PLAIN, 11));

		destinationFolder = new JTextField();
		destinationFolder.setBounds(152, 80, 294, 25);
		allChapterPane.add(destinationFolder);
		destinationFolder.setToolTipText("");
		destinationFolder.setColumns(10);

		websiteSelection1 = new JComboBox(Novel.websites);
		websiteSelection1.setBounds(152, 49, 390, 25);
		allChapterPane.add(websiteSelection1);

		JLabel lblNewLabel = new JLabel("Host website:");
		lblNewLabel.setBounds(10, 49, 86, 25);
		allChapterPane.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));

		JButton btnNewButton = new JButton("Browse...");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose destination directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					destinationFolder.setText(chooser.getSelectedFile().toString());
				}
			}

		});
		btnNewButton.setBounds(456, 79, 86, 27);
		allChapterPane.add(btnNewButton);

		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setBounds(-22, 11, 235, 41);
		allChapterPane.add(logArea);

		JScrollPane scrollPane = new JScrollPane(logArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 221, 532, 134);
		allChapterPane.add(scrollPane);

		JPanel chapterSelect = new JPanel();
		chapterSelect.setBounds(10, 109, 532, 45);
		chapterSelect.setBorder(BorderFactory.createTitledBorder("Select chapters to download"));
		allChapterPane.add(chapterSelect);
		chapterSelect.setLayout(null);

		JCheckBox chapterAllCheckBox = new JCheckBox("All");
		chapterAllCheckBox.setBounds(101, 12, 62, 23);
		chapterSelect.add(chapterAllCheckBox);
		chapterAllCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (chapterAllCheckBox.isSelected() == true) {
					firstChapter.setEnabled(false);
					lastChapter.setEnabled(false);
				}
				if (chapterAllCheckBox.isSelected() == false) {
					firstChapter.setEnabled(true);
					lastChapter.setEnabled(true);
				}
			}
		});

		JLabel lblChapter = new JLabel("Chapter range:");
		lblChapter.setBounds(210, 16, 113, 14);
		chapterSelect.add(lblChapter);

		firstChapter = new JTextField();
		firstChapter.setBounds(325, 13, 60, 20);
		firstChapter.setColumns(10);
		firstChapter.setHorizontalAlignment(JTextField.CENTER);
		chapterSelect.add(firstChapter);

		JLabel lblTo = new JLabel("-");
		lblTo.setBounds(396, 11, 6, 20);
		lblTo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		chapterSelect.add(lblTo);

		lastChapter = new JTextField();
		lastChapter.setBounds(413, 13, 60, 20);
		lastChapter.setColumns(10);
		lastChapter.setHorizontalAlignment(JTextField.CENTER);
		chapterSelect.add(lastChapter);

		JPanel optionSelect = new JPanel();
		optionSelect.setBounds(10, 165, 532, 51);
		optionSelect.setBorder(BorderFactory.createTitledBorder("Option select"));
		allChapterPane.add(optionSelect);
		optionSelect.setLayout(null);

		JCheckBox createTocCheckBox = new JCheckBox("Create ToC");
		createTocCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		createTocCheckBox.setBounds(153, 15, 81, 23);
		optionSelect.add(createTocCheckBox);
		createTocCheckBox.setToolTipText(toolTipStyle
				+ "Will create a \"Table of Contents\" file which can be used to convert all chapter files into a single epub file in calibre.</p></html>");

		fileTypeComboBox = new JComboBox(fileTypes);
		fileTypeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		fileTypeComboBox.setBounds(77, 16, 66, 20);
		optionSelect.add(fileTypeComboBox);

		JLabel fileTypeLabel = new JLabel("File output:");
		fileTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		fileTypeLabel.setBounds(10, 14, 66, 25);
		optionSelect.add(fileTypeLabel);

		JCheckBox chapterNumerationCheckBox = new JCheckBox("Chapter numeration");
		chapterNumerationCheckBox.setToolTipText(toolTipStyle
				+ "Will add a chapter number infront of the chapter name. Helpful for ordering chapters which don't have a chapter number in their title.</p></html>");
		chapterNumerationCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		chapterNumerationCheckBox.setBounds(236, 15, 121, 23);
		optionSelect.add(chapterNumerationCheckBox);

		JCheckBox checkInvertOrder = new JCheckBox("Invert chapter order");
		checkInvertOrder.setToolTipText(
				"<html><p width=\"300\">Invert  the chapter oder and download the last chapter first. Useful if sites list the highest chapter at the top</p></html>");
		checkInvertOrder.setFont(new Font("Tahoma", Font.PLAIN, 11));
		checkInvertOrder.setBounds(359, 15, 139, 23);
		optionSelect.add(checkInvertOrder);

		JPanel singleChapterPane = new JPanel();
		singleChapterPane.setBounds(10, 406, 557, 95);
		automaticPane.add(singleChapterPane);
		singleChapterPane.setBorder(BorderFactory.createTitledBorder("Get single chapter"));
		singleChapterPane.setLayout(null);

		JButton getChapterBtn = new JButton("Grab chapter");
		getChapterBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getChapterBtn.setBounds(429, 59, 113, 27);
		singleChapterPane.add(getChapterBtn);

		chapterURL = new JTextField();
		chapterURL.setBounds(133, 25, 409, 25);
		singleChapterPane.add(chapterURL);
		chapterURL.setColumns(10);

		JLabel lblchapterURL = new JLabel("Chapter URL:");
		lblchapterURL.setBounds(10, 24, 100, 25);
		singleChapterPane.add(lblchapterURL);
		lblchapterURL.setFont(new Font("Tahoma", Font.PLAIN, 11));

		JLabel label = new JLabel("Host website:");
		label.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label.setBounds(10, 59, 73, 25);
		singleChapterPane.add(label);

		websiteSelection2 = new JComboBox(Novel.websites);
		websiteSelection2.setBounds(133, 59, 286, 25);
		singleChapterPane.add(websiteSelection2);

		// Manual Tab
		JPanel manualPane = new JPanel();
		tabbedPane.addTab("Manual", null, manualPane, null);
		manualPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 5, 557, 284);
		manualPane.add(panel);
		panel.setBorder(BorderFactory.createTitledBorder("Chapter links select"));
		panel.setLayout(null);

		JLabel lblManualToc = new JLabel("Table Of Contents URL");
		lblManualToc.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblManualToc.setBounds(10, 17, 158, 25);
		panel.add(lblManualToc);

		manChapterListURL = new JTextField();
		manChapterListURL.setBounds(178, 14, 260, 25);
		panel.add(manChapterListURL);
		manChapterListURL.setColumns(10);

		chapterLinkList.setVisibleRowCount(-1);
		chapterLinkList.setLayoutOrientation(JList.VERTICAL_WRAP);
		chapterLinkList.setFixedCellWidth(268);
		panel.add(chapterLinkList);

		JButton removeLinks = new JButton("Remove links");
		removeLinks.setFont(new Font("Tahoma", Font.PLAIN, 11));
		removeLinks.setEnabled(false);
		removeLinks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] indices = chapterLinkList.getSelectedIndices();
				for (int i = indices.length - 1; i >= 0; i--) {
					listModelChapterLinks.removeElementAt(indices[i]);
					fetchChapters.chapterUrl.remove(indices[i]);
				}
			}
		});
		removeLinks.setBounds(448, 55, 99, 25);
		panel.add(removeLinks);

		JScrollPane scrollPane_1 = new JScrollPane(chapterLinkList);
		scrollPane_1.setBounds(10, 86, 537, 187);
		panel.add(scrollPane_1);

		JLabel lblLinkSelect = new JLabel("Select links to be removed:");
		lblLinkSelect.setBounds(10, 60, 227, 25);
		panel.add(lblLinkSelect);

		JButton retrieveLinks = new JButton("Retrieve Links");
		retrieveLinks.setFont(new Font("Tahoma", Font.PLAIN, 11));
		retrieveLinks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (manChapterListURL.getText().isEmpty() == true) {
					JOptionPane.showMessageDialog(frmNovelGrabber, "URL field is empty.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					manChapterListURL.requestFocusInWindow();
				}
				if (!manChapterListURL.getText().isEmpty() == true) {
					try {
						fetchChapters.chapterUrl.clear();
						listModelChapterLinks.clear();
						progressBar.setStringPainted(true);
						fetchChapters.retrieveChapterLinks(manChapterListURL.getText());
					} catch (IllegalArgumentException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (FileNotFoundException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (NullPointerException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (IOException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} finally {
						removeLinks.setEnabled(true);
					}
				}

			}
		});
		retrieveLinks.setBounds(448, 13, 99, 27);
		panel.add(retrieveLinks);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 288, 557, 78);
		panel_1.setBorder(BorderFactory.createTitledBorder("Chapter text select"));
		manualPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel lblChapterContainerSelector = new JLabel("Chapter container selector:");
		lblChapterContainerSelector.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblChapterContainerSelector.setBounds(20, 11, 153, 25);
		panel_1.add(lblChapterContainerSelector);

		JLabel lblSentenceSelector = new JLabel("Sentence selector:");
		lblSentenceSelector.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSentenceSelector.setBounds(20, 42, 120, 25);
		panel_1.add(lblSentenceSelector);

		manChapterContainer = new JTextField();
		manChapterContainer.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manChapterContainer.setToolTipText(toolTipStyle
				+ "Input chapter wrapping <div> selector following jsoup conventions. For example: .fr-view/.chapter-text etc for <div> class names. #mw-content-text/#chapter-wrapper for <div> id names. More info on jsoup.org</p></html>");
		manChapterContainer.setBounds(177, 13, 86, 20);
		panel_1.add(manChapterContainer);
		manChapterContainer.setColumns(10);

		manSentenceSelector = new JTextField();
		manSentenceSelector.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manSentenceSelector.setToolTipText(toolTipStyle
				+ "Input html sentence wrapping. Usually \"p\" for <p>(paragraphs). Use \"*\" as a wildcard.</p></html>");
		manSentenceSelector.setBounds(177, 44, 86, 20);
		panel_1.add(manSentenceSelector);
		manSentenceSelector.setColumns(10);

		manProgressBar = new JProgressBar();
		manProgressBar.setBounds(15, 470, 430, 27);
		manualPane.add(manProgressBar);

		JButton btnManGrabChapters = new JButton("Grab Chapters");
		btnManGrabChapters.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnManGrabChapters.setBounds(455, 470, 112, 27);
		manualPane.add(btnManGrabChapters);

		JLabel lblSaveLocation = new JLabel("Save directory:");
		lblSaveLocation.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblSaveLocation.setBounds(20, 422, 118, 25);
		manualPane.add(lblSaveLocation);

		manDestinationFolder = new JTextField();
		manDestinationFolder.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manDestinationFolder.setBounds(126, 424, 319, 25);
		manualPane.add(manDestinationFolder);
		manDestinationFolder.setColumns(10);

		JButton btnManBrowse = new JButton("Browse...");
		btnManBrowse.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnManBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Choose destination directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					manDestinationFolder.setText(chooser.getSelectedFile().toString());
				}
			}

		});
		btnManBrowse.setBounds(455, 423, 112, 27);
		manualPane.add(btnManBrowse);

		JSeparator separator = new JSeparator();
		separator.setBounds(15, 459, 550, 5);
		manualPane.add(separator);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 365, 557, 52);
		manualPane.add(panel_2);
		panel_2.setLayout(null);
		panel_2.setBorder(BorderFactory.createTitledBorder("Option select"));

		manCreateToc.setToolTipText(
				"<html><p width=\"300\">Will create a \"Table of Contents\" file which can be used to convert all chapter files into a single epub file in calibre.</p></html>");
		manCreateToc.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manCreateToc.setBounds(153, 15, 81, 23);
		panel_2.add(manCreateToc);

		JComboBox manFileType = new JComboBox(fileTypes);
		manFileType.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manFileType.setBounds(77, 16, 66, 20);
		panel_2.add(manFileType);

		JLabel label_1 = new JLabel("File output:");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_1.setBounds(10, 14, 66, 25);
		panel_2.add(label_1);

		manChapterNumeration.setToolTipText(
				"<html><p width=\"300\">Will add a chapter number infront of the chapter name. Helpful for ordering chapters which don't have a chapter number in their title.</p></html>");
		manChapterNumeration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manChapterNumeration.setBounds(236, 15, 121, 23);
		panel_2.add(manChapterNumeration);

		JCheckBox manCheckInvertOrder = new JCheckBox("Invert chapter order");
		manCheckInvertOrder.setToolTipText(
				"<html><p width=\"300\">Invert the chapter order and download the last chapter first. Useful if sites list the highest chapter at the top.</p></html>");
		manCheckInvertOrder.setFont(new Font("Tahoma", Font.PLAIN, 11));
		manCheckInvertOrder.setBounds(359, 15, 139, 23);
		panel_2.add(manCheckInvertOrder);

		// manual chapter download
		btnManGrabChapters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnManGrabChapters.setEnabled(false);
				// input validation
				if (manChapterListURL.getText().isEmpty() == true) {
					JOptionPane.showMessageDialog(frmNovelGrabber, "URL field is empty.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					manChapterListURL.requestFocusInWindow();
				} else if (manDestinationFolder.getText().isEmpty() == true) {
					JOptionPane.showMessageDialog(frmNovelGrabber, "Save directory field is empty.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					manDestinationFolder.requestFocusInWindow();
				} else if (manChapterContainer.getText().isEmpty() == true) {
					JOptionPane.showMessageDialog(frmNovelGrabber, "Chapter container selector is empty.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					manChapterContainer.requestFocusInWindow();
				} else if (manSentenceSelector.getText().isEmpty() == true) {
					JOptionPane.showMessageDialog(frmNovelGrabber, "Sentence selector is empty.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					manDestinationFolder.requestFocusInWindow();
				} else if ((manFileType.getSelectedItem().toString() == "TXT") && (manCreateToc.isSelected() == true)) {
					JOptionPane.showMessageDialog(frmNovelGrabber,
							"Cannot create Table of Contents page from txt files.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					manDestinationFolder.requestFocusInWindow();
				} else if ((!manDestinationFolder.getText().isEmpty() == true)
						&& (!manChapterListURL.getText().isEmpty() == true)
						&& (!manChapterContainer.getText().isEmpty() == true)
						&& (!manSentenceSelector.getText().isEmpty() == true)) {
					try {
						manProgressBar.setStringPainted(true);
						if (manFileType.getSelectedItem().toString() == "TXT") {
							fetchChapters.manSaveChapters(manDestinationFolder.getText(), ".txt",
									manChapterNumeration.isSelected(), manChapterContainer.getText(),
									manSentenceSelector.getText(), manCheckInvertOrder.isSelected());
						} else {
							fetchChapters.manSaveChapters(manDestinationFolder.getText(), ".html",
									manChapterNumeration.isSelected(), manChapterContainer.getText(),
									manSentenceSelector.getText(), manCheckInvertOrder.isSelected());
						}

						if (manCreateToc.isSelected() == true) {
							fetchChapters.createToc(manDestinationFolder.getText());
						}
						fetchChapters.chapterFileNames.clear();
					} catch (IllegalArgumentException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (FileNotFoundException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (NullPointerException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (IOException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} finally {
						manProgressBar.setStringPainted(false);
						manProgressBar.setValue(0);
					}
				}
				btnManGrabChapters.setEnabled(true);
			}

		});

		// Single Chapter
		getChapterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chapterURL.getText().isEmpty() == true) {
					JOptionPane.showMessageDialog(frmNovelGrabber, "URL field is empty.", "Warning",
							JOptionPane.WARNING_MESSAGE);
					chapterURL.requestFocusInWindow();
				} else if (chapterURL.getText().isEmpty() == false) {
					try {
						progressBar.setStringPainted(true);
						fetchChapters.saveChapter(chapterURL.getText(),
								(websiteSelection2.getSelectedItem().toString()).toLowerCase().replace(" ", ""));
					} catch (IllegalArgumentException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (FileNotFoundException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (NullPointerException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} catch (IOException err) {
						JOptionPane.showMessageDialog(frmNovelGrabber, err, "Error", JOptionPane.ERROR_MESSAGE);
					} finally {
						progressBar.setStringPainted(false);
						progressBar.setValue(0);
					}
				}
			}
		});

		// All Chapters
		getAllChaptersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getAllChaptersBtn.setEnabled(false);
				// input validation
				if (chapterListURL.getText().isEmpty() == true) {
					showPopup("URL field is empty.", "warning");
					chapterListURL.requestFocusInWindow();
				} else if ((fileTypeComboBox.getSelectedItem().toString() == "TXT")
						&& (createTocCheckBox.isSelected() == true)) {
					showPopup("Cannot create a Table of Contents file with .txt file type.", "warning");
				} else if (destinationFolder.getText().isEmpty() == true) {
					showPopup("Save directory field is empty.", "warning");
					destinationFolder.requestFocusInWindow();
				} else if ((chapterAllCheckBox.isSelected() == false)
						&& ((firstChapter.getText().isEmpty() == true) || (lastChapter.getText().isEmpty() == true))) {
					showPopup("No chapter range defined.", "warning");
				} else if ((chapterAllCheckBox.isSelected() == false)
						&& (!firstChapter.getText().matches("\\d+") || !lastChapter.getText().matches("\\d+"))) {
					showPopup("Chapter range must contain numbers.", "warning");
				} else if ((chapterAllCheckBox.isSelected() == false) && ((Integer.parseInt(firstChapter.getText()) < 1)
						|| (Integer.parseInt(lastChapter.getText()) < 1))) {
					showPopup("Chapter numbers can't be lower than 1.", "warning");
				} else if ((chapterAllCheckBox.isSelected() == false)
						&& (Integer.parseInt(lastChapter.getText()) < Integer.parseInt(firstChapter.getText()))) {
					showPopup("Last chapter can't be lower than first chapter.", "warning");
				}
				// grabbing chapter calls
				else if ((destinationFolder.getText().isEmpty() == false)
						&& (chapterListURL.getText().isEmpty() == false)) {
					try {
						progressBar.setStringPainted(true);
						// grabbing all chapters
						if (chapterAllCheckBox.isSelected() == true) {
							if (fileTypeComboBox.getSelectedItem().toString() == "TXT") {
								fetchChapters.getAllChapterLinks(chapterListURL.getText(), destinationFolder.getText(),
										(websiteSelection1.getSelectedItem().toString()).toLowerCase().replace(" ", ""),
										".txt", chapterNumerationCheckBox.isSelected(), checkInvertOrder.isSelected());
							} else {
								fetchChapters.getAllChapterLinks(chapterListURL.getText(), destinationFolder.getText(),
										(websiteSelection1.getSelectedItem().toString()).toLowerCase().replace(" ", ""),
										".html", chapterNumerationCheckBox.isSelected(), checkInvertOrder.isSelected());
							}
							if (createTocCheckBox.isSelected() == true) {
								fetchChapters.createToc(destinationFolder.getText());
							}
							fetchChapters.chapterFileNames.clear();
						}
						// grabbing chapters from selected range
						if ((chapterAllCheckBox.isSelected() == false) && (firstChapter.getText().isEmpty() == false
								|| lastChapter.getText().isEmpty() == false)) {
							if (fileTypeComboBox.getSelectedItem().toString() == "TXT") {
								fetchChapters
										.getChapterRangeLinks(chapterListURL.getText(), destinationFolder.getText(),
												(websiteSelection1.getSelectedItem().toString()).toLowerCase()
														.replace(" ", ""),
												Integer.parseInt(firstChapter.getText()),
												Integer.parseInt(lastChapter.getText()), ".txt",
												chapterNumerationCheckBox.isSelected(), checkInvertOrder.isSelected());
							} else {
								fetchChapters
										.getChapterRangeLinks(chapterListURL.getText(), destinationFolder.getText(),
												(websiteSelection1.getSelectedItem().toString()).toLowerCase()
														.replace(" ", ""),
												Integer.parseInt(firstChapter.getText()),
												Integer.parseInt(lastChapter.getText()), ".html",
												chapterNumerationCheckBox.isSelected(), checkInvertOrder.isSelected());
							}
							if (createTocCheckBox.isSelected() == true && fetchChapters.error == false) {
								fetchChapters.createToc(destinationFolder.getText());
							}
							fetchChapters.chapterFileNames.clear();
						}

					}
					// exception handling
					catch (IllegalArgumentException err) {
						showPopup(err.toString(), "error");
					} catch (FileNotFoundException err) {
						showPopup(err.toString(), "error");
					} catch (NullPointerException err) {
						showPopup(err.toString(), "error");
					} catch (IOException err) {
						showPopup(err.toString(), "error");
					} finally {
						progressBar.setStringPainted(false);
						progressBar.setValue(0);
					}
				}
				getAllChaptersBtn.setEnabled(true);
			}
		});

	}

	public static void appendText(String log) {
		logArea.append(log + NL);
		logArea.update(logArea.getGraphics());
	}

	public void showPopup(String errorMsg, String kind) {
		switch (kind) {
		case "warning":
			JOptionPane.showMessageDialog(frmNovelGrabber, errorMsg, "Warning", JOptionPane.WARNING_MESSAGE);
			break;
		case "error":
			JOptionPane.showMessageDialog(frmNovelGrabber, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
			break;
		}
	}

	public static void updateProgress(String progressBarSelect, int i) {
		switch (progressBarSelect) {
		case "auto":
			progressBar.setValue(progressBar.getValue() + i);
			if (progressBar.getValue() < progressBar.getMaximum()) {
				progressBar.setString((progressBar.getValue() + i) + " / " + progressBar.getMaximum());
			}
			progressBar.update(progressBar.getGraphics());
			break;
		case "manual":
			manProgressBar.setValue(manProgressBar.getValue() + i);
			if (manProgressBar.getValue() < manProgressBar.getMaximum()) {
				manProgressBar.setString((manProgressBar.getValue() + i) + " / " + manProgressBar.getMaximum());
			}
			manProgressBar.update(manProgressBar.getGraphics());
			break;
		}
	}

	public static void setMaxProgress(String progressBarSelect, int i) {
		switch (progressBarSelect) {
		case "auto":
			progressBar.setMaximum(i);
			progressBar.setString("0 / " + i);
			progressBar.update(progressBar.getGraphics());
			break;
		case "manual":
			manProgressBar.setMaximum(i);
			manProgressBar.setString("0 / " + i);
			manProgressBar.update(manProgressBar.getGraphics());
			break;
		}
	}
}
