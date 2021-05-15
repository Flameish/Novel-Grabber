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

public class wuxia_blog implements Source {
    private final String name = "Wuxia.Blog";
    private final String url = "https://www.wuxia.blog/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public wuxia_blog() {
    }

    public wuxia_blog(Novel novel) {
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
            Elements chapterLinks = toc.select("#chapters a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            String bookID = toc.selectFirst("meta[property=og:image]").attr("content");
            bookID = bookID.substring(bookID.lastIndexOf("/")+1, bookID.lastIndexOf("."));
            Document chapterListPage = Jsoup.connect("https://www.wuxia.blog/temphtml/_tempChapterList_all_" + bookID + ".html")
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .post();
            chapterLinks = chapterListPage.select("a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
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
            chapterBody = doc.selectFirst("div.article");
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
            Element title = toc.selectFirst("title");
            Element author = toc.selectFirst("div:has(> h4:contains(Author:)) a");
            Element desc = toc.selectFirst("div[itemprop=description]");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.text().substring(0, title.text().indexOf(" | ")) : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text().replace("Description:", "") : "");
            metadata.setBufferedCover(cover != null ? cover.attr("content") : "");

            Elements tags = toc.select("div.row:has(> h4:contains(Genre)) > a");
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
        blacklistedTags.add("p:contains(This chapter is updated by Wuxia.Blog)");
        blacklistedTags.add("span[itemprop=datePublished]");
        blacklistedTags.add("span.fa-calendar");
        blacklistedTags.add("div:has(> ul.pager)");
        blacklistedTags.add("div.fb-like");
        blacklistedTags.add("button.btn");
        blacklistedTags.add("div.recently-nav");
        return blacklistedTags;
    }

}
