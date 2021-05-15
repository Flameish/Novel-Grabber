package grabber.sources;

import grabber.*;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class alphapolis_co_jp implements Source {
    private final String name = "AlphaPolis";
    private final String url = "https://www.alphapolis.co.jp/novel/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public alphapolis_co_jp() {
    }

    public alphapolis_co_jp(Novel novel) {
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
        try {
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            Elements chapterLinks = toc.select(".episodes a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.select("span.title").text(), chapterLink.attr("abs:href")));
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
        Document doc = getPageHeadless(chapter.chapterURL);
        String extractedContentHtml = findChapter(doc, chapter.chapterURL);
        return Jsoup.parse(extractedContentHtml);
    }

    private Document getPageHeadless(String chapterURL) {
        if (novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window);
        }
        novel.headlessDriver.driver.navigate().to(chapterURL);
        novel.cookies.forEach((key, value) -> novel.headlessDriver.driver.manage().addCookie(new Cookie(key, value)));
        novel.headlessDriver.driver.navigate().to(chapterURL);
        novel.headlessDriver.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        WebElement chapterElement = novel.headlessDriver.driver.findElement(By.cssSelector(".novel-body"));
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
            Element title = toc.selectFirst(".content-main .title");
            Element author = toc.selectFirst(".content-main .author a");
            Element desc = toc.selectFirst(".content-main .abstract");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(toc.selectFirst(".cover img").attr("abs:src"));

            Elements tags = toc.select(".content-tags a");
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
