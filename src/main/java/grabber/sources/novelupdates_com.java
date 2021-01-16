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
import org.openqa.selenium.WebElement;
import system.data.Settings;
import system.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class novelupdates_com implements Source {
    private final Novel novel;
    private Document toc;

    public novelupdates_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            Connection.Response res = Jsoup.connect(novel.novelLink)
                    .method(Connection.Method.GET)
                    .execute();
            toc = res.parse();
            res = Jsoup.connect("https://www.novelupdates.com/wp-admin/admin-ajax.php")
                    .method(Connection.Method.POST)
                    .cookies(res.cookies())
                    .data("action", "nd_getchapters")
                    .data("mypostid", toc.select("#mypostid").attr("value"))
                    .execute();
            Document doc = res.parse();
            for (Element chapterLink : doc.select("li a:not([title])")) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            Collections.reverse(chapterList);
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc;
            if (Settings.getInstance().isNuHeadless()) {
                doc = getPageHeadless(chapter.chapterURL);
                if (!doc.select(".entry-meta").isEmpty()) {
                    String fullChapterUrl = doc.selectFirst(".entry-content a").attr("abs:href");
                    if (!fullChapterUrl.isEmpty()) {
                        if (init.gui != null) {
                            init.gui.appendText(novel.window, "[INFO] WordPress \"Pre-Chapter\" detected. " +
                                    "Trying to navigate to full chapter: " + fullChapterUrl);
                        }
                        doc = getPageHeadless(fullChapterUrl);
                    }

                }
            } else {
                doc = Jsoup.connect(chapter.chapterURL)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                if (!doc.select(".entry-meta").isEmpty()) {

                    String fullChapterUrl = doc.selectFirst(".entry-content a").attr("abs:href");
                    if (!fullChapterUrl.isEmpty()) {
                        if (init.gui != null) {
                            init.gui.appendText(novel.window, "[INFO] WordPress \"Pre-Chapter\" detected. " +
                                    "Trying to navigate to full chapter: " + fullChapterUrl);
                        }
                        doc = Jsoup.connect(fullChapterUrl)
                                .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                                .get();
                    }
                }
            }
            String extractedContentHtml = findChapter(doc, chapter.chapterURL);
            chapterBody = Jsoup.parse(extractedContentHtml);
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not detect chapter on: "
                    + chapter.chapterURL + "(" + e.getMessage() + ")", e);
        }
        return chapterBody;
    }


    private Document getPageHeadless(String chapterURL) {
        if (novel.headlessDriver == null) {
            novel.headlessDriver = new Driver(novel.window, novel.browser);
        }
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
            metadata.setTitle(toc.select(".seriestitlenu").first().text());
            metadata.setAuthor(toc.select("#authtag").first().text());
            metadata.setDescription(toc.select("#editdescription").first().text());
            metadata.setBufferedCover(toc.select(".seriesimg img").attr("abs:src"));

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

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
