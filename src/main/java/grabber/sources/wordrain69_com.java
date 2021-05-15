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

public class wordrain69_com implements Source {
    private final String name = "Wordrain";
    private final String url = "https://wordrain69.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public wordrain69_com(Novel novel) {
        this.novel = novel;
    }

    public wordrain69_com() {
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
            toc = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
            Connection.Response res = Jsoup.connect("https://wordrain69.com/wp-admin/admin-ajax.php")
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .referrer("novel.novelLink")
                    .data("action", "manga_get_chapters")
                    .data("manga", toc.select("#manga-chapters-holder").attr("data-id"))
                    .execute();

            Document doc = res.parse();
            for (Element link : doc.select(".listing-chapters_wrap a:not(a[title])")) {
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
            metadata.setTitle(toc.select(".post-title h1").first().text());
            metadata.setAuthor(toc.select(".author-content").first().text());
            metadata.setDescription(toc.select(".summary__content").first().text());
            metadata.setBufferedCover(toc.select(".summary_image img").attr("abs:data-src"));

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
        blacklistedTags.add("center");
        blacklistedTags.add("meta");
        blacklistedTags.add("script");
        blacklistedTags.add("iframe");
        blacklistedTags.add(".google-auto-placed");
        blacklistedTags.add(".sharedaddy");
        return blacklistedTags;
    }

}
