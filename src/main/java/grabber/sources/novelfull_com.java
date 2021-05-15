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

public class novelfull_com implements Source {
    private final String name = "Novel Full";
    private final String url = "https://novelfull.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public novelfull_com(Novel novel) {
        this.novel = novel;
    }

    public novelfull_com() {
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
            toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).get();
            Elements chapterLinks;
            while (!toc.select("li.next").hasClass("disabled")) {
                chapterLinks = toc.select(".list-chapter a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                toc = Jsoup.connect(toc.select("li.next a").attr("abs:href")).cookies(novel.cookies).get();
            }
            chapterLinks = toc.select(".list-chapter a");
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
            Document doc = Jsoup.connect(chapter.chapterURL).cookies(novel.cookies).get();
            chapterBody = doc.select("#chapter-content").first();
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
            metadata.setTitle(toc.select("h3.title").first().text());
            metadata.setAuthor(toc.select(".info > div:nth-child(1) > a:nth-child(2)").first().text());
            metadata.setDescription(toc.select("div.desc-text").first().text());
            metadata.setBufferedCover(toc.select(".book > img:nth-child(1)").attr("abs:src"));

            Elements tags = toc.select(".info > div:nth-child(2) a");
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
        blacklistedTags.add("ads");
        blacklistedTags.add("div[align=left]");
        blacklistedTags.add(".adsbygoogle");
        blacklistedTags.add(".cha-tit p");
        return blacklistedTags;
    }

}
