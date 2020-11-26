package gui;

import grabber.GrabberUtils;
import grabber.NovelMetadata;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class editMetadata extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton coverBtn;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField subjectsField;
    private JScrollPane descScrollPane;
    private JTextArea descArea;
    private NovelMetadata metadata;
    private String imageFormat;

    public editMetadata(NovelMetadata metadata) {
        this.metadata = metadata;
        imageFormat = metadata.getCoverFormat();
        setContentPane(contentPane);
        setModal(true);
        setTitle("Edit EPUB metadata");
        ImageIcon favicon = new ImageIcon(getClass().getResource("/images/favicon.png"));
        setIconImage(favicon.getImage());
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        coverBtn.addComponentListener(new ComponentAdapter() {
        });
        // Edit cover image
        coverBtn.addActionListener(arg01 -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".jpg .png .gif .jpeg .svg", "png", "jpg", "gif", "jpeg", "svg");
            chooser.setFileFilter(filter);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String imageFileName = chooser.getSelectedFile().toString();
                imageFormat = GrabberUtils.getFileExtension(imageFileName);
                coverBtn.setIcon(new ImageIcon(new ImageIcon(chooser.getSelectedFile().toString()).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
            }
        });

    }

    static void main(NovelMetadata metadata) {
        editMetadata dialog = new editMetadata(metadata);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void onOK() {
        metadata.setTitle(titleField.getText());
        metadata.setAuthor(authorField.getText());
        metadata.setDescription(descArea.getText());
        metadata.setSubjects(Arrays.asList(subjectsField.getText().split(",")));
        Icon icon = coverBtn.getIcon();
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0,0);
        g.dispose();
        metadata.setBufferedCover(bufferedImage, imageFormat);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        titleField = new JTextField(metadata.getTitle());
        authorField = new JTextField(metadata.getAuthor());
        subjectsField = new JTextField();
        if(!metadata.getSubjects().isEmpty()) {
            for (String tag : metadata.getSubjects()) {
                subjectsField.setText(subjectsField.getText() + tag + ",");
            }
        }
        coverBtn = new JButton();
        coverBtn.setIcon(new ImageIcon(new ImageIcon(metadata.getBufferedCover()).getImage().getScaledInstance(100, 133, Image.SCALE_DEFAULT)));
        coverBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        coverBtn.setBorder(BorderFactory.createEmptyBorder());
        coverBtn.setContentAreaFilled(false);
        descArea = new JTextArea(metadata.getDescription());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descScrollPane = new JScrollPane(descArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
