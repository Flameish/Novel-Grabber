package grabber;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import system.init;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EPUB {
    private FileInputStream inputStream;
    private Resource resource;

    EPUB(Novel currGrab) {
        writeEpub(currGrab);
    }

    private void writeEpub(Novel novel) {
        try {
            if(init.window != null) {
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
                if (novel.options.window.equals("auto")) {
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
            epubWriter.write(book, new FileOutputStream(novel.options.saveLocation +
                    File.separator + novel.metadata.bookAuthor + " - " + novel.metadata.bookTitle + ".epub"));
            //novel.gui.appendText(novel.window, "[INFO]Epub successfully created.");

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
}
