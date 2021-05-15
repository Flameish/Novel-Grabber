package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import net.dankito.readability4j.extended.Readability4JExtended;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ptwxz_com implements Source {
    private final String name = "Piaotian";
    private final String url = "https://www.ptwxz.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public ptwxz_com() {
    }

    public ptwxz_com(Novel novel) {
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

            //get input stream from the URL
            InputStream inStream = new URL(novel.novelLink).openStream();

            //parse document using input stream and specify the charset
            Document doc = Jsoup.parse(inStream, "UTF-8", novel.novelLink);
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            Document chapterPage = Jsoup.connect(toc.selectFirst("caption a:contains((查看全部章节))").attr("abs:href")).get();
            Elements chapterLinks = chapterPage.select(".centent li a");
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
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            Element chapterText = doc.selectFirst("body");
            for (String tag : getBlacklistedTags()) {
                chapterText.select(tag).remove();
            }
            String extractedContentHtml = findChapter(chapterText, chapter.chapterURL);
            chapterBody = Jsoup.parse(extractedContentHtml);
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterBody;
    }

    private String findChapter(Element chapterText, String URL) {
        Readability4J readability4J = new Readability4JExtended(URL, chapterText.html());
        Article article = readability4J.parse();
        return article.getContent();
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            Element title = toc.selectFirst("h1");
            Element author = toc.selectFirst("#centerm > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2)");
            Element desc = toc.selectFirst("#centerm > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > div:nth-child(2)");
            Element cover = toc.selectFirst("div#content tbody td a img[align=right]");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text().replace("作 者：", "") : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add(".toplink");
        blacklistedTags.add("h1 a");
        blacklistedTags.add("div[align=center]");
        return blacklistedTags;
    }

}
