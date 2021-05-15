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

public class asianovel_net implements Source {
    private final String name = "asianovel";
    private final String url = "https://www.asianovel.net/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public asianovel_net() {
    }

    public asianovel_net(Novel novel) {
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
            Elements paginationLinks = toc.select(".pagination li a");
            if(paginationLinks != null &&!paginationLinks.isEmpty()) {
                String canonicalUrl = toc.selectFirst("link[rel=canonical]").attr("abs:href");
                Document temp = Jsoup.connect(canonicalUrl)
                        .cookies(novel.cookies)
                        .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                        .get();
                Elements chapterLinks = temp.select(".summary-compact a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                int lastPage = Integer.parseInt(paginationLinks.get(paginationLinks.size()-1).text());
                for(int i = 2; i <= lastPage; i++) {
                    temp = Jsoup.connect(canonicalUrl + "page/" + i)
                            .cookies(novel.cookies)
                            .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                            .get();
                    chapterLinks = temp.select(".summary-compact a");
                    for (Element chapterLink : chapterLinks) {
                        chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                }
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
            chapterBody = doc.selectFirst("article");
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
            Element author = toc.selectFirst("meta[name=author]");
            Element desc = toc.selectFirst(".summary-classic__text:not(a)");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.attr("content") : "");
            metadata.setAuthor(author != null ? author.attr("content") : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("content") : "");

            Elements tags = toc.select(".taxo .taxo__text");
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
