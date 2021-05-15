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

public class fanfiktion_de implements Source {
    private final String name = "FanFiktion";
    private final String url = "https://fanfiktion.de";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public fanfiktion_de(Novel novel) {
        this.novel = novel;
    }

    public fanfiktion_de() {
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
            Elements chapterLinks = toc.select("#kA option");
            String fullLink = toc.select("link[rel=canonical]").attr("abs:href");
            String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
            String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
            chapterLinks = chapterLinks.select("option[value]");
            for (int i = 0; i < chapterLinks.size(); i++)
                chapterList.add(new Chapter(chapterLinks.get(i).text(), baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
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
            chapterBody = doc.select(".user-formatted-inner").first();
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
            metadata.setTitle(toc.select(".huge-font").first().text());
            metadata.setAuthor(toc.select("a.no-wrap").first().text());
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
