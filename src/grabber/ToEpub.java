package grabber;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ToEpub {
    private FileInputStream inputStream;
    private Resource resource;

    ToEpub(AutoNovel currGrab) {
        writeEpub(currGrab);
    }

    private void writeEpub(AutoNovel currGrab) {
        try {
            currGrab.gui.appendText(currGrab.window, "[INFO]Writing epub...");
            // Create new Book
            Book book = new Book();
            Metadata metadata = book.getMetadata();
            // Title
            if (currGrab.bookTitle != null && !currGrab.bookTitle.isEmpty()) {
                metadata.addTitle(currGrab.bookTitle);
            } else {
                metadata.addTitle("Unknown");
            }
            // Author
            if (currGrab.bookAuthor != null && !currGrab.bookAuthor.isEmpty() && !currGrab.bookAuthor.equals("null")) {
                metadata.addAuthor(new Author(currGrab.bookAuthor));
            } else {
                metadata.addAuthor(new Author("Unknown"));
            }
            // Subjects
            if (currGrab.bookSubjects != null && !currGrab.bookSubjects.isEmpty()) {
                metadata.setSubjects(currGrab.bookSubjects);
            }
            // Description
            if (currGrab.bookDesc != null && !currGrab.bookDesc.get(0).isEmpty() && !currGrab.noDescription) {
                metadata.setDescriptions(currGrab.bookDesc);
            }
            // Set cover image & page
            if (currGrab.bookCover != null && !currGrab.bookCover.isEmpty()) {
                if (currGrab.window.equals("auto")) {
                    // Add cover image as a resource
                    inputStream = new FileInputStream(currGrab.saveLocation + "/images/" + currGrab.bookCover);
                    resource = new Resource(inputStream, currGrab.bookCover);
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();
                } else {
                    // Add manual cover image. Its saved as a full path
                    inputStream = new FileInputStream(currGrab.bookCover);
                    resource = new Resource(inputStream, shared.getFileName(currGrab.bookCover));
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();
                }
                // Adding cover page
                inputStream = new FileInputStream(currGrab.saveLocation + "/chapters/"
                        + currGrab.successfulExtraPagesFilenames.get(0) + ".html");
                resource = new Resource(inputStream, currGrab.successfulExtraPagesFilenames.get(0) + ".html");
                book.setCoverPage(resource);
                book.addSection(currGrab.successfulExtraPagesNames.get(0), resource);
                inputStream.close();
            } else {
                book.setCoverImage(new Resource(getClass().getResourceAsStream("/images/cover_placeholder.png"), "cover_placeholder.png"));

            }
            // Description page
            if (currGrab.bookDesc != null && !currGrab.bookDesc.get(0).isEmpty() && !currGrab.noDescription) {
                inputStream = new FileInputStream(currGrab.saveLocation + "/chapters/"
                        + currGrab.successfulExtraPagesFilenames.get(2) + ".html");
                resource = new Resource(inputStream, currGrab.successfulExtraPagesFilenames.get(2) + ".html");
                book.addSection(currGrab.successfulExtraPagesNames.get(2), resource);
                inputStream.close();
            }
            // Table of Contents
            inputStream = new FileInputStream(currGrab.saveLocation + "/chapters/"
                    + currGrab.successfulExtraPagesFilenames.get(1) + ".html");
            resource = new Resource(inputStream, currGrab.successfulExtraPagesFilenames.get(1) + ".html");
            book.addSection(currGrab.successfulExtraPagesNames.get(1), resource);
            inputStream.close();

            // Chapters
            for (int i = 0; i < currGrab.successfulFilenames.size(); i++) {
                inputStream = new FileInputStream(currGrab.saveLocation + "/chapters/"
                        + currGrab.successfulFilenames.get(i) + ".html");
                resource = new Resource(inputStream, currGrab.successfulFilenames.get(i) + ".html");
                book.addSection(currGrab.successfulChapterNames.get(i), resource);
                inputStream.close();
            }
            // Add used images
            if (currGrab.getImages) {
                for (String imageName : currGrab.imageNames) {
                    inputStream = new FileInputStream(currGrab.saveLocation + "/images/" + imageName);
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
            epubWriter.write(book, new FileOutputStream(currGrab.saveLocation + File.separator + currGrab.bookTitle + ".epub"));
            currGrab.gui.appendText(currGrab.window, "[INFO]Epub successfully created.");

            // Delete image and chapter files
            Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
            Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");

            if (Files.exists(imagesFolder)) shared.deleteFolderAndItsContent(imagesFolder);
            if (Files.exists(chaptersFolder)) shared.deleteFolderAndItsContent(chaptersFolder);

        } catch (FileNotFoundException e) {
            currGrab.gui.appendText(currGrab.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
