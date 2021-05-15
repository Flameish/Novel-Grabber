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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class dreame_com implements Source {
    private final String name = "Dreame";
    private final String url = "https://dreame.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public dreame_com(Novel novel) {
        this.novel = novel;
    }

    public dreame_com() {
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
        List<Chapter> chapterList = new ArrayList<>();
        try {
            toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).get();
            String firstChapterUrl = toc.select(".js-readBook-btn").attr("abs:href");
            Document firstChapterPage = Jsoup.connect(firstChapterUrl).cookies(novel.cookies).get();
            for (Element chapterLink : firstChapterPage.select(".chapter-list a")) {
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
            String encodedChapter = doc.select("#contentTpl").html();
            String decodedChapter = new String(Base64.getMimeDecoder().decode(encodedChapter), StandardCharsets.UTF_8);
            doc = Jsoup.parse(decodedChapter);
            chapterBody = doc;
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not connect to webpage. (" + e.getMessage() + ")", e);
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if (toc != null) {
            metadata.setTitle(toc.select(".details .name").first().text());
            metadata.setAuthor(toc.select(".details .author").first().text());
            metadata.setDescription(toc.select(".brief").first().text());
            String coverURL = toc.select(".js-cover.img").attr("abs:data-cover");
            if (coverURL.contains("@")) coverURL = coverURL.substring(0, coverURL.indexOf("@"));
            metadata.setBufferedCover(coverURL);

            Elements tags = toc.select(".novel-tags span");
            List<String> subjects = new ArrayList<>();
            for (Element tag : tags) {
                subjects.add(tag.text());
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }

    /**
     * Initialize blacklisted tags.
     */
    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

}
