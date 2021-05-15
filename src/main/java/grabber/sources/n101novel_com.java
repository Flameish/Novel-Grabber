package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class n101novel_com implements Source {
    private final String name = "101novel";
    private final String url = "https://www.101novel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public n101novel_com(Novel novel) {
        this.novel = novel;
    }

    public n101novel_com() {
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
            if (novel.novelLink.contains("/txt/")) {
                toc = Jsoup.connect(novel.novelLink)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                Document chapterPage = Jsoup.connect(toc.selectFirst("a.btnlink:contains(全文閱讀)").attr("abs:href"))
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                Elements chapterLinks = chapterPage.select("tbody a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
            }
            if (novel.novelLink.contains("/ck101/")) {
                toc = Jsoup.connect(novel.novelLink)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                Elements chapterLinks = toc.select("tbody a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                toc = Jsoup.connect(toc.selectFirst(".bread-crumbs li a:contains(TXT)").attr("abs:href"))
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
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
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst("p");
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
            Element title = toc.selectFirst("tbody span");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setBufferedCover(toc.selectFirst("tbody img").attr("abs:src"));
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
