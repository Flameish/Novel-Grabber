package grabber.sources;

import grabber.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class quotev_com implements Source {
    private final String name = "Quotev";
    private final String url = "https://quotev.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public quotev_com(Novel novel) {
        this.novel = novel;
    }

    public quotev_com() {
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
        try {
            toc = getTocHeadless();
            Elements chapterLinks = toc.select("#rselectList a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    private Document getTocHeadless() {
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        return toc;
    }

    public Element getChapterContent(Chapter chapter) {
        Document doc = getPageHeadless(chapter);
        return doc.select("#rescontent").first();
    }

    private Document getPageHeadless(Chapter chapter) {
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst("title");
            Element author = toc.selectFirst(".quizAuthorList a");
            Element desc = toc.selectFirst("#qdesct");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:content") : "");

            Elements tags = toc.select("#quizViewTagTop a");
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
