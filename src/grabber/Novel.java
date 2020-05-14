package grabber;

import gui.GUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Novel {
    public GUI gui;
    public List<Chapter> chapters;
    public Metadata metadata;
    public Options options;
    public HostSettings host;
    public Driver headless;

    Document tableOfContent;
    public String novelLink;
    String nextChapterBtn = "NOT_SET";
    String nextChapterURL;
    public boolean killTask;
    private boolean reGrab = false;
    List<String> extraPages = new ArrayList<>();
    List<String> imageLinks = new ArrayList<>();
    List<String> imageNames = new ArrayList<>();

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

    public Novel() {}
    public Novel(GUI gui) {
        this.gui = gui;
        String hostname = gui.autoHostSelection.getSelectedItem().toString();
        novelLink  = gui.chapterListURL.getText();
        host = new HostSettings(hostname);
        metadata = new Metadata(this);
        options = new Options();
        chapters = new ArrayList();
    }

    public void getChapterList() {
        gui.appendText("auto", "[INFO]Fetching novel info...");
        // Headless
        if(options.headless) {
            headless = new Driver(this);
            chapters = headless.getChapterList();
            // Static
        } else {
            // Custom chapter selection
            chapters = host.getChapterList(this);
        }
    }

    public void downloadChapters() throws Exception {
        gui.setMaxProgress(options.window, options.lastChapter-options.firstChapter+1);
        if(reGrab) {
            metadata.wordCount = 0;
            for(Chapter chapter: chapters) chapter.status = 0;
        }
        if(options.invertOrder) Collections.reverse(chapters); // Will get un-reversed for potential re-grab in report();
        // -1 since chapter numbers start at 1
        if(options.headless)    headless = new Driver(this);
        for(int i = options.firstChapter-1; i < options.lastChapter; i++) {
            if(killTask) {
                // Remove already downloaded images and chapters
                try {
                    Path chaptersFolder = Paths.get(options.saveLocation + "/chapters");
                    Path imagesFolder = Paths.get(options.saveLocation + "/images");
                    if (Files.exists(imagesFolder)) GrabberUtils.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) GrabberUtils.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    gui.appendText(options.window, e.getMessage());
                }
                throw new Exception("Grabbing stopped.");
            }
            chapters.get(i).saveChapter(this);
            gui.updateProgress(options.window);
            GrabberUtils.sleep(options.waitTime);
        }
        reGrab = true;
    }

    public void getMetadata() {
        metadata.getTitle();
        metadata.getDesc();
        metadata.getAuthor();
        metadata.getTags();
        metadata.getChapterNumber();
        metadata.getCover();
    }

    /**
     * Extra pages for EPUB
      */
    public void createCoverPage() {
        // Write buffered cover to save location
        if (metadata.bufferedCover != null && metadata.bookCover != null) {
            try {
                File outputfile = new File(options.saveLocation + File.separator + "images" + File.separator + metadata.bufferedCoverName);
                if (!outputfile.exists()) outputfile.mkdirs();
                ImageIO.write(metadata.bufferedCover, metadata.bufferedCoverName.substring(metadata.bufferedCoverName.lastIndexOf(".") + 1), outputfile);
            } catch (IOException e) {
                gui.appendText(options.window, "[ERROR]Could not write cover image to file.");
            }
        }
        String fileName = "cover_Page";
        String filePath = options.saveLocation + File.separator + "chapters" + File.separator + fileName +".html";
        String imageName = metadata.bookCover;
        imageName = GrabberUtils.getFileName(imageName);
        try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
            out.print(htmlHead + "<div class=\"cover\" style=\"padding: 0pt; margin:0pt; text-align: center; padding:0pt; margin: 0pt;\">" + NL);
            out.println("<img src=\"" + imageName + "\" class=\"cover.img\" style=\"width: 600px; height: 800px;\" />");
            out.print("</div>" + NL + htmlFoot);
            extraPages.add(fileName);
        } catch (IOException e) {
            gui.appendText(options.window, e.getMessage());
            e.printStackTrace();
        }
    }

    public void createToc() {
        String fileName = "table_of_contents";
        String filePath = options.saveLocation + File.separator + "chapters" + File.separator + fileName+  ".html";
        try (PrintStream out = new PrintStream(filePath , "UTF-8")) {
            out.print(htmlHead + "<b>Table of Contents</b>" + NL + "<p style=\"text-indent:0pt\">" + NL);
            for (Chapter chapter: chapters) {
                if(chapter.status == 1)
                    out.println("<a href=\"" + chapter.fileName + ".html\">" + chapter.name + "</a><br/>");
            }
            out.print("</p>" + NL + htmlFoot);
            extraPages.add(fileName);
        } catch (IOException e) {
            gui.appendText(options.window, e.getMessage());
            e.printStackTrace();
        }
    }

    public void createDescPage() {
        String fileName = "desc_Page";
        String filePath = options.saveLocation + File.separator + "chapters" + File.separator + fileName + ".html";
        try (PrintStream out = new PrintStream(filePath, "UTF-8")) {
            out.print(htmlHead + "<div><b>Description</b>" + NL);
            out.println("<p>" + metadata.bookDesc.get(0) + "</p>");
            out.print("</div>" + NL + htmlFoot);
            extraPages.add(fileName);
        } catch (IOException e) {
            gui.appendText(options.window, e.getMessage());
            e.printStackTrace();
        }
    }

    public void createEPUB() {
        EPUB epub = new EPUB(this);
    }

    /**
     Prints potential failed chapters. Reverses the chapter list again for next grabbing
     and closes the headless driver if used.
     */
    public void report() {
        gui.appendText(options.window, "[INFO]Finished.");
        if(options.invertOrder) Collections.reverse(chapters);
        // Print failed chapters
        for(Chapter chapter: chapters) {
            if(chapter.status == 2)
                gui.appendText(options.window,"[WARN]Failed to grab: " +chapter.name);
        }
        if(options.headless) headless.close();
    }
}