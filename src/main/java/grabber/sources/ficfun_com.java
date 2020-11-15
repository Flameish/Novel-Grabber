package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class ficfun_com implements Source {
    private final Novel novel;
    private Document toc;

    public ficfun_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            String firstChapterUrl = toc.select(".js-readBook-btn").attr("abs:href");
            Document firstChapterPage = Jsoup.connect(firstChapterUrl).get();
            for (Element chapterLink : firstChapterPage.select(".chapter-list a")) {
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
            String encodedChapter = doc.select("contentTpl").html();
            String decodedChapter = new String(Base64.getMimeDecoder().decode(encodedChapter), StandardCharsets.UTF_8);
            doc = Jsoup.parse(decodedChapter);
            chapterBody = doc;
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[GRABBER-ERROR]Could not connect to webpage. (" + e.getMessage() + ")");
            }
        }
        return chapterBody;
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        metadata.setTitle(toc.select(".details .name").first().text());
        metadata.setAuthor(toc.select(".details .author").first().text());
        metadata.setDescription(toc.select(".brief").first().text());
        metadata.setBufferedCover(toc.select(".js-cover.img").attr("abs:data-cover"));

        Elements tags = toc.select(".novel-tags span");
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
