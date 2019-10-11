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

    ToEpub(Download currGrab) {
        writeEpub(currGrab);
    }

    /**
     * Deletes Folder with all of its content
     * <p>
     * path to folder which should be deleted
     */

    private static InputStream getResource(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    private static Resource getResource(String path, String href) throws IOException {
        return new Resource(getResource(path), href);
    }

    private void writeEpub(Download currGrab) {
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
            // Set cover image & page
            if (currGrab.bookCover != null && !currGrab.bookCover.isEmpty()) {
                if (currGrab.window.equals("auto")) {
                    // Add cover image as a resource
                    inputStream = new FileInputStream(currGrab.saveLocation + "/images/" + currGrab.bookCover);
                    resource = new Resource(inputStream, currGrab.bookCover);
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();

/*                    inputStream = new FileInputStream(currGrab.saveLocation + "/images/" + currGrab.bookCover);
                    resource = new Resource(inputStream, "cover.jpg");
                    inputStream.close();*/
                } else {
                    // Add manual cover image. Its saved as a full path
                    inputStream = new FileInputStream(currGrab.bookCover);
                    resource = new Resource(inputStream, Shared.getFileName(currGrab.bookCover));
                    book.getResources().add(resource);
                    book.setCoverImage(resource);
                    inputStream.close();

/*                    inputStream = new FileInputStream(currGrab.bookCover);
                    resource = new Resource(inputStream, "cover.jpg");
                    inputStream.close();*/
                }
                // Adding cover page
                inputStream = new FileInputStream(currGrab.saveLocation + "/chapters/"
                        + currGrab.successfulFilenames.get(currGrab.successfulFilenames.size() - 2) + ".html");
                resource = new Resource(inputStream, currGrab.successfulFilenames.get(currGrab.successfulFilenames.size() - 2) + ".html");
                book.setCoverPage(resource);
                book.addSection(currGrab.successfulChapterNames.get(currGrab.successfulChapterNames.size() - 2), resource);
                inputStream.close();
            } else {
                book.setCoverImage(new Resource(getClass().getResourceAsStream("/images/cover_placeholder.png"), "cover_placeholder.png"));

            }
            // Table of Contents
            inputStream = new FileInputStream(currGrab.saveLocation + "/chapters/"
                    + currGrab.successfulFilenames.get(currGrab.successfulFilenames.size() - 1) + ".html");
            resource = new Resource(inputStream, currGrab.successfulFilenames.get(currGrab.successfulFilenames.size() - 1) + ".html");
            book.addSection(currGrab.successfulChapterNames.get(currGrab.successfulChapterNames.size() - 1), resource);
            inputStream.close();

            // Chapters
            for (int i = 0; i < currGrab.successfulFilenames.size() - 2; i++) {
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

            Path chaptersFolder = Paths.get(currGrab.saveLocation + "/chapters");
            Path imagesFolder = Paths.get(currGrab.saveLocation + "/images");

            if (Files.exists(imagesFolder)) Shared.deleteFolderAndItsContent(imagesFolder);
            if (Files.exists(chaptersFolder)) Shared.deleteFolderAndItsContent(chaptersFolder);


        } catch (FileNotFoundException e) {
            currGrab.gui.appendText(currGrab.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
