package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class boxnovel_com implements Source {
    private final Novel novel;
    private Document toc;

    public boxnovel_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Document firstChapterPage = Jsoup.connect(toc.selectFirst(".wp-manga-chapter a").attr("abs:href")).get();
            Elements chapterLinks = firstChapterPage.select("div.select-view:nth-child(2) > div:nth-child(1) > label:nth-child(1) > select:nth-child(1) option");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("data-redirect")));
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterBody = doc.select(".text-left").first();
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        metadata.setTitle(toc.select(".post-title h3").first().text());
        metadata.setAuthor(toc.select(".author-content").first().text());
        metadata.setDescription(toc.select(".description-summary").first().text());
        metadata.setBufferedCover(toc.select(".summary_image img").attr("abs:src"));

        Elements tags = toc.select(".genres-content a");
        List<String> subjects = new ArrayList<>();
        for(Element tag: tags) {
            subjects.add(tag.text());
        }
        metadata.setSubjects(subjects);

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add("div.code-block");
        blacklistedTags.add(".adbox");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
