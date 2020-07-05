package grabber;

import gui.GUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.init;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

public class Chapter implements Serializable {
    // Used to set unique filenames
    private static int chapterId = 0;
    public String name;
    public String chapterURL;
    String fileName;
    // 0 = not downloaded, 1 = successfully downloaded, 2 = failed download
    int status = 0;
    public String xhrChapterId;
    public int xhrBookId;

    public Chapter(String name, String link) {
        this.name = name;
        this.chapterURL = link;
        fileName = String.format("%05d", ++chapterId) + "-" + name.replaceAll("[^\\w]+", "-");
    }

    void saveChapter(Novel novel) {
        Document doc;
        try {
            if(novel.options.headless) {
                novel.headless.driver.navigate().to(chapterURL);
                novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterContainer)));
                WebElement chapterElement = novel.headless.driver.findElement(By.cssSelector("body"));
                String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
                doc = Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
            } else {
                switch(novel.host.url) {
                    case "https://wattpad.com/":
                        doc = Jsoup.connect(xhrRequest.wattpadGetChapterTextURL(chapterURL.substring(24, chapterURL.indexOf("-")))).timeout(30 * 1000).get();
                        break;
                    case "https://tapread.com/":
                        doc = Jsoup.parse(xhrRequest.tapReadGetChapterContent("bookId=" + xhrBookId + "&chapterId=" + xhrChapterId), "https://tapread.com/");
                        break;
                    default:
                        if(novel.cookies != null) doc = Jsoup.connect(chapterURL)
                                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0")
                                .cookies(novel.cookies)
                                .timeout(30 * 1000)
                                .get();
                        else doc = Jsoup.connect(chapterURL).timeout(30 * 1000).get();
                        break;
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            if(init.window != null) {
                init.window.appendText(novel.options.window,"[ERROR]"+e.getMessage());
            }
            status = 2;
            return;
        }
        Element chapterContent = doc.select(novel.host.chapterContainer).first();
        if (novel.host.url.equals("https://tapread.com/")) chapterContent = doc;

        if (chapterContent == null) {
            if(init.window != null) {
                init.window.appendText(novel.options.window,"[ERROR]Chapter container (" + novel.host.chapterContainer + ") not found.");
            }
            return;
        }
        // Getting the next chapter URL from the "nextChapterBtn" href for Chapter-To-Chapter.
        if(novel.options.window.equals("manual")) {
            if (!novel.nextChapterBtn.equals("NOT_SET")) novel.nextChapterURL = doc.select(novel.nextChapterBtn).first().absUrl("href");
        }

        if (novel.options.removeStyling) chapterContent.select("[style]").removeAttr("style");

        if (novel.host.blacklistedTags != null && !novel.host.blacklistedTags.isEmpty()) {
            for (String tag : novel.host.blacklistedTags) {
                if (!chapterContent.select(tag).isEmpty()) {
                    chapterContent.select(tag).remove();
                }
            }
        }
        if (novel.options.getImages) {
            for (Element image : chapterContent.select("img")) {
                GrabberUtils.downloadImage(image.absUrl("src"), novel);
            }
        }

        novel.metadata.wordCount = novel.metadata.wordCount + GrabberUtils.getWordCount(chapterContent.toString());
        if(init.window != null) {
            init.window.pagesCountLbl.setText(String.valueOf(novel.metadata.wordCount / 300));
        }

        // Create chapters folder if it doesn't exist.
        File dir = new File(novel.options.saveLocation + File.separator + "chapters");
        if (!dir.exists()) dir.mkdirs();

        try (PrintStream out = new PrintStream(dir.getPath() + File.separator + fileName + ".html", "UTF-8")) {
            for (Element image : chapterContent.select("img")) {
                // Check if the image was successfully downloaded.
                String src = image.absUrl("src");
                if (novel.imageLinks.contains(src)) {
                    // Use hashCode of src + .jpg as the image name if renaming wasn't successful.
                    String imageName = GrabberUtils.getImageName(image.attr("src"));
                    if (imageName.equals("could_not_rename_image")) {
                        imageName = src.hashCode() + ".jpg";
                    }
                    // Replace href for image to point to local path.
                    image.attr("src", imageName);
                    // Remove the img tag if image wasn't downloaded.
                } else chapterContent.select("img").remove();
            }
            // Write text content to file.
            if (novel.options.displayChapterTitle) {
                chapterContent.prepend("<span style=\"font-weight: 700; text-decoration: underline;\">" + name + "</span><br>");
            }
            out.print(EPUB.htmlHead);
            out.println(chapterContent);
            out.println(EPUB.htmlFoot);
        } catch (IOException e) {
            e.printStackTrace();
            if(init.window != null) {
                init.window.appendText(novel.options.window, "[ERROR]"+e.getMessage());
            }
        } finally {
            status = 1;
            if(init.window != null) {
                init.window.appendText(novel.options.window, "[INFO]"+name+" saved.");
            }
            System.out.println("[INFO]"+name+" saved.");
        }
    }

    @Override
    public String toString() {
            return name;
    }
}