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

public class foxteller_com implements Source {
    private final String name = "Foxteller";
    private final String url = "https://foxteller.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public foxteller_com(Novel novel) {
        this.novel = novel;
    }

    public foxteller_com() {
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
            Elements chapterLinks = toc.select("#accordion a");
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
        chapterBody = doc.select("#chapter-content").first();
        return chapterBody;
    }

    private Document getPageHeadless(Chapter chapter) {
        if (novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window);
        }
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#chapter-content p")));
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector("body"));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        return Jsoup.parse(chapterElement.getAttribute("innerHTML"), baseUrl);
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            metadata.setTitle(toc.select(".novel-title").first().text());
            metadata.setDescription(toc.select(".novel-description").first().text());
            metadata.setBufferedCover(toc.select(".novel-featureimg img").attr("abs:src").replace("https", "http"));

            Elements tags = toc.select(".novel-tags");
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
        blacklistedTags.add("span");
        blacklistedTags.add(".in-article");
        return blacklistedTags;
    }

}
