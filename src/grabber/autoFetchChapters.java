package grabber;

import gui.GUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

/**
 * Chapter download handling of the automatic tab.
 */
public class autoFetchChapters {
    public static boolean killTask = false;

    static void getChapters(Download currGrab) {
        try {
            // Need to reset in case of stopped grabbing
            currGrab.chapterLinks.clear();
            currGrab.chaptersNames.clear();
            // Connect to webpage
            Document doc = Jsoup.connect(currGrab.currHostSettings.url).get();
            // Get chapter links and names.
            if (!currGrab.autoChapterToChapter) {
                if (currGrab.currHostSettings.host.equals("https://www.fanfiction.net/")) {
                    Elements chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
                    String fullLink = doc.select("link[rel=canonical]").attr("abs:href");
                    String baseLinkStart = fullLink.substring(0, Shared.ordinalIndexOf(fullLink, "/", 5) + 1);
                    String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);

                    Elements links = chapterItems.select("option[value]");
                    for (Element chapterLink : links) {
                        if (!currGrab.chapterLinks.contains(baseLinkStart + chapterLink.attr("value") + baseLinkEnd)) {
                            currGrab.chapterLinks.add(baseLinkStart + chapterLink.attr("value") + baseLinkEnd);
                            currGrab.chaptersNames.add(chapterLink.text());
                        }
                    }

                } else {
                    Elements chapterItems = doc.select(currGrab.currHostSettings.chapterLinkSelecter);
                    Elements links = chapterItems.select("a[href]");
                    for (Element chapterLink : links) {
                        currGrab.chapterLinks.add(chapterLink.attr("abs:href"));
                        currGrab.chaptersNames.add(chapterLink.text());
                    }
                }
            }
            currGrab.tocDoc = doc;
        } catch (IllegalArgumentException | IOException e) {
            currGrab.gui.appendText(currGrab.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
    }

    static void downloadChapters(Download currGrab) {
        currGrab.successfulFilenames.clear();
        currGrab.successfulChapterNames.clear();
        currGrab.failedChapters.clear();
        currGrab.imageLinks.clear();
        currGrab.imageNames.clear();
        currGrab.gui.appendText(currGrab.window, "[INFO]Connecting...");
        // Reverse link order if selected.
        if (currGrab.invertOrder) {
            Collections.reverse(currGrab.chapterLinks);
            Collections.reverse(currGrab.chaptersNames);
        }
        // To latest chapter.
        if (currGrab.gui.toLastChapter.isSelected()) {
            currGrab.lastChapter = currGrab.chapterLinks.size();
        }
        // Grab all chapters.
        if (currGrab.allChapters) {
            processAllChapters(currGrab);
            // Grab chapters from specific range.
        } else {
            if (currGrab.lastChapter > currGrab.chapterLinks.size()) {
                currGrab.gui.appendText(currGrab.window, "[ERROR] Novel does not have that many chapters. " +
                        "(" + currGrab.chapterLinks.size() + " detected.)");
                return;
            }
            processSpecificChapters(currGrab);
        }
    }

    static void getMetadata(Download currGrab) {
        try {
            // Reset
            currGrab.gui.autoBookTitle.setText("");
            currGrab.gui.autoAuthor.setText("");
            currGrab.gui.autoChapterAmount.setText("");
            currGrab.gui.setBufferedCover(null);
            currGrab.gui.autoBookSubjects.setText("");
            Document doc = currGrab.tocDoc;
            // Title
            if (!currGrab.currHostSettings.bookTitleSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookTitleSelector) != null && !doc.select(currGrab.currHostSettings.bookTitleSelector).isEmpty()) {
                    currGrab.bookTitle = doc.select(currGrab.currHostSettings.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                    currGrab.gui.autoBookTitle.setText(currGrab.bookTitle);
                } else {
                    currGrab.bookTitle = "Unknown";
                    currGrab.gui.autoBookTitle.setText("Unknown");
                }
            } else {
                currGrab.bookTitle = "Unknown";
                currGrab.gui.autoBookTitle.setText("Unknown");
            }
            // Author
            if (!currGrab.currHostSettings.bookAuthorSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookAuthorSelector) != null && !doc.select(currGrab.currHostSettings.bookAuthorSelector).isEmpty()) {
                    if (currGrab.currHostSettings.host.equals("https://volarenovels.com/")) {
                        currGrab.bookAuthor = doc.select(currGrab.currHostSettings.bookAuthorSelector).first().text().replace("Translated by: ", "");
                        currGrab.gui.autoAuthor.setText(currGrab.bookAuthor);
                    } else {
                        currGrab.bookAuthor = doc.select(currGrab.currHostSettings.bookAuthorSelector).first().text();
                        currGrab.gui.autoAuthor.setText(currGrab.bookAuthor);
                    }
                } else {
                    currGrab.bookAuthor = "Unknown";
                    currGrab.gui.autoAuthor.setText("Unknown");
                }
            } else {
                currGrab.bookAuthor = "Unknown";
                currGrab.gui.autoAuthor.setText("Unknown");
            }
            if (!currGrab.chapterLinks.isEmpty()) {
                currGrab.gui.autoChapterAmount.setText(String.valueOf(currGrab.chapterLinks.size()));
                currGrab.gui.autoGetNumberButton.setEnabled(true);
            }
            // Tags
            if (!currGrab.currHostSettings.bookSubjectSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookSubjectSelector) != null && !doc.select(currGrab.currHostSettings.bookSubjectSelector).isEmpty()) {
                    Elements tags = doc.select(currGrab.currHostSettings.bookSubjectSelector);
                    if (currGrab.currHostSettings.host.equals("http://gravitytales.com/")) {
                        String allTags = doc.select(currGrab.currHostSettings.bookSubjectSelector).first().text();
                        allTags = allTags.replace("Genres:", "");
                        currGrab.bookSubjects = Arrays.asList(allTags.split(", "));
                        for (String eachTag : currGrab.bookSubjects) {
                            currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + eachTag + " ");
                        }
                    } else {
                        for (Element tag : tags) {
                            currGrab.bookSubjects.add(tag.text());
                            currGrab.gui.autoBookSubjects.setText(currGrab.gui.autoBookSubjects.getText() + tag.text() + " ");
                        }
                    }
                } else {
                    currGrab.bookSubjects = null;
                    currGrab.gui.autoBookSubjects.setText("");
                }
            } else {
                currGrab.bookSubjects = null;
                currGrab.gui.autoBookSubjects.setText("");
            }
            // Chapter number
            if (!currGrab.chapterLinks.isEmpty()) {
                currGrab.gui.autoChapterAmount.setText(String.valueOf(currGrab.chapterLinks.size()));
                currGrab.gui.autoGetNumberButton.setEnabled(true);
            }
            if (currGrab.autoChapterToChapter) {
                currGrab.gui.autoChapterAmount.setText("Unknown");
                currGrab.gui.autoGetNumberButton.setEnabled(false);
            }
            // Cover
            if (!currGrab.currHostSettings.bookCoverSelector.isEmpty()) {
                if (doc.select(currGrab.currHostSettings.bookCoverSelector) != null && !doc.select(currGrab.currHostSettings.bookCoverSelector).isEmpty()) {
                    Element coverSelect = doc.select(currGrab.currHostSettings.bookCoverSelector).first();
                    if (coverSelect != null) {
                        String coverLink;
                        if (currGrab.currHostSettings.host.equals("https://wordexcerpt.com/"))
                            coverLink = coverSelect.attr("data-src");
                        else coverLink = coverSelect.attr("src");
                        currGrab.bufferedCover = Shared.getBufferedCover(coverLink, currGrab);
                        currGrab.gui.setBufferedCover(currGrab.bufferedCover);
                        currGrab.bookCover = currGrab.imageNames.get(0);
                /* downloadImage() adds every image to Lists and this interferes with
                   the cover image when adding images from these Lists to the epub */
                        currGrab.imageNames.clear();
                        currGrab.imageLinks.clear();
                    }
                }
            }
        } catch (Exception e) {
            currGrab.gui.appendText(currGrab.window, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processSpecificChapters(Download currGrab) {
        currGrab.tocFileName = "Table of Contents " + currGrab.firstChapter + "-" + currGrab.lastChapter;
        currGrab.gui.setMaxProgress(currGrab.window, (currGrab.lastChapter - currGrab.firstChapter) + 1);
        currGrab.gui.progressBar.setStringPainted(true);
        for (int i = currGrab.firstChapter; i <= currGrab.lastChapter; i++) {
            Shared.saveChapterWithHTML(currGrab.chapterLinks.get(i - 1), i, currGrab.chaptersNames.get(i - 1),
                    currGrab.currHostSettings.chapterContainer, currGrab);
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
        }
    }

    private static void processAllChapters(Download currGrab) {
        currGrab.tocFileName = currGrab.bookTitle;
        currGrab.gui.setMaxProgress(currGrab.window, currGrab.chapterLinks.size());
        currGrab.gui.progressBar.setStringPainted(true);
        for (int i = 1; i <= currGrab.chapterLinks.size(); i++) {
            Shared.saveChapterWithHTML(currGrab.chapterLinks.get(i - 1), i, currGrab.chaptersNames.get(i - 1),
                    currGrab.currHostSettings.chapterContainer, currGrab);
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
        }
    }

    /**
     * Handles downloading chapter to chapter.
     */
    static void processChaptersToChapters(String[] args, Download currGrab) {
        currGrab.gui.appendText(currGrab.window, "[INFO]Connecting...");
        currGrab.gui.setMaxProgress(currGrab.window, 9001);
        String nextChapter = args[0];
        String lastChapter = args[1];
        currGrab.nextChapterBtn = args[2];
        int chapterNumber = 0;
        while (true) {
            chapterNumber++;
            Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
            nextChapter = currGrab.nextChapterURL;
            if (nextChapter.equals(lastChapter) || (nextChapter + "/").equals(lastChapter)) {
                chapterNumber++;
                Shared.sleep(currGrab.waitTime);
                currGrab.nextChapterBtn = "NOT_SET";
                Shared.saveChapterWithHTML(nextChapter, chapterNumber, "Chapter " + chapterNumber, currGrab.chapterContainer, currGrab);
                break;
            }
            if (killTask) {
                currGrab.gui.appendText(currGrab.window, "[INFO]Stopped.");
                Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
                Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");
                try {
                    if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    currGrab.gui.appendText(currGrab.window, e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
            Shared.sleep(currGrab.waitTime);
        }
    }

    /**
     * Displays chapter name and chapter number.
     */
    public static String[] getChapterNumber(GUI gui, String chapterURL, Download currGrab) {
        try {
            int chapterNumber = currGrab.chapterLinks.indexOf(chapterURL);
            if (chapterNumber == -1)
                chapterNumber = currGrab.chapterLinks.indexOf(chapterURL.substring(0, chapterURL.lastIndexOf("/")));
            if (chapterNumber == -1)
                chapterNumber = currGrab.chapterLinks.indexOf(chapterURL.replace("https:", "http:"));
            if (chapterNumber == -1) gui.showPopup("Could not find chapter number.", "error");
            else {
                return new String[]{currGrab.chaptersNames.get(chapterNumber), String.valueOf(chapterNumber + 1)};
            }
        } catch (IllegalArgumentException e) {
            gui.appendText("auto", "[ERROR]" + e.getMessage());
            e.printStackTrace();
        }
        return new String[]{null, null};
    }
}
