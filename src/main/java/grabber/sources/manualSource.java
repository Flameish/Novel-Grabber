package grabber.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import grabber.*;
import gui.GUI;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import net.dankito.readability4j.extended.Readability4JExtended;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.init;

public class manualSource implements Source {
    private final String chapterContainer;
    private final Novel novel;

    public manualSource(Novel novel) {
        this.novel = novel;
        this.chapterContainer = init.gui.manChapterContainer.getText();
    }

    /**
     * Retrieves all links containing a href tag and displays them on the GUI.
     */
    public List<Chapter> getChapterList() {
        init.gui.appendText("manual", "Retrieving links from: " + novel.novelLink);
        if (novel.useHeadless) {
            novel.tableOfContent = getTocHeadless();
        } else {
            novel.tableOfContent = getTocStatic();
        }
        GUI.manLinkListModel.removeAllElements();
        // Add every link as a new chapter and add to gui
        Elements links = new Elements();
        if(novel.tableOfContent != null) {
            links = novel.tableOfContent.select("a[href]");
        }
        for (Element chapterLink : links) {
            if (chapterLink.attr("abs:href").startsWith("http") && !chapterLink.text().isEmpty()) {
                Chapter chapter = new Chapter(chapterLink.text(),chapterLink.attr("abs:href"));
                GUI.manLinkListModel.addElement(chapter);
            }
        }
        init.gui.appendText("manual", links.size() + " links retrieved.");
        return null;
    }

    private Document getTocHeadless() {
        if(novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window, novel.browser);
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        return toc;
    }

    private Document getTocStatic() {
        try {
            return Jsoup.connect(novel.novelLink)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null && !novel.window.equals("checker")) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        }
        return null;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc;
            if(novel.useHeadless) {
                doc = getPageHeadless(chapter);
            } else {
                doc = Jsoup.connect(chapter.chapterURL)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
            }
            if(novel.autoDetectContainer || chapterContainer == null || chapterContainer.isEmpty()) {
                String extractedContentHtml = findChapter(doc, chapter.chapterURL);
                chapterBody = Jsoup.parse(extractedContentHtml);
            } else {
                chapterBody = doc.select(chapterContainer).first();
            }
            // Get the next chapter URL from the "nextChapterBtn" href for Chapter-To-Chapter grabbing.
            if (!novel.nextChapterBtn.equals("NOT_SET")) {
                novel.nextChapterURL = doc.select(novel.nextChapterBtn).first().absUrl("href");
            }
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            if(init.gui != null) {
                init.gui.appendText(novel.window,"[GRABBER-ERROR]Could not detect chapter on: "
                        + chapter.chapterURL+"("+e.getMessage()+")");
            }
        }
        return chapterBody;
    }

    private String findChapter(Document doc, String URL) {
        Readability4J readability4J = new Readability4JExtended(URL, doc.html());
        Article article = readability4J.parse();
        return article.getContent();
    }

    private Document getPageHeadless(Chapter chapter) {
        if(novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window, novel.browser);
        }
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        if(chapterContainer.isEmpty()) { // Wait 5 seconds for everything to finish loading
            novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        } else { // Wait until chapter container is located
            novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(chapterContainer)));
        }
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    // Dummy
    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();
        return metadata;
    }

    // Dummy
    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

    // Dummy
    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
