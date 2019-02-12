import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/*
 *  Window display and handling.
 */
public class NovelGrabber {

	private JFrame frmNovelGrabber;
	private JTextField chapterListURL;
	private JTextField destinationFolder;
	private JComboBox<String> websiteSelection1;
	private JComboBox<String> websiteSelection2;
	private JComboBox<String> fileTypeComboBox;
	private static JTextArea logArea;
	private static JProgressBar progressBar;
	private JTextField chapterURL;
	public static final String NL = System.getProperty("line.separator");
	private static String[] websites = {"Wuxiaworld","Royal Road","Gravity Tales","Volare Novels"};
	private static String[] fileTypes = {"HTML","TXT"};
	private JTextField firstChapter;
	private JTextField lastChapter;
	/**
	 * Launch the application
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					NovelGrabber window = new NovelGrabber();
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
	public NovelGrabber() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		frmNovelGrabber = new JFrame();
		frmNovelGrabber.setResizable(false);
		frmNovelGrabber.setTitle("Novel Grabber - 0.01");
		frmNovelGrabber.setBounds(100, 100, 588, 563);
		frmNovelGrabber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNovelGrabber.getContentPane().setLayout(null);
		
		JPanel allChapterPane = new JPanel();
		allChapterPane.setBounds(10, 11, 562, 381);
		allChapterPane.setBorder(BorderFactory.createTitledBorder("Get multiple chapters"));
		frmNovelGrabber.getContentPane().add(allChapterPane);
		allChapterPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 340, 419, 30);
		allChapterPane.add(progressBar);
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		progressBar.setForeground(new Color(0, 128, 128));
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setString("");
		
		JButton getAllChaptersBtn = new JButton("Grab chapters");
		getAllChaptersBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getAllChaptersBtn.setBounds(439, 339, 113, 31);
		allChapterPane.add(getAllChaptersBtn);
		
		chapterListURL = new JTextField();
		chapterListURL.setBounds(152, 19, 400, 25);
		allChapterPane.add(chapterListURL);
		chapterListURL.setToolTipText("https://novelwebsite.com/novel/name");
		chapterListURL.setColumns(10);
		
		JLabel lblNovelChapterList = new JLabel("Table of Contents URL:");
		lblNovelChapterList.setLabelFor(chapterListURL);
		lblNovelChapterList.setBounds(20, 19, 116, 25);
		allChapterPane.add(lblNovelChapterList);
		lblNovelChapterList.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblDestinationDirectory = new JLabel("Save directory:");
		lblDestinationDirectory.setBounds(20, 80, 103, 25);
		allChapterPane.add(lblDestinationDirectory);
		lblDestinationDirectory.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		destinationFolder = new JTextField();
		destinationFolder.setBounds(152, 80, 304, 25);
		allChapterPane.add(destinationFolder);
		destinationFolder.setToolTipText("C:\\Users\\YourName\\somefolder\\novels");
		destinationFolder.setColumns(10);
		
		
		websiteSelection1 = new JComboBox(websites);
		websiteSelection1.setBounds(152, 49, 400, 25);
		allChapterPane.add(websiteSelection1);
		
		JLabel lblNewLabel = new JLabel("Host website:");
		lblNewLabel.setBounds(20, 49, 86, 25);
		allChapterPane.add(lblNewLabel);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));	
		
		JButton btnNewButton = new JButton("Browse...");
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
		btnNewButton.setBounds(466, 79, 86, 27);
		allChapterPane.add(btnNewButton);
		
		logArea = new JTextArea();
		logArea.setEditable(false);
		logArea.setBounds(-22, 11, 235, 41);
		allChapterPane.add(logArea);
		
		JScrollPane scrollPane = new JScrollPane(logArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrollPane.setBounds(10, 223, 542, 111);
		allChapterPane.add(scrollPane);
		
		JPanel chapterSelect = new JPanel();
		chapterSelect.setBounds(10, 109, 542, 45);
		chapterSelect.setBorder(BorderFactory.createTitledBorder("Select chapters to download"));
		allChapterPane.add(chapterSelect);
		chapterSelect.setLayout(null);
		
		JCheckBox chapterAllCheckBox = new JCheckBox("All");
		chapterAllCheckBox.setBounds(101, 12, 62, 23);
		chapterSelect.add(chapterAllCheckBox);
		chapterAllCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(chapterAllCheckBox.isSelected() == true) {
					firstChapter.setEnabled(false);
					lastChapter.setEnabled(false);
				}
				if(chapterAllCheckBox.isSelected() == false) {
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
		optionSelect.setBounds(10, 165, 542, 45);
		optionSelect.setBorder(BorderFactory.createTitledBorder("Option select"));
		allChapterPane.add(optionSelect);
		optionSelect.setLayout(null);
		
		JCheckBox createTocCheckBox = new JCheckBox("Create ToC");
		createTocCheckBox.setBounds(6, 15, 81, 23);
		optionSelect.add(createTocCheckBox);
		createTocCheckBox.setToolTipText("Will create a \"Table of Contents\" file which can be used to convert all chapter files into a single epub file in calibre.");
		
		fileTypeComboBox = new JComboBox(fileTypes);
		fileTypeComboBox.setBounds(230, 16, 66, 20);
		optionSelect.add(fileTypeComboBox);
		
		JLabel fileTypeLabel = new JLabel("File type:");
		fileTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		fileTypeLabel.setBounds(148, 14, 81, 25);
		optionSelect.add(fileTypeLabel);
		
		JPanel singleChapterPane = new JPanel();
		singleChapterPane.setBounds(10, 403, 562, 120);
		singleChapterPane.setBorder(BorderFactory.createTitledBorder("Get single chapter"));
		frmNovelGrabber.getContentPane().add(singleChapterPane);
		singleChapterPane.setLayout(null);
		
		JButton getChapterBtn = new JButton("Grab chapter");
		getChapterBtn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		getChapterBtn.setBounds(439, 65, 113, 32);
		singleChapterPane.add(getChapterBtn);
		
		chapterURL = new JTextField();
		chapterURL.setBounds(133, 25, 419, 30);
		singleChapterPane.add(chapterURL);
		chapterURL.setColumns(10);
		
		JLabel lblchapterURL = new JLabel("Chapter URL:");
		lblchapterURL.setBounds(10, 24, 113, 30);
		singleChapterPane.add(lblchapterURL);
		lblchapterURL.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel label = new JLabel("Host website:");
		label.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label.setBounds(10, 66, 86, 30);
		singleChapterPane.add(label);
		
		websiteSelection2 = new JComboBox(websites);
		websiteSelection2.setBounds(133, 66, 296, 30);
		singleChapterPane.add(websiteSelection2);
		
		//All Chapters
		getAllChaptersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getAllChaptersBtn.setEnabled(false);
				//input validation
				if(chapterListURL.getText().isEmpty() == true) {
					appendText("URL field is empty.");
					chapterListURL.requestFocusInWindow();
				}
				else if((fileTypeComboBox.getSelectedItem().toString() == "TXT") && (createTocCheckBox.isSelected() == true)) {
					appendText("Cannot create a Table of Contents file with .txt file type.");
				}
				else if(destinationFolder.getText().isEmpty() == true) {
					appendText("Save directory field is empty.");
					destinationFolder.requestFocusInWindow();
				}
				else if((chapterAllCheckBox.isSelected() == false) && ((firstChapter.getText().isEmpty() == true) || (lastChapter.getText().isEmpty() == true))) {
					appendText("No chapter range defined.");
				}
				else if((chapterAllCheckBox.isSelected() == false) && (!firstChapter.getText().matches("\\d+") || !lastChapter.getText().matches("\\d+"))) {
					appendText("Chapter range must contain numbers.");
				}
				else if((chapterAllCheckBox.isSelected() == false) && ((Integer.parseInt(firstChapter.getText()) < 1) || (Integer.parseInt(lastChapter.getText()) < 1))) {
					appendText("Chapter numbers can't be lower than 1");
				}
				else if((chapterAllCheckBox.isSelected() == false) && (Integer.parseInt(lastChapter.getText()) < Integer.parseInt(firstChapter.getText()))) {
					appendText("Last chapter can't be lower than first chapter.");
				}
				//grabbing chapter calls
				else if((destinationFolder.getText().isEmpty() == false) && (chapterListURL.getText().isEmpty() == false)) {
					try {
						progressBar.setStringPainted(true);
						//grabbing all chapters
						if(chapterAllCheckBox.isSelected() == true) {
							if(fileTypeComboBox.getSelectedItem().toString() == "TXT") {
								fetchChapters.getAllChapterLinks(chapterListURL.getText(), destinationFolder.getText(), (websiteSelection1.getSelectedItem().toString()).toLowerCase().replace(" ", ""), ".txt");	
							}
							else {
								fetchChapters.getAllChapterLinks(chapterListURL.getText(), destinationFolder.getText(), (websiteSelection1.getSelectedItem().toString()).toLowerCase().replace(" ", ""), ".html");
							}
							if(createTocCheckBox.isSelected() == true) {
							fetchChapters.createToc(destinationFolder.getText());
							}
							fetchChapters.chapterFileNames.clear();
						}
						//grabbing chapters from selected range
						if((chapterAllCheckBox.isSelected() == false) && (firstChapter.getText().isEmpty() == false || lastChapter.getText().isEmpty() == false)) {
							if(fileTypeComboBox.getSelectedItem().toString() == "TXT") {
								fetchChapters.getChapterRangeLinks(chapterListURL.getText(), 
										destinationFolder.getText(), 
										(websiteSelection1.getSelectedItem().toString()).toLowerCase().replace(" ", ""),
										Integer.parseInt(firstChapter.getText()),
										Integer.parseInt(lastChapter.getText()),
										".txt");
							}
							else {
								fetchChapters.getChapterRangeLinks(chapterListURL.getText(), 
										destinationFolder.getText(), 
										(websiteSelection1.getSelectedItem().toString()).toLowerCase().replace(" ", ""),
										Integer.parseInt(firstChapter.getText()),
										Integer.parseInt(lastChapter.getText()),
										".html");
							}
							if(createTocCheckBox.isSelected() == true) {
							fetchChapters.createToc(destinationFolder.getText());
							}	
							fetchChapters.chapterFileNames.clear();
						}
						
					}
					//exception handling
					catch(IllegalArgumentException err) {
						appendText("Error: Must supply a valid URL");
						appendText(err.toString());
					}
					catch(FileNotFoundException err) {
						appendText("Error: Could not access save directory.");
						appendText(err.toString());
					}
					catch(NullPointerException err) {
						appendText("Error: Wrong host or URL input." + NL + "Could not detect key variables.");
						appendText(err.toString());
					}
					catch(IOException err) {
						appendText(err.toString());
					}
					finally {
						progressBar.setStringPainted(false);
						progressBar.setValue(0);
					}
				}
				getAllChaptersBtn.setEnabled(true);
			}
		});
		//Single Chapter
		getChapterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chapterURL.getText().isEmpty() == true) {
					appendText("URL field is empty.");
					chapterURL.requestFocusInWindow();
				}
				else if(chapterURL.getText().isEmpty() == false) {
					try {
						progressBar.setStringPainted(true);
						fetchChapters.saveChapter(chapterURL.getText(), (websiteSelection2.getSelectedItem().toString()).toLowerCase().replace(" ", ""));
					}
					catch(IllegalArgumentException err) {
						appendText("Error: Not a URL.");
						appendText(err.toString());
					}
					catch(FileNotFoundException err) {
						appendText("FileNotFoundException");
						appendText(err.toString());
					}
					catch(NullPointerException err) {
						appendText("Error: Wrong host or URL input.");
						appendText(err.toString());
					}
					catch(IOException err) {
						appendText(err.toString());
					}
					finally {
						progressBar.setStringPainted(false);
						progressBar.setValue(0);
					}
				}
			}
		});
		
	}
	public static void appendText(String log) {
		logArea.append(log + NL);
		logArea.update(logArea.getGraphics());
	}
	public static void updateProgress(int i) {
		progressBar.setValue(progressBar.getValue() + i);
		if(progressBar.getValue() < progressBar.getMaximum()) {
		progressBar.setString((progressBar.getValue() + i) + " / " + progressBar.getMaximum());
		}
		progressBar.update(progressBar.getGraphics());
	}
	public static void setMaxProgress(int i) {
		progressBar.setMaximum(i);
		progressBar.setString("0 / " + i);
		progressBar.update(progressBar.getGraphics());
	}
}
