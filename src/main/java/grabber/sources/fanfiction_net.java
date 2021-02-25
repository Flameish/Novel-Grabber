package grabber.sources;

import grabber.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class fanfiction_net implements Source {
    private final String name = "FanFiction";
    private final String url = "https://fanfiction.net";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public fanfiction_net(Novel novel) {
        this.novel = novel;
    }

    public fanfiction_net() {
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
        toc = getTocHeadless();
        System.out.println(toc);
        Elements chapterLinks = toc.select("#chap_select option");
        String fullLink = toc.select("link[rel=canonical]").attr("abs:href");
        String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
        String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
        chapterLinks = chapterLinks.select("option[value]");
        for (int i = 0; i < chapterLinks.size() / 2; i++) {
            chapterList.add(new Chapter(chapterLinks.get(i).text(), baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
        }
        return chapterList;
    }


    private Document getPageStatic() throws IOException {
        return Jsoup.connect(novel.novelLink)
                .cookies(novel.cookies)
                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                .get();
    }

    private Document getTocHeadless() {
        if (novel.headlessDriver == null) novel.headlessDriver = new Driver(novel.window);
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(novel.novelLink);
        //novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#chap_select option")));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        return toc;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            chapterBody = doc.select("#storytext").first();
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            metadata.setTitle(toc.select("#profile_top b.xcontrast_txt").first().text());
            metadata.setAuthor(toc.select("#profile_top a.xcontrast_txt").first().text());
            metadata.setDescription(toc.select("div.xcontrast_txt").first().text());
            metadata.setBufferedCover(toc.select("#profile_top img.cimage").attr("abs:src"));

        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
