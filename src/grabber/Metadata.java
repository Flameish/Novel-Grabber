package grabber;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Metadata {
    public String bookTitle;
    public String bookAuthor;
    public List<String> bookSubjects = new ArrayList();
    public List bookDesc = new ArrayList();
    public BufferedImage bufferedCover;
    public String bufferedCoverName;
    public String bookCover;
    public int wordCount = 0;
    private Novel novel;

    public Metadata(Novel novel) {
        this.novel = novel;
        // Reset info on GUI
        if(init.window != null) {
            init.window.autoBookTitle.setText("");
            init.window.autoAuthor.setText("");
            init.window.autoChapterAmount.setText("");
            init.window.setBufferedCover(null);
            init.window.autoBookSubjects.setText("");
        }

    }

    void getTitle() {
        if (!novel.host.bookTitleSelector.isEmpty()) {
            if (novel.tableOfContent.select(novel.host.bookTitleSelector) != null && !novel.tableOfContent.select(novel.host.bookTitleSelector).isEmpty()) {
                bookTitle = novel.tableOfContent.select(novel.host.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
                if(init.window != null) {
                    init.window.autoBookTitle.setText(bookTitle);
                }
            } else {
                bookTitle = "Unknown";
                if(init.window != null) {
                    init.window.autoBookTitle.setText("Unknown");

                }
            }
        } else {
            bookTitle = "Unknown";
            if(init.window != null) {
                init.window.autoBookTitle.setText("Unknown");
            }
        }
    }

    void getDesc() {
        if (!novel.host.bookDescSelector.equals("false")) {
            if (novel.tableOfContent.select(novel.host.bookDescSelector) != null && !novel.tableOfContent.select(novel.host.bookDescSelector).isEmpty()) {
                bookDesc.add(0, novel.tableOfContent.select(novel.host.bookDescSelector).first().text());
            } else {
                bookDesc.add(0, "");
            }
        } else {
            bookDesc.add(0, "");
        }
    }


    void getAuthor() {
        if (!novel.host.bookAuthorSelector.isEmpty()) {
            if (novel.tableOfContent.select(novel.host.bookAuthorSelector) != null && !novel.tableOfContent.select(novel.host.bookAuthorSelector).isEmpty()) {
                bookAuthor = novel.tableOfContent.select(novel.host.bookAuthorSelector).first().text();
                if(init.window != null) {
                    init.window.autoAuthor.setText(bookAuthor);
                }
            } else {
                bookAuthor = "Unknown";
                if(init.window != null) {
                    init.window.autoAuthor.setText("Unknown");
                }
            }
        } else {
            bookAuthor = "Unknown";
            if(init.window != null) {
                init.window.autoAuthor.setText("Unknown");
            }
        }
    }

    void getTags() {
        if (!novel.host.bookSubjectSelector.isEmpty()) {
            if (novel.tableOfContent.select(novel.host.bookSubjectSelector) != null && !novel.tableOfContent.select(novel.host.bookSubjectSelector).isEmpty()) {
                Elements tags = novel.tableOfContent.select(novel.host.bookSubjectSelector);
                for (Element tag : tags) {
                    bookSubjects.add(tag.text());
                }

                // Display book subjects on GUI
                int maxNumberOfSubjects = 0;
                if(init.window != null) {
                    init.window.autoBookSubjects.setText("<html>");
                    for (String eachTag : bookSubjects) {
                        init.window.autoBookSubjects.setText(init.window.autoBookSubjects.getText() + eachTag + ", ");
                        maxNumberOfSubjects++;
                        if (maxNumberOfSubjects == 4) {
                            maxNumberOfSubjects = 0;
                            init.window.autoBookSubjects.setText(init.window.autoBookSubjects.getText() + "<br>");
                        }
                    }
                    if (!init.window.autoBookSubjects.getText().isEmpty()) {
                        init.window.autoBookSubjects.setText(
                                init.window.autoBookSubjects.getText().substring(0,
                                        init.window.autoBookSubjects.getText().lastIndexOf(",")));
                    }
                }
            } else {
                bookSubjects.add("Unknown");
                if(init.window != null) {
                    init.window.autoBookSubjects.setText("Unknown");

                }
            }
        } else {
            bookSubjects.add("Unknown");
            if(init.window != null) {
                init.window.autoBookSubjects.setText("Unknown");
            }
        }
    }

    void getChapterNumber() {
        if (!novel.chapters.isEmpty()) {
            if(init.window != null) {
                init.window.autoChapterAmount.setText(String.valueOf(novel.chapters.size()));
                init.window.autoGetNumberButton.setEnabled(true);
            }
        }
    }

    void getCover() {
        if (!novel.host.bookCoverSelector.isEmpty()) {
            if (novel.tableOfContent.select(novel.host.bookCoverSelector) != null && !novel.tableOfContent.select(novel.host.bookCoverSelector).isEmpty()) {
                Element coverSelect = novel.tableOfContent.select(novel.host.bookCoverSelector).first();
                if (coverSelect != null) {
                    String coverLink = coverSelect.attr("abs:src");
                    // Custom
                    if (novel.host.url.equals("https://wordexcerpt.com/"))
                        coverLink = coverSelect.attr("data-src");
                    if (novel.host.url.equals("https://webnovel.com/")) {
                        coverLink = coverLink.replace("/300/300", "/600/600");
                    }
                    bufferedCover = GrabberUtils.getBufferedCover(coverLink, novel);
                    if(!novel.imageNames.isEmpty()) {
                        if(init.window != null) {
                            init.window.setBufferedCover(bufferedCover);
                        }
                        bookCover = novel.imageNames.get(0);
                    }
            /* downloadImage() adds every image to <Lists> and this interferes with
               the cover image when adding images from these <Lists> to the epub */
                    novel.imageNames.clear();
                    novel.imageLinks.clear();
                }
            }
        }
    }
}
