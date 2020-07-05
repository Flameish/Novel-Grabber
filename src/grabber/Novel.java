package grabber;

import org.jsoup.nodes.Document;
import scripts.ChapterLists;
import scripts.ChapterListsHeadless;
import scripts.Logins;
import system.init;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Novel {
    public List<Chapter> chapters;
    public Metadata metadata;
    public NovelOptions options;
    public HostSettings host;
    public Driver headless;
    public Map<String, String> cookies;

    public Document tableOfContent;
    public Document tempPage;
    public String novelLink;
    String nextChapterBtn = "NOT_SET";
    String nextChapterURL;
    public boolean killTask;
    private boolean reGrab = false;
    List<String> extraPages = new ArrayList<>();
    List<String> imageLinks = new ArrayList<>();
    List<String> imageNames = new ArrayList<>();

    public Novel() {
    }

    public Novel(NovelOptions novelOptions) {
        novelLink  = novelOptions.novelLink;
        host = new HostSettings(novelOptions.hostname);
        metadata = new Metadata(this);
        options = novelOptions;
        chapters = new ArrayList();
    }

    public void getChapterList() {
        if(options.useAccount) {
            Logins.getLoginCookies(this);
        }
        if(options.headless) {
            headless = new Driver(this);
            ChapterListsHeadless.getList(this);
        } else {
            ChapterLists.getList(this);
        }
    }

    public void downloadChapters() throws Exception {
        System.out.println("[INFO]Starting download...");
        if(init.window != null) {
            init.window.setMaxProgress(options.window, options.lastChapter-options.firstChapter+1);
        }
        if(reGrab) {
            metadata.wordCount = 0;
            for(Chapter chapter: chapters) chapter.status = 0;
        }
        // Will get un-reversed for potential re-grab in report();
        if(options.invertOrder) Collections.reverse(chapters);

        if(options.headless) {
            if(headless == null) {
                headless = new Driver(this);
            }
        }
        // -1 since chapter numbers start at 1
        for(int i = options.firstChapter-1; i < options.lastChapter; i++) {
            if(killTask) {
                // Remove already downloaded images and chapters
                try {
                    Path chaptersFolder = Paths.get(options.saveLocation + "/chapters");
                    Path imagesFolder = Paths.get(options.saveLocation + "/images");
                    if (Files.exists(imagesFolder)) GrabberUtils.deleteFolderAndItsContent(imagesFolder);
                    if (Files.exists(chaptersFolder)) GrabberUtils.deleteFolderAndItsContent(chaptersFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                    if(init.window != null) {
                        init.window.appendText(options.window, e.getMessage());
                    }
                }
                throw new Exception("[INFO]Grabbing stopped.");
            }
            chapters.get(i).saveChapter(this);

            if(init.window != null) {
                init.window.updateProgress(options.window);
            }

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
     Prints potential failed chapters. Reverses the chapter list again for next grabbing
     and closes the headless driver if used.
     */
    public void report() {
        System.out.println("[INFO]Output: "+options.saveLocation + " " + metadata.bookAuthor + " - " + metadata.bookTitle + ".epub");
        if(init.window != null) {
            init.window.appendText(options.window,"[INFO]Finished.");
        }
        if(options.invertOrder) Collections.reverse(chapters);
        // Print failed chapters
        for(Chapter chapter: chapters) {
            if(chapter.status == 2)
                if(init.window != null) {
                    init.window.appendText(options.window,"[WARN]Failed to grab: " +chapter.name);
                }
        }
        if(options.headless) headless.close();
    }
}