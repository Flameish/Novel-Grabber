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

public class lightnovelpub_com implements Source {
    private final String name = "Light Novel Pub";
    private final String url = "https://www.lightnovelpub.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public lightnovelpub_com() {
    }

    public lightnovelpub_com(Novel novel) {
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
            toc = Jsoup.connect(novel.novelLink + "?tab=chapters")
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            // Add chapters from first page
            Elements chapterLinks = toc.select(".chapter-list a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.attr("title"), chapterLink.attr("abs:href")));
            }
            // Go through pagination links and all chapters
            Element nextPageBtn = toc.selectFirst("ul.pagination li.PagedList-skipToNext a");
            while (nextPageBtn != null) {
                toc = Jsoup.connect(nextPageBtn.attr("abs:href"))
                        .cookies(novel.cookies)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                chapterLinks = toc.select(".chapter-list a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.attr("title"), chapterLink.attr("abs:href")));
                }
                // Select next page
                nextPageBtn = toc.selectFirst("ul.pagination li.PagedList-skipToNext a");
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
            chapterBody = doc.selectFirst("#chapter-container");
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
            Element title = toc.selectFirst("h1.novel-title");
            Element author = toc.selectFirst("span[itemprop=author]");
            Element desc = toc.selectFirst(".content");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:content") : "");

            Elements tags = toc.select(".categories a");
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
        blacklistedTags.add(".adsbox");
        blacklistedTags.add(".trinity-player-iframe-wrapper");
        blacklistedTags.add("p[class]");
        return blacklistedTags;
    }

}
