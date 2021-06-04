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

public class wnmtl_org implements Source{
    private final String name = "WNMTL";
    private final String url = "https://wnmtl.org";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public wnmtl_org(Novel novel) {
        this.novel = novel;
    }
    public wnmtl_org() {}

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
        List<Chapter> cList = new ArrayList<>();
        try {
            toc = Jsoup.connect(novel.novelLink)
                    .cookies(novel.cookies)
                    .get();
            Connection.Response res = Jsoup.connect("https://www.wnmtl.org/wp-admin/admin-ajax.php")
                    .data("action","manga_get_chapters")
                    .data("manga",toc.select("div.post-rating > input").attr("value"))
                    .cookies(novel.cookies)
                    .method(Connection.Method.POST)
                    .execute();
            Elements chaps = res.parse().select("div.listing-chapters_wrap show-more > ul > li > a");
            for (Element e : chaps) {
                cList.add(new Chapter(e.text(), e.attr("abs:href")));
            }
            Collections.reverse(chaps);
        }catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        } catch (NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not find expected selectors. Correct novel link?", e);
        }
        return cList;
    }

    @Override
    public Element getChapterContent(Chapter chapter) {
        Element body = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL)
                    .cookies(novel.cookies)
                    .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:83.0) Gecko/20100101 Firefox/83.0")
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
        NovelMetadata mdata = new NovelMetadata();
        if (toc != null) {
                Element title = toc.selectFirst("div.post-title > h1");
                Element author = toc.selectFirst("div.author-content > a");
                Element description = toc.selectFirst("div.description-summary > div > p");
                Element cimg = toc.selectFirst("img.img-responsive");
                mdata.setTitle(title != null ? title.text() : "");
                mdata.setAuthor(author != null ? author.text() : "");
                mdata.setDescription(description != null ? description.text() : "");
                mdata.setBufferedCover(cimg != null ? cimg.attr("abs:src") : "");
                Elements tags = toc.select("div.genres-content > a");
                List<String> subjects = new ArrayList<>();
                for (Element tag : tags) {
                    subjects.add(tag.text());
                }
                mdata.setSubjects(subjects);

        }
        return mdata;
    }

    @Override
    public List<String> getBlacklistedTags() {
        List<String> blacklistedTags = new ArrayList<>();
        blacklistedTags.add("div.nav-links");
        return blacklistedTags;
    }
}
