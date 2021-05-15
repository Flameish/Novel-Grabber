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

public class webnovel_com implements Source {
    private final String name = "Webnovel";
    private final String url = "https://webnovel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public webnovel_com() {
    }

    public webnovel_com(Novel novel) {
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
            // Fetch novel page for metadata
            toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).get();
            // Go to table of contents page for chapter list
            Document chapterListPage = Jsoup.connect(toc.selectFirst("a.j_show_contents").attr("abs:href")).cookies(novel.cookies).get();
            // Select all chapters which are not VIP locked (they have a svg icon)
            Elements chapterLinks = chapterListPage.select(".volume-item a:not(:has(svg))");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.attr("title"), chapterLink.attr("abs:href")));
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
            chapterBody = doc.select("div.cha-words").first();
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
            Element title = toc.selectFirst("p:has(a[title=home]) > span:last-child");
            Element author = toc.selectFirst("p:has(*:contains(Author)) > *:not(:contains(Author))");
            Element desc = toc.selectFirst("#about p");
            Element cover = toc.selectFirst(".g_thumb img:eq(1)");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");

            Elements tags = toc.select(".m-tags a");
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
        blacklistedTags.add("pirate");
        blacklistedTags.add(".cha-hr");
        blacklistedTags.add(".cha-info");
        blacklistedTags.add(".cha-tit p");
        blacklistedTags.add(".j_bottom_comment_area");
        blacklistedTags.add(".user-links-wrap");
        return blacklistedTags;
    }

}
