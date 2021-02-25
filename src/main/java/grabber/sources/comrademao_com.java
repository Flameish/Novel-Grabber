package grabber.sources;

import grabber.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;

public class comrademao_com implements Source {
    private final String name = "Comrade Mao";
    private final String url = "https://comrademao.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public comrademao_com() {
    }

    public comrademao_com(Novel novel) {
        this.novel = novel;
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
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tbody a")));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        Elements chapterLinks;
        while (!toc.select(".next").isEmpty()) {
            chapterLinks = toc.select("tbody a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.headlessDriver.driver.navigate().to(toc.select(".next").attr("abs:href"));
            novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tbody a")));
            baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
            toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        }
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        chapterLinks = toc.select("tbody a");
        for (Element chapterLink : chapterLinks) {
            chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        Collections.reverse(chapterList);
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapter.chapterURL);
        novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("article")));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document doc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        return doc.selectFirst("article");
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst("title");
            metadata.setTitle(title != null ? title.text().substring(0, title.text().indexOf("–") - 1) : "");
            metadata.setDescription(toc.select("#Description").first().text());
            metadata.setBufferedCover(toc.select("#thumbnail img").attr("abs:src"));

            Elements tags = toc.select("#Genre a");
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
