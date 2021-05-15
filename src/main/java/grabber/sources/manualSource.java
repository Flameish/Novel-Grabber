package grabber.sources;

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
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class manualSource implements Source {
    private final String name = "Manual";
    private final String url = "https://google.com/";
    private final boolean canHeadless = false;
    private String chapterContainer;
    private Novel novel;

    public manualSource() {
    }

    public manualSource(Novel novel) {
        this.novel = novel;
        this.chapterContainer = init.gui.manChapterContainer.getText();
    }

    /**
     * Retrieves all links containing a href tag and displays them on the GUI.
     */
    public String getName() {
        return name;
    }

    public boolean canHeadless() {
        return canHeadless;
    }

    public String toString() {
        return name;
    }

    public String getUrl() {
        return url;
    }

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
        if (novel.tableOfContent != null) {
            links = novel.tableOfContent.select("a[href]");
        }
        for (Element chapterLink : links) {
            if (chapterLink.attr("abs:href").startsWith("http") && !chapterLink.text().isEmpty()) {
                Chapter chapter = new Chapter(chapterLink.text(), chapterLink.attr("abs:href"));
                GUI.manLinkListModel.addElement(chapter);
            }
        }
        init.gui.appendText("manual", links.size() + " links retrieved.");
        return null;
    }

    private Document getTocHeadless() {
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        return toc;
    }

    private Document getTocStatic() {
        try {
            return Jsoup.connect(novel.novelLink).cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return null;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc;
            if (novel.useHeadless) {
                doc = getPageHeadless(chapter);
            } else {
                doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
            }
            if (novel.autoDetectContainer || chapterContainer == null || chapterContainer.isEmpty()) {
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
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not detect chapter on: " + chapter.chapterURL);
        }
        return chapterBody;
    }

    private String findChapter(Document doc, String URL) {
        Readability4J readability4J = new Readability4JExtended(URL, doc.html());
        Article article = readability4J.parse();
        return article.getContent();
    }

    private Document getPageHeadless(Chapter chapter) {
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        if (chapterContainer.isEmpty()) { // Wait 5 seconds for everything to finish loading
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

}
