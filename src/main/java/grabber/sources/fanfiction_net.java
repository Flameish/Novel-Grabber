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

public class fanfiction_net implements Source {
    private final Novel novel;
    private Document toc;

    public fanfiction_net(Novel novel) {
        this.novel = novel;
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select("#chap_select option");
            String fullLink = toc.select("link[rel=canonical]").attr("abs:href");
            String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
            String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
            chapterLinks = chapterLinks.select("option[value]");
            for(int i = 0;  i < chapterLinks.size() / 2; i++) {
                chapterList.add(new Chapter(chapterLinks.get(i).text(), baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
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
            chapterBody = doc.select("#storytext").first();
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

        metadata.setTitle(toc.select("#profile_top b.xcontrast_txt").first().text());
        metadata.setAuthor(toc.select("#profile_top a.xcontrast_txt").first().text());
        metadata.setDescription(toc.select("div.xcontrast_txt").first().text());
        metadata.setBufferedCover(toc.select("#profile_top img.cimage").attr("abs:src"));

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
