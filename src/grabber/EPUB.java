package grabber;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import system.init;
import system.persistent.Settings;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EPUB {
    private static final String NL = System.getProperty("line.separator");
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
    private FileInputStream inputStream;
    private Resource resource;

    public EPUB(Novel novel) {
        this.novel = novel;
    }

    public void writeEpub() {
        try {
            if(init.window != null && !novel.options.window.equals("checker")) {
                init.window.appendText(novel.options.window, "[INFO]Writing epub...");
            }
            Book book = new Book();
            Metadata metadata = book.getMetadata();
            // Title
            if (novel.metadata.bookTitle != null && !novel.metadata.bookTitle.isEmpty()) {
                metadata.addTitle(novel.metadata.bookTitle);
            } else {
                metadata.addTitle("Unknown");
            }
            // Author
            if (novel.metadata.bookAuthor != null && !novel.metadata.bookAuthor.isEmpty() && !novel.metadata.bookAuthor.equals("null")) {
                metadata.addAuthor(new Author(novel.metadata.bookAuthor));
            } else {
                metadata.addAuthor(new Author("Unknown"));
            }
            // Subjects
            if (novel.metadata.bookSubjects != null && !novel.metadata.bookSubjects.isEmpty()) {
                metadata.setSubjects(novel.metadata.bookSubjects);
            }
            // Description
            if (novel.metadata.bookDesc != null && !novel.metadata.bookDesc.isEmpty() && !novel.options.noDescription) {
                metadata.setDescriptions(novel.metadata.bookDesc);
            }
            // Set cover image & page
            if (novel.metadata.bookCover != null && !novel.metadata.bookCover.isEmpty()) {
                if (novel.options.window.equals("auto") || novel.options.window.equals("checker")) {
                    // Add cover image as a resource
                    inputStream = new FileInputStream(novel.options.saveLocation + "/images/" + novel.metadata.bookCover);
                    resource = new Resource(inputStream, novel.metadata.bookCover);
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();
                } else {
                    // Add manual cover image. Its saved as a full path
                    inputStream = new FileInputStream(novel.metadata.bookCover);
                    resource = new Resource(inputStream, GrabberUtils.getFileName(novel.metadata.bookCover));
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();
                }
                // Adding cover page
                inputStream = new FileInputStream(novel.options.saveLocation + "/chapters/"
                        + novel.extraPages.get(0) + ".html");
                resource = new Resource(inputStream, novel.extraPages.get(0) + ".html");
                book.setCoverPage(resource);
                book.addSection("Cover", resource);
                inputStream.close();
            } else {
                book.setCoverImage(new Resource(getClass().getResourceAsStream("/files/images/cover_placeholder.png"), "cover_placeholder.png"));

            }
            // Table of Contents
            inputStream = new FileInputStream(novel.options.saveLocation + "/chapters/"
                    + novel.extraPages.get(1) + ".html");
            resource = new Resource(inputStream, novel.extraPages.get(1) + ".html");
            book.addSection("Table of Contents", resource);
            inputStream.close();

            // Description page
            if (novel.metadata.bookDesc != null && !novel.metadata.bookDesc.isEmpty() && !novel.options.noDescription) {
                inputStream = new FileInputStream(novel.options.saveLocation + "/chapters/"
                        + novel.extraPages.get(2) + ".html");
                resource = new Resource(inputStream, novel.extraPages.get(2) + ".html");
                book.addSection("Description", resource);
                inputStream.close();
            }

            // Chapters
            for(Chapter chapter: novel.chapters) {
                if(chapter.status == 1) {
                    inputStream = new FileInputStream(novel.options.saveLocation + "/chapters/"
                            + chapter.fileName + ".html");
                    resource = new Resource(inputStream, chapter.fileName + ".html");
                    book.addSection(chapter.name, resource);
                    inputStream.close();
                }

            }

            // Add used images
            if (novel.options.getImages) {
                for (String imageName : novel.imageNames) {
                    inputStream = new FileInputStream(novel.options.saveLocation + "/images/" + imageName);
                    resource = new Resource(inputStream, imageName);
                    book.getResources().add(resource);
                    inputStream.close();
                }
            }
            // Add css file
            book.getResources().add(new Resource(getClass().getResourceAsStream("/files/default.css"), "default.css"));
            // Create EpubWriter
            EpubWriter epubWriter = new EpubWriter();
            // Write the Book as Epub
            String epubFilename;
            switch (Settings.getEPUBOutputFormat()) {
                case 0:
                    epubFilename = (novel.metadata.bookAuthor + " - " + novel.metadata.bookTitle + ".epub").replaceAll(" ","");
                    epubWriter.write(book, new FileOutputStream(novel.options.saveLocation
                            + "/" + epubFilename));
                    break;
                case 1:
                    epubFilename = (novel.metadata.bookTitle + " - " + novel.metadata.bookAuthor + ".epub").replaceAll(" ","");
                    epubWriter.write(book, new FileOutputStream(novel.options.saveLocation
                            + "/" + epubFilename));
                    break;
                case 2:
                    epubFilename = (novel.metadata.bookTitle + ".epub").replaceAll(" ", "");
                    epubWriter.write(book, new FileOutputStream(novel.options.saveLocation
                            + "/" + epubFilename));
                    break;
                default:
                    epubFilename = (novel.metadata.bookAuthor + " - " + novel.metadata.bookTitle + ".epub").replaceAll(" ","");
                    epubWriter.write(book, new FileOutputStream(novel.options.saveLocation
                            + "/" + epubFilename));
                    break;
            }
            novel.epubFilename = epubFilename;
            // Delete image and chapter files
            Path chaptersFolder = Paths.get(novel.options.saveLocation + "/chapters");
            Path imagesFolder = Paths.get(novel.options.saveLocation + "/images");

            if (Files.exists(imagesFolder)) GrabberUtils.deleteFolderAndItsContent(imagesFolder);
            if (Files.exists(chaptersFolder)) GrabberUtils.deleteFolderAndItsContent(chaptersFolder);

        } catch (FileNotFoundException e) {
            //novel.gui.appendText(novel.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extra pages for EPUB
     */

    public void createCoverPage() {
        // Write buffered cover to save location
        if (novel.metadata.bufferedCover != null && novel.metadata.bookCover != null) {
            File dir = new File(novel.options.saveLocation + File.separator + "images");
            if(!dir.exists()) dir.mkdirs();
            File coverFile = new File(dir + File.separator + novel.metadata.bufferedCoverName);
            String imgExt = novel.metadata.bufferedCoverName.substring(novel.metadata.bufferedCoverName.lastIndexOf(".") + 1);
            try {
                if(coverFile.createNewFile()) ImageIO.write(novel.metadata.bufferedCover, imgExt, coverFile);
            } catch (IIOException e) {
                try {
                    int width = novel.metadata.bufferedCover.getWidth();
                    int height = novel.metadata.bufferedCover.getHeight();
                    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                    int px[] = new int[width * height];
                    novel.metadata.bufferedCover.getRGB(0, 0, width, height, px, 0, width);
                    output.setRGB(0, 0, width, height, px, 0, width);
                    ImageIO.write(output, imgExt, coverFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[ERROR]Could not write cover image to file.");
                if(init.window != null && !novel.options.window.equals("checker")) {
                    init.window.appendText(novel.options.window, "[ERROR]Could not write cover image to file.");
                }
            }
        }
        // Create Cover page
        String fileName = "cover_Page";
        String filePath = novel.options.saveLocation + File.separator + "chapters" + File.separator + fileName +".html";
        String imageName = novel.metadata.bookCover;
        imageName = GrabberUtils.getFileName(imageName);
        try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
            out.print(htmlHead + "<div class=\"cover\" style=\"padding: 0pt; margin:0pt; text-align: center; padding:0pt; margin: 0pt;\">" + NL);
            out.println("<img src=\"" + imageName + "\" class=\"cover.img\" style=\"width: 600px; height: 800px;\" />");
            out.print("</div>" + NL + htmlFoot);
            novel.extraPages.add(fileName);
        } catch (IOException e) {
            if(init.window != null && !novel.options.window.equals("checker")) {
                init.window.appendText(novel.options.window,e.getMessage());

            }
            e.printStackTrace();
        }
    }

    public void createToc() {
        String fileName = "table_of_contents";
        String filePath = novel.options.saveLocation + File.separator + "chapters" + File.separator + fileName+  ".html";
        try (PrintStream out = new PrintStream(filePath , "UTF-8")) {
            out.print(htmlHead + "<b>Table of Contents</b>" + NL + "<p style=\"text-indent:0pt\">" + NL);
            for (Chapter chapter: novel.chapters) {
                if(chapter.status == 1)
                    out.println("<a href=\"" + chapter.fileName + ".html\">" + chapter.name + "</a><br/>");
            }
            out.print("</p>" + NL + htmlFoot);
            novel.extraPages.add(fileName);
        } catch (IOException e) {
            if(init.window != null && !novel.options.window.equals("checker")) {
                init.window.appendText(novel.options.window,e.getMessage());

            }
            e.printStackTrace();
        }
    }

    public void createDescPage() {
        String fileName = "desc_Page";
        String filePath = novel.options.saveLocation + File.separator + "chapters" + File.separator + fileName + ".html";
        try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
            out.print(htmlHead + "<div><b>Description</b>" + NL);
            out.println("<p>" + novel.metadata.bookDesc.get(0) + "</p>");
            out.print("</div>" + NL + htmlFoot);
            novel.extraPages.add(fileName);
        } catch (IOException e) {
            if(init.window != null && !novel.options.window.equals("checker")) {
                init.window.appendText(novel.options.window,e.getMessage());
            }
            e.printStackTrace();
        }
    }
}
