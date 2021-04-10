package grabber.sources;

import grabber.*;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import net.dankito.readability4j.extended.Readability4JExtended;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.Config;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class novelupdates_com implements Source {
    private final String name = "Novel Updates";
    private final String url = "https://novelupdates.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public novelupdates_com(Novel novel) {
        this.novel = novel;
    }

    public novelupdates_com() {
    }

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
        List<Chapter> chapterList = new ArrayList();
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        novel.headlessDriver.driver.findElement(By.cssSelector("span.my_popupreading_open")).click();
        novel.headlessDriver.wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#my_popupreading ol.sp_chp li a[href]")));
        toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        for (Element chapterLink : toc.select("div#my_popupreading ol.sp_chp li a[href]:not(:has(i))")) {
            chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        Collections.reverse(chapterList);
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = getPageHeadless(chapter.chapterURL);
            String extractedContentHtml = findChapter(doc, chapter.chapterURL);
            chapterBody = Jsoup.parse(extractedContentHtml);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not detect chapter on: "
                    + chapter.chapterURL + "(" + e.getMessage() + ")", e);
        }
        return chapterBody;
    }


    private Document getPageHeadless(String chapterURL) {
        if (novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window);
        }
        novel.headlessDriver.driver.navigate().to(chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapterURL);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    private String findChapter(Document doc, String URL) {
        Readability4J readability4J = new Readability4JExtended(URL, doc.html());
        Article article = readability4J.parse();
        return article.getContent();
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst(".seriestitlenu");
            Element author = toc.selectFirst("#authtag");
            Element desc = toc.selectFirst("#editdescription");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(toc.selectFirst(".seriesimg img").attr("abs:src"));

            Elements tags = toc.select("#seriesgenre a");
            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
                subjects.add(tag.text());
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
