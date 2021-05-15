package grabber.sources;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class foxaholic_com implements Source {
    private final String name = "Foxaholic";
    private final String url = "https://foxaholic.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public foxaholic_com(Novel novel) {
        this.novel = novel;
    }

    public foxaholic_com() {
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
            Connection.Response res = Jsoup.connect("https://foxaholic.com/wp-admin/admin-ajax.php")
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .referrer("novel.novelLink")
                    .data("action", "manga_get_chapters")
                    .data("manga", toc.select("#manga-chapters-holder").attr("data-id"))
                    .execute();

            Document page = res.parse();
            for (Element link : page.select(".listing-chapters_wrap a:not(a[title])")) {
                chapterList.add(new Chapter(link.text(), link.attr("abs:href")));
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
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select(".text-left").first();
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
            Element title = toc.selectFirst(".breadcrumb > :last-child");
            Element author = toc.selectFirst(".author-content a");
            Element desc = toc.selectFirst(".summary__content");
            Element cover = toc.selectFirst(".summary_image img");

            metadata.setTitle(title != null ? title.text() : "");
            metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:data-src") : "");

            Elements tags = toc.select(".genres-content a");
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
        blacklistedTags.add(".sharedaddy");
        blacklistedTags.add(".google-auto-placed");
        blacklistedTags.add("iframe");
        blacklistedTags.add("meta");
        blacklistedTags.add("center");
        return blacklistedTags;
    }

}
