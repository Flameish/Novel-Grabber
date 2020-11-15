package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class foxaholic_com implements Source {
    private final Novel novel;
    private Document toc;

    public foxaholic_com(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Connection.Response res = Jsoup.connect("https://foxaholic.com/wp-admin/admin-ajax.php")
                    .method(Connection.Method.POST)
                    .referrer("novel.novelLink")
                    .data("action", "manga_get_chapters")
                    .data("manga",toc.select("#manga-chapters-holder").attr("data-id"))
                    .execute();

            Document page = res.parse();
            for(Element link: page.select(".listing-chapters_wrap a:not(a[title])")) {
                chapterList.add(new Chapter(link.text(), link.attr("abs:href")));
            }
            Collections.reverse(chapterList);
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

        metadata.setTitle(toc.select("title").first().text().replace(" – Foxaholic",""));
        metadata.setAuthor(toc.select(".author-content a").first().text());
        metadata.setDescription(toc.select(".summary__content").first().text());
        metadata.setBufferedCover(toc.select(".summary_image img").attr("abs:data-src"));

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
        blacklistedTags.add(".sharedaddy");
        blacklistedTags.add(".google-auto-placed");
        blacklistedTags.add("iframe");
        blacklistedTags.add("meta");
        blacklistedTags.add("center");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
