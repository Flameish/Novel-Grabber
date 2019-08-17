import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.*;

public class ToEpub {
    public ToEpub(Download currGrab) {
        writeEpub(currGrab);
    }

    /**
     * Deletes Folder with all of its content
     * <p>
     * path to folder which should be deleted
     */
/*    static void deleteFolderAndItsContent(final Path folder) throws IOException {
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }*/
    private static InputStream getResource(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    private static Resource getResource(String path, String href) throws IOException {
        return new Resource(getResource(path), href);
    }

    private void writeEpub(Download currGrab) {
        try {
            NovelGrabberGUI.appendText(currGrab.window, "[INFO]Writing epub...");
            // Create new Book
            Book book = new Book();
            Metadata metadata = book.getMetadata();
            // Set the title
            if (currGrab.bookTitle != null && !currGrab.bookTitle.isEmpty()) {
                metadata.addTitle(currGrab.bookTitle);
            } else {
                metadata.addTitle("Unknown");
            }
            // Add an Author
            if (currGrab.bookAuthor != null && !currGrab.bookAuthor.isEmpty() && !currGrab.bookAuthor.equals("null")) {
                metadata.addAuthor(new Author(currGrab.bookAuthor));
            } else {
                metadata.addAuthor(new Author("Unknown"));
            }
            // Set cover image
            if (currGrab.bookCover != null && !currGrab.bookCover.isEmpty()) {
                book.setCoverImage(getResource(currGrab.bookCover, "cover.jpg"));
            } else {
                book.setCoverImage(new Resource(getClass().getResourceAsStream("images/default_cover.png"), "default_cover.png"));
            }
            // Add Chapters
            for (int i = 0; i < currGrab.successfulFilenames.size(); i++) {
                book.addSection(currGrab.successfulChapterNames.get(i), getResource(currGrab.saveLocation + "/chapters/"
                        + currGrab.successfulFilenames.get(i) + ".html", currGrab.successfulFilenames.get(i) + ".html"));
            }
            // Add used images
            if (currGrab.getImages) {
                for (String imageName : currGrab.imageNames) {
                    book.getResources().add(getResource(currGrab.saveLocation + "/images/" + imageName, imageName));
                }
            }
            // Add css file
            book.getResources().add(new Resource(getClass().getResourceAsStream("files/default.css"), "default.css"));
            // Create EpubWriter
            EpubWriter epubWriter = new EpubWriter();
            // Write the Book as Epub
            try (FileOutputStream epubStream = new FileOutputStream(currGrab.saveLocation + "/" + currGrab.bookTitle + ".epub")) {
                epubWriter.write(book, epubStream);
            }
            NovelGrabberGUI.appendText(currGrab.window, "[INFO]Epub successfully created.");

/*            Path chaptersFolder = Paths.get(currGrab.saveLocation+"/chapters");
            Path imagesFolder = Paths.get(currGrab.saveLocation+"/images");

            deleteFolderAndItsContent(chaptersFolder);
            deleteFolderAndItsContent(imagesFolder);*/

        } catch (FileNotFoundException e) {
            NovelGrabberGUI.appendText(currGrab.window, "[ERROR]" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
