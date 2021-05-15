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
import java.util.Collections;
import java.util.List;

public class ranobes_net implements Source {
    private final String name = "Ranobes";
    private final String url = "https://ranobes.net/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public ranobes_net() {
    }

    public ranobes_net(Novel novel) {
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

            // Walk navigation page for chapters
            Element nextPageEle = toc.selectFirst("a[title=Go to table of contents]");
            Document navi;
            while (nextPageEle != null) {
                navi = Jsoup.connect(nextPageEle.attr("abs:href"))
                        .cookies(novel.cookies)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                Elements chapterLinks = navi.select("#dle-content div.cat_block.cat_line a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.attr("title"), chapterLink.attr("abs:href")));
                }
                nextPageEle = navi.selectFirst("span.page_next a");
            }
            Collections.reverse(chapterList);
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
            chapterBody = doc.selectFirst("#arrticle");
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
            Element title = toc.selectFirst("meta[property=og:title]");
            Element author = toc.selectFirst("h1.title span.subtitle");
            Element desc = toc.selectFirst(".cont-text[itemprop=description]");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.attr("content") : "");
            metadata.setAuthor(author != null ? author.text().replace("by ", "") : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("content") : "");

            Elements tags = toc.select("div.links[itemprop=genre] a");
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
        blacklistedTags.add("div[align=center]");
        return blacklistedTags;
    }

}
