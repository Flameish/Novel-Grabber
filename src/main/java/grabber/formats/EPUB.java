package grabber.formats;
import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import system.Config;

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
    private Novel novel;
    private final NovelMetadata novelMetadata;
    private Book book;

    public EPUB(Novel novel) {
        this.novel = novel;
        this.novelMetadata = novel.metadata;
        // Library novels try to update existing files
        if (novel.window.equals("checker")) {
            try {
                book = tryReadOldFile();
            } catch (FileNotFoundException e) {
                GrabberUtils.err("Could not find old book file: " + e.getMessage());
            } catch (IOException e) {
                GrabberUtils.err("Could not access old book file: " + e.getMessage());
            }
        }
        if (book == null) {
            book = new Book();
            try {
                book.getResources().add(new Resource(getClass().getResourceAsStream("/default.css"), "default.css"));
            } catch (IOException e) {
                GrabberUtils.err(novel.window, "Could not add default.css file to EPUB. " + e.getMessage());
            }
        }
    }

    public void write() {
        // Order is important
        addMetadata();
        addCover();
        // Not re-adding for existing epubs
        if (!novel.window.equals("checker")) {
            addToc();
            if(!novel.noDescription && !novelMetadata.getDescription().isEmpty()) addDesc();
        }
        if (novel.getImages) addImages();
        addChapters();

        String epubFilename = setFilename();
        GrabberUtils.createDir(novel.saveLocation);

        try {
            GrabberUtils.info(novel.window,"Writing EPUB...");
            EpubWriter epubWriter = new EpubWriter();
            epubWriter.write(book, new FileOutputStream(novel.saveLocation + "/" + epubFilename));
            novel.filename = epubFilename;
            GrabberUtils.info(novel.window, "Output: " + novel.saveLocation+"/"+ epubFilename);
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not write EPUB. " + e.getMessage(), e);
        }
    }

    /**
     * Tries to read old EPUB file from save location.
     */
    public Book tryReadOldFile() throws IOException {
        File epubFile = new File(novel.saveLocation + "/" + setFilename());
        InputStream inputStream = new FileInputStream(epubFile);
        return new EpubReader().readEpub(inputStream, "UTF-8");
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
                GrabberUtils.err(novel.window, "Could not add "+entry.getValue()+" to EPUB. "+e.getMessage(), e);
            }
        }
    }

    private void addChapters() {
        for(Chapter chapter: novel.chapterList) {
            if(chapter.status == 1) {
                String chapterString = htmlHead + chapter.chapterContent + htmlFoot;
                try(InputStream inputStream = new ByteArrayInputStream(chapterString.getBytes(StandardCharsets.UTF_8))) {
                    Resource resource = new Resource(inputStream, chapter.fileName + ".html");
                    book.addSection(chapter.name, resource);
                } catch (IOException e) {
                    GrabberUtils.err(novel.window, "Could not add "+chapter.name+" to EPUB. "+e.getMessage(), e);
                }
            }
        }
    }

    private String setFilename() {
        String epubFilename = "Unknown.epub";
        switch (Config.getInstance().getFilenameFormat()) {
            case 0:
                epubFilename = novelMetadata.getAuthor() + " - " + novelMetadata.getTitle() + ".epub";
                break;
            case 1:
                epubFilename = novelMetadata.getTitle() + " - " + novelMetadata.getAuthor() + ".epub";
                break;
            case 2:
                epubFilename = novelMetadata.getTitle() + ".epub";
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
            GrabberUtils.err(novel.window, "Could not add cover to EPUB. " + e.getMessage(), e);
        }
    }

    public void addToc() {
        StringBuilder tocBuilder = new StringBuilder(htmlHead + NL +
                "<b>Table of Contents</b>" + NL +
                "<p style=\"text-indent:0pt\">" + NL);
        for (Chapter chapter: novel.chapterList) {
            if(chapter.status == 1)
                tocBuilder.append("<a href=\"").append(chapter.fileName).append(".html\">").append(chapter.name).append("</a><br/>").append(NL);
        }
        tocBuilder.append("</p>").append(NL).append(htmlFoot);

        Document.OutputSettings settings = new Document.OutputSettings();
        settings.syntax(Document.OutputSettings.Syntax.xml);
        settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        settings.charset("UTF-8");

        Document doc = Jsoup.parse(tocBuilder.toString());
        doc.outputSettings(settings);

        try (InputStream inputStream = new ByteArrayInputStream(doc.html().getBytes(StandardCharsets.UTF_8))) {
            Resource resource = new Resource(inputStream, "table_of_contents.html");
            book.addSection("Table of Contents", resource);
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not add table of content to EPUB. " + e.getMessage(), e);
        }
    }

    public void addDesc() {
        String descString = htmlHead + NL+
                "<div><b>Description</b>" + NL +
                "<p>" + novelMetadata.getDescription() + "</p>" + NL +
                "</div>" + NL +
                htmlFoot;

        Document.OutputSettings settings = new Document.OutputSettings();
        settings.syntax(Document.OutputSettings.Syntax.xml);
        settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        settings.charset("UTF-8");

        Document doc = Jsoup.parse(descString);
        doc.outputSettings(settings);

        try (InputStream inputStream = new ByteArrayInputStream(doc.html().getBytes(StandardCharsets.UTF_8))) {
            Resource resource = new Resource(inputStream, "desc_Page.html");
            book.addSection("Description", resource);
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not add description to EPUB. "+e.getMessage(), e);
        }
    }
}
