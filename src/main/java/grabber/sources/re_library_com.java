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

public class re_library_com implements Source {
    private final String name = "Re:Library";
    private final String url = "https://re-library.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public re_library_com(Novel novel) {
        this.novel = novel;
    }

    public re_library_com() {
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
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select(".su-accordion a");
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
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select(".entry-content").first();
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
            metadata.setTitle(toc.select(".entry-title").first().text());
            metadata.setBufferedCover(toc.select("img.rounded").attr("abs:src"));
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add(".sharedaddy");
        blacklistedTags.add(".code-block");
        blacklistedTags.add(".ezoic-adpicker-ad");
        blacklistedTags.add(".ezoic-ad");
        blacklistedTags.add(".su-button");
        blacklistedTags.add("table:has(a[href])");
        blacklistedTags.add("div[style=margin:0 auto;width:100px]");
        blacklistedTags.add(".prevPageLink");
        blacklistedTags.add(".nextPageLink");
        blacklistedTags.add("a:contains(Next)");
        blacklistedTags.add("a:contains(Previous)");
        blacklistedTags.add("table:has(span[style=font-size:8pt;color:#999999])");
        blacklistedTags.add("h2:contains(References)");
        blacklistedTags.add("table#fixed");
        blacklistedTags.add("a:contains(Index)");
        return blacklistedTags;
    }

}
