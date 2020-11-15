package grabber;
import system.data.Settings;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import system.init;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * Handles the creation of the EPUB file.
 */
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
    private final NovelMetadata novelMetadata;
    private final Book book;

    public EPUB(Novel novel) {
        this.novel = novel;
        this.novelMetadata = novel.metadata;
        book = new Book();
        try {
            book.getResources().add(new Resource(getClass().getResourceAsStream("/default.css"), "default.css"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
            if(init.gui != null) {
                init.gui.appendText(novel.window, "[EPUB-ERROR]Could not add default.css file to EPUB. "+e.getMessage());
            }
        }
    }

    public void writeEpub() {
        // Order is important
        addMetadata();
        addCover();
        addToc();
        if(!novel.noDescription && !novelMetadata.getDescription().isEmpty()) addDesc();
        if (novel.getImages) addImages();
        addChapters();

        String epubFilename = setFilename();
        GrabberUtils.createDir(novel.saveLocation);

        try {
            EpubWriter epubWriter = new EpubWriter();
            epubWriter.write(book, new FileOutputStream(novel.saveLocation + "/" + epubFilename));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR]Could not write EPUB. "+e.getMessage());
            if(init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not write EPUB. "+e.getMessage());
            }
        }
        System.out.println("[GRABBER]Output: " + novel.saveLocation+"/"+ epubFilename);
    }

    private void addImages() {
        for (Map.Entry<String, BufferedImage> entry : novel.images.entrySet()) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(entry.getValue(), GrabberUtils.getFileExtension(entry.getKey()), os);
                InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                Resource resource = new Resource(inputStream, entry.getKey());
                book.getResources().add(resource);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[ERROR]Could not add "+entry.getValue()+" to EPUB. "+e.getMessage());
                if(init.gui != null) {
                    init.gui.appendText(novel.window, "[ERROR]Could not add "+entry.getValue()+" to EPUB. "+e.getMessage());
                }
            }
        }
    }

    private void addChapters() {
        for(Chapter chapter: novel.chapterList) {
            if(chapter.status == 1) {
                try(InputStream inputStream = new ByteArrayInputStream(chapter.chapterContent.getBytes(StandardCharsets.UTF_8))) {
                    Resource resource = new Resource(inputStream, chapter.fileName + ".html");
                    book.addSection(chapter.name, resource);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("[ERROR]Could not add "+chapter.name+" to EPUB. "+e.getMessage());
                    if(init.gui != null) {
                        init.gui.appendText(novel.window,"[ERROR]Could not add "+chapter.name+" to EPUB. "+e.getMessage());
                    }
                }
            }
        }
    }

    private String setFilename() {
        String epubFilename = "Unknown.epub";
        switch (Settings.getInstance().getFilenameFormat()) {
            case 0:
                epubFilename = novelMetadata.getAuthor() + " - " + novelMetadata.getTitle() + ".epub";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
            case 1:
                epubFilename = novelMetadata.getTitle() + " - " + novelMetadata.getAuthor() + ".epub";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
            case 2:
                epubFilename = novelMetadata.getTitle() + ".epub";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
        }
        return epubFilename.replaceAll("[\\\\/:*?\"<>|]", "");
    }

    private void addMetadata() {
        Metadata metadata = book.getMetadata(); // EPUB metadata
        metadata.addTitle(novelMetadata.getTitle());
        metadata.addAuthor(new Author(novelMetadata.getAuthor()));
        metadata.setSubjects(novelMetadata.getSubjects());
        if (!novelMetadata.getDescription().isEmpty() && !novel.noDescription) {
            metadata.setDescriptions(Collections.singletonList(novelMetadata.getDescription()));
        }
    }

    public void addCover() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(novelMetadata.getBufferedCover(), novelMetadata.getCoverFormat(), os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            Resource resource = new Resource(inputStream, "cover." + novelMetadata.getCoverFormat());
            book.getResources().add(resource);
            book.setCoverImage(resource);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR]Could not add cover to EPUB. " + e.getMessage());
            if(init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not add cover to EPUB. " + e.getMessage());
            }
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
            System.out.println("[ERROR]Could not add table of content to EPUB. "+e.getMessage());
            if(init.gui != null) {
                init.gui.appendText(novel.window, "[EPUB-ERROR]Could not add table of content to EPUB. "+e.getMessage());
            }
        }
    }

    public void addDesc() {
        String descString = htmlHead + NL+
                "<div><b>Description</b>" + NL +
                "<p>" + novelMetadata.getDescription() + "</p>" + NL +
                "</div>" + NL +
                htmlFoot;
        try (InputStream inputStream = new ByteArrayInputStream(descString.getBytes(StandardCharsets.UTF_8))) {
            Resource resource = new Resource(inputStream, "desc_Page.html");
            book.addSection("Description", resource);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ERROR]Could not add description to EPUB. "+e.getMessage());
            if(init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not add description to EPUB. "+e.getMessage());
            }
        }
    }
}
