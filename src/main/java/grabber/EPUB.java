package grabber;
import system.data.Settings;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class EPUB {
    static final String NL = System.getProperty("line.separator");
    static final String htmlHead = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + NL+
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"" + NL +
            "  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" + NL +
            "\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + NL +
            "<head>" + NL +
            "<title></title>" + NL +
            "</head>" + NL +
            "<body>" + NL;
    static final String htmlFoot = "</body>" + NL + "</html>";

    private final Novel novel;
    private final Book book = new Book();

    public EPUB(Novel novel) {
        this.novel = novel;
    }

    public void writeEpub() {
        try {
            addMetadata();
            addCover();
            addToc();
            addChapters();
            if(!novel.noDescription && !novel.bookDesc.isEmpty()) addDesc();
            if (novel.getImages) addImages();

            book.getResources().add(new Resource(getClass().getResourceAsStream("/default.css"), "default.css"));
            EpubWriter epubWriter = new EpubWriter();
            String epubFilename = setFilename();
            GrabberUtils.createDir(novel.saveLocation);
            epubWriter.write(book, new FileOutputStream(novel.saveLocation + "/" + epubFilename));
            novel.epubFilename = epubFilename;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addImages() throws IOException {
        for (Map.Entry<String, BufferedImage> entry : novel.images.entrySet()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(entry.getValue(), GrabberUtils.getFileExtension(entry.getKey()), os);
            try (InputStream inputStream = new ByteArrayInputStream(os.toByteArray())) {
                Resource resource = new Resource(inputStream, entry.getKey());
                book.getResources().add(resource);
            }
        }
    }

    private void addChapters() throws IOException {
        InputStream inputStream = null;
        for(Chapter chapter: novel.chapterList) {
            if(chapter.status == 1) {
                inputStream = new ByteArrayInputStream(
                        chapter.chapterContent.toString()
                                .getBytes(StandardCharsets.UTF_8)
                );
                Resource resource = new Resource(inputStream, chapter.fileName + ".html");
                book.addSection(chapter.name, resource);
            }
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private String setFilename() {
        String epubFilename = "Something-Went-Wrong.epub";
        switch (Settings.getInstance().getFilenameFormat()) {
            case 0:
                epubFilename = novel.bookAuthor + " - " + novel.bookTitle + ".epub";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
            case 1:
                epubFilename = novel.bookTitle + " - " + novel.bookAuthor + ".epub";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
            case 2:
                epubFilename = novel.bookTitle + ".epub";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
        }
        return epubFilename.replaceAll("[\\\\/:*?\"<>|]", "");
    }

    private void addMetadata() {
        Metadata metadata = book.getMetadata();
        // Title
        if (novel.bookTitle != null && !novel.bookTitle.isEmpty()) {
            metadata.addTitle(novel.bookTitle);
        } else {
            metadata.addTitle("Unknown");
        }
        // Author
        if (novel.bookAuthor != null && !novel.bookAuthor.isEmpty() && !novel.bookAuthor.equals("null")) {
            metadata.addAuthor(new Author(novel.bookAuthor));
        } else {
            metadata.addAuthor(new Author("Unknown"));
        }
        // Subjects
        if (novel.bookSubjects != null && !novel.bookSubjects.isEmpty()) {
            metadata.setSubjects(novel.bookSubjects);
        }
        // Description
        if (novel.bookDesc != null && !novel.bookDesc.isEmpty() && !novel.noDescription) {
            metadata.setDescriptions(Collections.singletonList(novel.bookDesc));
        }
    }

    public void addCover() {
        try {
            if(novel.bookCover != null && !novel.bookCover.isEmpty()) {
                // Add BufferedImage as a resource to EPUB
                if(novel.window.equals("auto") || novel.window.equals("checker")) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(novel.bufferedCover, GrabberUtils.getFileExtension(novel.bookCover), os);
                    InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                    Resource resource = new Resource(inputStream, novel.bookCover);
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();
                }
                if(novel.window.equals("manual")) {
                    // Add manual cover image. Its saved as a full path
                    InputStream inputStream = new FileInputStream(novel.bookCover);
                    Resource resource = new Resource(inputStream, GrabberUtils.getFileName(novel.bookCover));
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();
                }
            } else {
                book.setCoverImage(new Resource(getClass().getResourceAsStream("/images/cover_placeholder.png"), "cover_placeholder.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToc() {
        StringBuilder tocString = new StringBuilder(htmlHead + "<b>Table of Contents</b>" + NL + "<p style=\"text-indent:0pt\">" + NL);
        for (Chapter chapter: novel.chapterList) {
            if(chapter.status == 1)
               tocString.append("<a href=\"").append(chapter.fileName).append(".html\">").append(chapter.name).append("</a><br/>").append(NL);
        }
        tocString.append("</p>").append(NL).append(htmlFoot);

        try (InputStream inputStream = new ByteArrayInputStream(tocString.toString().getBytes(StandardCharsets.UTF_8))) {
            Resource resource = new Resource(inputStream, "table_of_contents.html");
            book.addSection("Table of Contents", resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDesc() {
        String descString = htmlHead + NL+
                "<div><b>Description</b>" + NL +
                "<p>" + novel.bookDesc + "</p>" + NL +
                "</div>" + NL +
                htmlFoot;

        try (InputStream inputStream = new ByteArrayInputStream(descString.getBytes(StandardCharsets.UTF_8))) {
            Resource resource = new Resource(inputStream, "desc_Page.html");
            book.addSection("Description", resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
