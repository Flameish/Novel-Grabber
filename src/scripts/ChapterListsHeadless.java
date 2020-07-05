package scripts;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import system.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterListsHeadless {
    public static void getList(Novel novel) {
        System.out.println("[INFO]Fetching chapterlist...");
        if(init.window != null) {
            init.window.appendText(novel.options.window, "[INFO]Fetching novel info...");
        }

        switch(novel.host.url) {
            case "https://royalroad.com/":
                novel.chapters = royalroad(novel);
                break;
            case "https://comrademao.com/":
                novel.chapters = comrademao(novel);
                break;
            case "https://creativenovels.com/":
                novel.chapters = creativenovels(novel);
                break;
            case "https://flying-lines.com/":
                novel.chapters = flyingLines(novel);
                break;
            case "https://tapread.com/":
                novel.chapters = tapread(novel);
                break;
            case "https://wordexcerpt.com/":
                novel.chapters = wordexcerpt(novel);
                break;
            case "https://webnovel.com/":
                novel.chapters = webnovel(novel);
                break;
            case "https://boxnovel.com/":
                novel.chapters = boxnovel(novel);
                break;
            case "https://wordrain69.com/":
                novel.chapters = wordrain69(novel);
                break;
            case "https://ficfun.com/":
                novel.chapters = ficfun(novel);
                break;
            case "https://dreame.com/":
                novel.chapters = dreame(novel);
                break;
            case "https://booklat.com.ph/":
                novel.chapters = booklat(novel);
                break;
            case "https://wuxiaworld.site/":
                novel.chapters = wuxiaworldsite(novel);
                break;
            default:
                novel.chapters = defaults(novel);
                break;
        }
    }

    private static List<Chapter> royalroad(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        Select chapterShow = new Select(novel.headless.driver.findElement(By.name("chapters_length")));
        chapterShow.selectByVisibleText("All");
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> comrademao(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.bookDescSelector)));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        Elements chapterLinks;
        List<Chapter> chapters = new ArrayList<>();
        while (!novel.tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.headless.driver.navigate().to(novel.tableOfContent.select(".pagination a.next").attr("abs:href"));
            novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        }
        chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> creativenovels(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul[role='tablist'] > li:nth-of-type(3) button")));
        novel.headless.driver.findElement(By.cssSelector("ul[role='tablist'] > li:nth-of-type(3) button")).click();
        novel.headless.wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".post_box")));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> flyingLines(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-tables > span:nth-child(2)")));
        novel.headless.driver.findElement(By.cssSelector(".chapter-tables > span:nth-child(2)")).click();
        // Parse html from headerless to Jsoup for faster interaction.
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        // Save table of contents doc for metadata extraction later on
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> tapread(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".tab-content")));
        novel.headless.driver.findElement(By.cssSelector(".tab-content")).click();
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-list a")));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> wordexcerpt(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li.nav-item:nth-child(2) > a:nth-child(1)")));
        novel.headless.driver.findElement(By.cssSelector("li.nav-item:nth-child(2) > a:nth-child(1)")).click();
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterLinkSelector)));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> webnovel(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/div[1]/div/ul/li[2]/a")));
        novel.headless.driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[2]/div/div/div/div[1]/div/div[1]/div/ul/li[2]/a")).click();
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".j_catalog_list a")));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> boxnovel(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-readmore")));
        novel.headless.driver.findElement(By.cssSelector(".chapter-readmore")).click();
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> wordrain69(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".chapter-readmore")));
        novel.headless.driver.findElement(By.cssSelector(".chapter-readmore")).click();
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterLinkSelector)));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> ficfun(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button-round-red")));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tempPage  = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        novel.headless.driver.findElement(By.cssSelector(".button-round-red")).click();
        novel.tableOfContent = novel.tempPage;
        baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> dreame(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".button-round-purple")));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tempPage  = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        novel.headless.driver.findElement(By.cssSelector(".button-round-purple")).click();
        novel.tableOfContent = novel.tempPage;
        baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> booklat(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#lnkRead")));
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tempPage  = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        novel.headless.driver.findElement(By.cssSelector("#lnkRead")).click();
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        Elements chaptersLinks = novel.tableOfContent.select("#ddChapter option[value]");
        List<Chapter> chapters = new ArrayList<>();
        for(Element chapterLink: chaptersLinks) {
            chapters.add(new Chapter(chapterLink.text(),novel.novelLink.replace("/Info/","/Read/") +"/"+chapterLink.attr("value")));
        }
        novel.tableOfContent = novel.tempPage;
        return chapters;
    }

    private static List<Chapter> wuxiaworldsite(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        novel.headless.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(novel.host.chapterLinkSelector)));
        Map<String, String> cookies = new HashMap<>();
        for(Cookie ck : novel.headless.driver.manage().getCookies()) {
            cookies.put(ck.getName(), ck.getValue());
        }
        novel.cookies = cookies;
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> defaults(Novel novel) {
        novel.headless.driver.navigate().to(novel.novelLink);
        String baseUrl = novel.headless.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headless.driver.getCurrentUrl(), "/", 3) + 1);
        novel.tableOfContent = Jsoup.parse(novel.headless.driver.getPageSource(), baseUrl);
        List<Chapter> chapters = new ArrayList<>();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }
}
