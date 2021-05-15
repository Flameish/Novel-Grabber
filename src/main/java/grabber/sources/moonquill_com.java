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

public class moonquill_com implements Source {
    private final String name = "MoonQuill";
    private final String url = "https://moonquill.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public moonquill_com(Novel novel) {
        this.novel = novel;
    }

    public moonquill_com() {
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
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select("#toc a:has(h3)");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody;
        Document doc = getPageHeadless(chapter);
        chapterBody = doc.select("#content").first();
        return chapterBody;
    }

    private Document getPageHeadless(Chapter chapter) {
        if (novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window);
        }
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content p")));
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            metadata.setTitle(toc.select("h1.card-header-title").first().text());
            metadata.setAuthor(toc.select("h2.card-title:nth-child(2) > a:nth-child(1)").first().text());
            metadata.setDescription(toc.select("#syn > div:nth-child(1) > div:nth-child(1)").first().text());
            metadata.setBufferedCover(toc.select("img.w-100").attr("abs:src"));

            Elements tags = toc.select("span.badge-primary");
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
