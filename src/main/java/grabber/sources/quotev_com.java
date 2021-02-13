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

public class quotev_com implements Source {
    private final String name = "Quotev";
    private final String url = "https://quotev.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public quotev_com(Novel novel) {
        this.novel = novel;
    }

    public quotev_com() {
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
            Elements chapterLinks = toc.select("#rselectList a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (HttpStatusException httpEr) {
            GrabberUtils.err(novel.window, GrabberUtils.getHTMLErrMsg(httpEr));
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage!", e);
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select("#rescontent").first();
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
            metadata.setTitle(toc.select("#quizHeaderTitle h1").first().text());
            metadata.setAuthor(toc.select("a[rel=author]").first().text());
            metadata.setDescription(toc.select("#qdesct").first().text());
            metadata.setBufferedCover(toc.select(".logo").attr("abs:src"));

            Elements tags = toc.select(".genres");
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
