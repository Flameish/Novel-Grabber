package gui.views;

import gui.components.WrapLayout;

import javax.swing.*;
import java.awt.*;

public class AutomaticView extends JPanel {

    public AutomaticView() {
        super(new WrapLayout(FlowLayout.LEADING));

        setBackground(Color.decode("#2e3440"));
        JPanel taskPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c;

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
        taskPanel.add(imageBtn, c);

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
        taskPanel.add(textArea, c);


        add(taskPanel);
    }

}
