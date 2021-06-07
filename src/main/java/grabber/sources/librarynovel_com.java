package grabber.sources;

import com.sun.javadoc.Doc;
import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class librarynovel_com implements Source{
    private final String name = "LibraryNovel";
    private final String url = "https://librarynovel.com/";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public boolean canHeadless() {
        return canHeadless;
    }

    @Override
    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList<>();
        try {
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .get();
            Connection.Response res = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .data("action","manga_get_chapters")
                    .data("manga", toc.select("div.post-rating > div.user-rating > input").attr("value"))
                    .execute();
            Document doc = res.parse();
            for (Element chaps : doc.select("div > div > ul > li > a")) {
                chapterList.add(new Chapter(chaps.text(), chaps.attr("abs:href")));
            }
        }catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return chapterList;
    }

    @Override
    public Element getChapterContent(Chapter chapter) {
        Element body = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .get();
            body = doc.selectFirst("div.text-left");
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return body;
    }

    @Override
    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();
        if (toc != null) {

                Element title = toc.selectFirst("div.post-title > h1");
                Element author = toc.selectFirst("div.author-content > a");
                Element cover  = toc.selectFirst("div.summary-img > a > img");
                Element description = toc.selectFirst("div.description-summary > div");
                metadata.setTitle(title != null ? title.text() : "");
                metadata.setAuthor(author != null ? author.text() : "");
                metadata.setDescription(description != null ? description.text() : "");
                metadata.setBufferedCover(cover != null ? cover.attr("abs:data-src") : "");
        }
        return metadata;
    }

    @Override
    public List<String> getBlacklistedTags() {
        List<String> tags = new ArrayList<>();
        tags.add("div.wp-manga-nav");
        return tags;
    }
}
