package gui.views;

import grabber.sources.SourceException;
import gui.components.WrapLayout;
import search.Search;
import search.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchView extends JPanel {
    private final JPanel searchPanel;
    private final JPanel resultPanel;
    private final JComboBox<String> searchSourcesComboBox;
    private final JTextField searchField;
    private final JLabel wipIndicatorLbl;
    private final JButton searchBtn;
    private Search search;

    public SearchView() {
        super(new GridBagLayout());
        GridBagConstraints c;

        //Search Panel
        searchPanel = new JPanel(new GridBagLayout());

        searchSourcesComboBox = new JComboBox<>();
        searchSourcesComboBox.setVisible(false);
        searchSourcesComboBox.addActionListener(e -> {
            if (searchSourcesComboBox.getSelectedItem() != null) {
                String sourceSelection = searchSourcesComboBox.getSelectedItem().toString();
                if (sourceSelection.equals("All")) displaySearchResults();
                else displaySearchResults(sourceSelection);
            }
        });
        c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        searchPanel.add(searchSourcesComboBox, c);

        searchField = new JTextField(20);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        searchField.addActionListener(e -> executor.execute(() -> {
            if (!searchField.getText().isEmpty()) {
                fireSearch(searchField.getText());
            }
        }));
        c = new GridBagConstraints();
        c.gridx = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.EAST;
        searchPanel.add(searchField, c);

        searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> EventQueue.invokeLater(() -> {
            if (searchSourcesComboBox.getSelectedItem() != null) {
                String sourceSelection = searchSourcesComboBox.getSelectedItem().toString();
                if (sourceSelection.equals("All")) displaySearchResults();
                else displaySearchResults(sourceSelection);
            }
        }));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.weightx = 1.0f;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 10, 0, 0);
        searchPanel.add(searchBtn, c);

        wipIndicatorLbl = new JLabel();
        wipIndicatorLbl.setVisible(false);
        wipIndicatorLbl.setIcon(new ImageIcon(getClass().getResource("/images/busy.gif")));
        wipIndicatorLbl.setSize(new Dimension(30, 30));
        c = new GridBagConstraints();
        c.gridx = 3;
        c.anchor = GridBagConstraints.EAST;
        searchPanel.add(wipIndicatorLbl, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0f;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 10);
        add(searchPanel, c);

        // Result panel
        resultPanel = new JPanel(new WrapLayout(FlowLayout.CENTER, 20, 10));

        JScrollPane resultScrollPane = new JScrollPane(resultPanel);
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
    }

    private void buildSourceSelection() {
        searchSourcesComboBox.removeAll();
        searchSourcesComboBox.addItem("All");
        search.getResults().forEach((sourceName, searchResults) -> {
            searchSourcesComboBox.addItem(sourceName);
        });
        searchSourcesComboBox.setVisible(searchSourcesComboBox.getModel().getSize() > 0);
    }

    private void displaySearchResults() {
        clearSearchResults();
        search.getResults().forEach((sourceName, searchResults) -> {
            for (SearchResult result : searchResults) {
                if (result != null) {
                    JPanel resultNovel = createResultPanel(result);
                    resultPanel.add(resultNovel);
                }
            }
        });
        resultPanel.revalidate();
    }

    private void displaySearchResults(String sourceName) {
        clearSearchResults();
        SearchResult[] searchResults = search.getResultsForSource(sourceName);
        for (SearchResult result : searchResults) {
            if (result != null) {
                JPanel resultNovel = createResultPanel(result);
                resultPanel.add(resultNovel);
            }
        }
        resultPanel.revalidate();
    }


    private void clearSearchResults() {
        resultPanel.removeAll();
    }

    private void fireSearch(String searchTerm) {
        searchBtn.setEnabled(false);
        wipIndicatorLbl.setVisible(true);
        search = new Search(searchTerm);
        search.execute();
        buildSourceSelection();
        displaySearchResults();
        wipIndicatorLbl.setVisible(false);
        searchBtn.setEnabled(true);
    }

    private JPanel createResultPanel(SearchResult searchResult) {
        JPanel novelPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c;

        ImageIcon imageIcon;
        if(searchResult.getCoverImage() == null) {
            imageIcon = new ImageIcon(getClass().getResource("/images/default_cover.png"));
        } else {
            imageIcon = new ImageIcon(searchResult.getCoverImage());
        }
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(150, 200,  Image.SCALE_SMOOTH); // scale it the smooth way
        imageIcon = new ImageIcon(newimg);

        JButton imageBtn = new JButton(imageIcon);
        imageBtn.setPreferredSize(new Dimension(150, 200));
        imageBtn.setBorder(BorderFactory.createEmptyBorder());
        imageBtn.setToolTipText(searchResult.getTitle());
        imageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageBtn.addActionListener(actionEvent -> new SearchResultView(searchResult));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0f;
        c.anchor = GridBagConstraints.NORTH;
        novelPanel.add(imageBtn, c);

        JTextArea textArea = new JTextArea();
        textArea.setText(searchResult.getTitle());
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
        novelPanel.add(textArea, c);

        return novelPanel;
    }
}
