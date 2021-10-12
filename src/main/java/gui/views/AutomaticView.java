package gui.views;

import gui.components.WrapLayout;

import javax.swing.*;
import java.awt.*;

public class AutomaticView extends JPanel {

    private final JPanel taskPanel;

    public AutomaticView() {
        super(new GridBagLayout());

        setBackground(Color.decode("#2e3440"));
        GridBagConstraints c;
        // Result panel
        taskPanel = new JPanel(new WrapLayout(FlowLayout.LEADING));

        JScrollPane resultScrollPane = new JScrollPane(taskPanel);
        resultScrollPane.setBorder(null);
        resultScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 10, 10);
        add(resultScrollPane, c);

        JPanel newDownloadPanel = new JPanel(new GridBagLayout());

        JButton imageBtn = new JButton("+");
        imageBtn.setPreferredSize(new Dimension(150, 200));
        imageBtn.setBackground(Color.decode("#4c566a"));
        imageBtn.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        imageBtn.setToolTipText("Start a new download");
        imageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageBtn.addActionListener(actionEvent -> new NovelInputDialog());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTH;
        newDownloadPanel.add(imageBtn, c);

        JTextArea textArea = new JTextArea();
        textArea.setText("New download");
        textArea.setPreferredSize(new Dimension(150, 50));
        textArea.setEditable(false);
        textArea.setCursor(null);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        newDownloadPanel.add(textArea, c);

        taskPanel.add(newDownloadPanel);
    }

    public void addDownloadTile(NovelDownloadView downloadView) {
        GridBagConstraints c;
        JPanel newDownloadPanel = new JPanel(new GridBagLayout());
        JButton imageBtn = new JButton();
        imageBtn.setIcon(downloadView.getCoverImage());
        imageBtn.setPreferredSize(new Dimension(150, 200));
        imageBtn.setBackground(Color.decode("#4c566a"));
        imageBtn.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        imageBtn.setToolTipText("Start a new download");
        imageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageBtn.addActionListener(actionEvent -> new NovelInputDialog());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTH;
        newDownloadPanel.add(imageBtn, c);

        JTextArea textArea = new JTextArea();
        textArea.setText("New download");
        textArea.setPreferredSize(new Dimension(150, 50));
        textArea.setEditable(false);
        textArea.setCursor(null);
        textArea.setOpaque(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        newDownloadPanel.add(textArea, c);

        taskPanel.add(newDownloadPanel);
    }

}
