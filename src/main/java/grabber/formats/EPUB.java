package grabber.formats;
import grabber.helper.Utils;
import grabber.novel.Chapter;
import grabber.novel.Chapter.Status;
import grabber.novel.Novel;
import grabber.novel.NovelMetadata;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import org.jsoup.Jsoup;

import nl.siegmann.epublib.epub.EpubWriter;
import org.jsoup.parser.Parser;
import system.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles the creation of the EPUB file.
 */
public class EPUB {
    public static final String htmlHead = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
            "\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "<title></title>\n" +
            "</head>\n" +
            "<body>\n";
    public static final String htmlFoot = "</body>\n</html>";

    public static String write(Novel novel, String savelocation) throws IOException {
        Logger.verbose("Building EPUB...");

        Book epub = new Book();
        addMetadataToEpub(novel.getMetadata(), epub);
        if (novel.getOptions().isCreateToc()) createDescPage(novel.getMetadata().getDescription(), epub);
        if (novel.getOptions().isCreateToc()) createTocPage(novel.getChapters(), epub);
        if (novel.getOptions().isGetImages()) addImagesToEpub(novel.getImages(), epub);
        addChaptersToEpub(novel.getChapters(), epub);

        String epubFilename = novel.getOptions().getNovelFilenameTemplate()
                .replace("%t", novel.getMetadata().getTitle())
                .replace("%a", novel.getMetadata().getAuthor())
                .replace("%fc", String.valueOf(novel.getOptions().getFirstChapter()))
                .replace("%lc", String.valueOf(novel.getOptions().getLastChapter()))
                .replaceAll("[\\\\/:*?\"<>|]", "")
                + ".epub";
        Utils.createDir(savelocation);
        Logger.info("Writing EPUB " + savelocation + "/" + epubFilename);
        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(epub, new FileOutputStream(savelocation + "/" + epubFilename));
        return epubFilename;
    }

    private static void createDescPage(String desc, Book epub) {
        String descString = String.format("%s<div><h1>Description</h1>\n<p>%s</p>\n</div>\n%s", htmlHead, desc, htmlFoot);
        Resource resource = new Resource(
                "descPage",
                Utils.cleanHTMLString(descString).getBytes(StandardCharsets.UTF_8),
                "desc_page.html",
                MediatypeService.determineMediaType("desc_page.html"));
        epub.addSection("Description", resource);
    }

    private static void createTocPage(List<Chapter> chapterList, Book epub) {
        StringBuilder tocBuilder = new StringBuilder(htmlHead +
                "<h1>Table of Contents</h1>\n" +
                "<ol>\n");
        int chapterCounter = 1;
        for(Chapter chapter: chapterList) {
            if(chapter.getDownloadStatus() != Status.SUCCESS) continue;

            String sanitizedChapterName = chapter.getName().replaceAll("[^\\w]+", "-");
            String chapterFilename = String.format("%05d-%s.html", chapterCounter++, sanitizedChapterName);
            tocBuilder.append(String.format("<li><a href=\"%s\">%s</a></li>\n", chapterFilename, chapter.getName()));

        }
        tocBuilder.append("</ol>\n").append(htmlFoot);

        Resource resource = new Resource(
                "tocPage",
                Utils.cleanHTMLString(tocBuilder.toString()).getBytes(StandardCharsets.UTF_8),
                "table_of_contents.html",
                MediatypeService.determineMediaType("table_of_contents.html"));
        epub.addSection("Table of Contents", resource);
    }

    private static void addMetadataToEpub(NovelMetadata novelMetadata, Book epub) {
        Metadata epubMetadata = epub.getMetadata();
        epubMetadata.addTitle(novelMetadata.getTitle());
        epubMetadata.addAuthor(new Author(novelMetadata.getAuthor()));
        epubMetadata.setSubjects(novelMetadata.getSubjects());
        if (!novelMetadata.getDescription().isEmpty()) {
            epubMetadata.setDescriptions(Collections.singletonList(novelMetadata.getDescription()));
        }
        if (novelMetadata.getCoverName() != null && novelMetadata.getCoverImage() != null) {
            Resource resource = new Resource(
                    "coverImage",
                    novelMetadata.getCoverImage(),
                    novelMetadata.getCoverName(),
                    MediatypeService.determineMediaType(novelMetadata.getCoverName()));
            epub.getResources().add(resource);
            epub.setCoverImage(resource);
        }
    }

    private static void addImagesToEpub(Map<String, byte[]> images, Book epub) {
        for (Map.Entry<String, byte[]> entry : images.entrySet()) {
            byte[] image = entry.getValue();
            String imageFilename = entry.getKey();
            Resource resource = new Resource(
                    imageFilename,
                    image,
                    imageFilename,
                    MediatypeService.determineMediaType(imageFilename));
            epub.getResources().add(resource);
        }
    }

    private static void addChaptersToEpub(List<Chapter> chapterList, Book epub) {
        int chapterCounter = 1;
        for(Chapter chapter: chapterList) {
            if(chapter.getDownloadStatus() != Status.SUCCESS) continue;

            String sanitizedChapterName = chapter.getName().replaceAll("[^\\w]+", "-");
            String chapterFilename = String.format("%05d-%s.html", chapterCounter++, sanitizedChapterName);
            String chapterString = htmlHead + Utils.cleanHTMLString(chapter.getChapterBody().toString()) + htmlFoot;
            Resource resource = new Resource(
                    chapter.getUrl(),
                    chapterString.getBytes(StandardCharsets.UTF_8),
                    chapterFilename,
                    MediatypeService.determineMediaType(chapterFilename));
            resource.setTitle(sanitizedChapterName);
            epub.addSection(chapter.getName(), resource);
        }
    }

    public static Novel read(String path) throws IOException {
        File epubFile = new File(path);
        Logger.verbose("Reading " + path);
        try(InputStream fileInputStream = new FileInputStream(epubFile)) {
            Book book = new EpubReader().readEpub(fileInputStream, "UTF-8");
            Logger.verbose("Loading metadata...");
            Metadata bookMetadata = book.getMetadata();
            NovelMetadata novelMetadata = new NovelMetadata();
            novelMetadata.setTitle(bookMetadata.getTitles().get(0));
            novelMetadata.setAuthor(bookMetadata.getAuthors().get(0).toString());
            novelMetadata.setSubjects(bookMetadata.getSubjects());
            novelMetadata.setDescription(bookMetadata.getDescriptions().get(0));
            novelMetadata.setCoverImage(book.getCoverImage().getData());
            novelMetadata.setCoverName(book.getCoverImage().getHref());

            Novel novel = new Novel(novelMetadata);
            Logger.verbose("Loading chapters...");
            for (TOCReference tocReference : book.getTableOfContents().getTocReferences()) {
                Resource resource = tocReference.getResource();
                if (!resource.getHref().equals("desc_page.html") && !resource.getHref().equals("table_of_contents.html")) {
                    Chapter chapter = new Chapter(tocReference.getTitle(), resource.getId());
                    chapter.setChapterBody(
                            Jsoup.parse(new String(resource.getData(), StandardCharsets.UTF_8),
                                    resource.getId(),
                                    Parser.htmlParser()));
                    chapter.setDownloadStatus(Status.SUCCESS);
                    novel.getChapters().add(chapter);
                }
            }

            Logger.verbose("Loading images...");
            for (Resource resource : book.getResources().getAll()) {
                if (resource.getMediaType().getName().startsWith("image/") && !resource.getId().startsWith("coverImage")) {
                    novel.getImages().put(resource.getHref(), resource.getData());
                }
            }

            return novel;
        }
    }
}
