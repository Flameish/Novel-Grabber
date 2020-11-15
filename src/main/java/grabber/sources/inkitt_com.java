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

public class inkitt_com implements Source {
    private final Novel novel;
    private Document toc;

    public inkitt_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select(".chapter-link");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
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
            chapterBody = doc.select(".story-page-text").first();
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

        metadata.setTitle(toc.select(".story-title").first().text());
        metadata.setAuthor(toc.select(".author-link span").first().text());
        metadata.setDescription(toc.select(".story-summary").first().text());
        String imageURL = toc.select(".story-horizontal-cover div[itemprop=image]").attr("style");
        imageURL = imageURL.replace("background-image: url('","");
        imageURL = imageURL.replace("')","");
        metadata.setBufferedCover(imageURL);

        Elements tags = toc.select(".genres");
        List<String> subjects = new ArrayList<>();
        for(Element tag: tags) {
            subjects.add(tag.text());
        }
        metadata.setSubjects(subjects);

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
