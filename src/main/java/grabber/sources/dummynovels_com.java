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

public class dummynovels_com implements Source {
    private final String name = "Dummy Novels";
    private final String url = "https://dummynovels.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public dummynovels_com(Novel novel) {
        this.novel = novel;
    }

    public dummynovels_com() {
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
            Elements chapterLinks = toc.select(".chapter-arc-accordion a:not(.elementor-accordion-title)");
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
            chapterBody = doc.selectFirst(".elementor-widget-theme-post-content");
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
            Element author = toc.selectFirst(".elementor-text-editor:contains(Author:)");
            Element desc = toc.selectFirst(".novel-synopsis-content");
            Element cover = toc.selectFirst("meta[property=og:image]");

            metadata.setTitle(title != null ? title.attr("content") : "");
            metadata.setAuthor(author != null ? author.text().replace("Author: ", "") : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:content") : "");

            Elements tags = toc.select(".novel-term a");
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
