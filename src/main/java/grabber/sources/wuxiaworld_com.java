package grabber.sources;

import grabber.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class wuxiaworld_com implements Source {
    private final String name = "Wuxiaworld.com";
    private final String url = "https://wuxiaworld.com";
    private final boolean canHeadless = true;
    private Novel novel;
    private Document toc;

    public wuxiaworld_com(Novel novel) {
        this.novel = novel;
    }

    public wuxiaworld_com() {
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
            if (Config.getInstance().getHeadlessList().contains(name)) {
                toc = getTocHeadless();
            } else {
                toc = getPageStatic();
            }
            Elements chapterLinks = toc.select("#accordion .chapter-item a");
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
        novel.headlessDriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#accordion .chapter-item a")));
        String baseUrl = novel.headlessDriver.driver.getCurrentUrl().substring(0, GrabberUtils.ordinalIndexOf(novel.headlessDriver.driver.getCurrentUrl(), "/", 3) + 1);
        Document toc = Jsoup.parse(novel.headlessDriver.driver.getPageSource(), baseUrl);
        novel.headlessDriver.driver.close();
        novel.headlessDriver = null;
        return toc;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc;
            if (novel.cookies != null) {
                doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            } else {
                doc = Jsoup.connect(chapter.chapterURL).get();
            }
            chapterBody = doc.select(".p-15 .fr-view").first();
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
            Element title = toc.selectFirst(".novel-body h2");
            Element author = toc.selectFirst(".novel-body div:contains(Author) dd");
            Element desc = toc.selectFirst(".fr-view:not(.pt-10)");
            Element cover = toc.selectFirst(".novel-left img");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");

            Elements tags = toc.select(".genres a");
            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
                subjects.add(tag.text());
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List<String> blacklistedTags = new ArrayList<>();
        blacklistedTags.add("a.chapter-nav");
        return blacklistedTags;
    }

}
