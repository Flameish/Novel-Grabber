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

public class lightnovelworld_com implements Source {
    private final String name = "Light Novel World";
    private final String url = "https://www.lightnovelworld.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public lightnovelworld_com() {
    }

    public lightnovelworld_com(Novel novel) {
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
            Elements chapterLinks;
            while (true) {
                GrabberUtils.sleep(750);
                chapterLinks = toc.select(".chapter-list a");
                for (Element chapterLink : chapterLinks) {
                    System.out.println(chapterLink.attr("title") + chapterLink.attr("abs:href"));
                    chapterList.add(new Chapter(chapterLink.attr("title"), chapterLink.attr("abs:href")));
                }
                if (toc.selectFirst(".PagedList-skipToNext") == null) break;
                System.out.println(toc.select(".PagedList-skipToNext a").attr("abs:href"));
                toc = Jsoup.connect(toc.select(".PagedList-skipToNext a").attr("abs:href"))
                        .cookies(novel.cookies)
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
            GrabberUtils.sleep(750);
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
                    .get();
            chapterBody = doc.selectFirst(".chapter-content");
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
            Element title = toc.selectFirst(".novel-title");
            Element author = toc.selectFirst("span[itemprop=author]");
            Element desc = toc.selectFirst(".summmary .content");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(toc.selectFirst(".cover img").attr("abs:data-src"));

            Elements tags = toc.select(".tags .content a");
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
        blacklistedTags.add(".trinity-player-iframe-wrapper");
        blacklistedTags.add("p[class]");
        blacklistedTags.add(".adsbox");
        return blacklistedTags;
    }

}
