package grabber.sources;

import grabber.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.*;

public class comrademao_com implements Source {
    private final String name = "Comrade Mao";
    private final String url = "https://comrademao.com";
    private final boolean canHeadless = false;
    private Novel novel;
    private Document toc;

    public comrademao_com() {
    }

    public comrademao_com(Novel novel) {
        this.novel = novel;
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
            toc = Jsoup.connect(novel.novelLink).cookies(novel.cookies).get();
            Elements chapterLinks = toc.select(".table > tbody a");
            for (Element chapterLink : chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            Element nextBtn = toc.selectFirst(".pagination-next");
            while(nextBtn != null) {
                GrabberUtils.sleep(500);
                Document nextPage = Jsoup.connect(nextBtn.attr("href")).cookies(novel.cookies).get();
                chapterLinks = nextPage.select(".table > tbody a");
                for (Element chapterLink : chapterLinks) {
                    chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                nextBtn = nextPage.selectFirst(".pagination-next");
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
            chapterBody = doc.selectFirst("#content");
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
            Element title = toc.selectFirst(".is-one-third > p:nth-child(1)");
            //Element author = toc.selectFirst("");
            Element desc = toc.selectFirst("div.columns:nth-child(1) > div:nth-child(2)");
            Element cover = toc.selectFirst("#NovelInfo .is-one-third img");

            metadata.setTitle(title != null ? title.text() : "");
            //metadata.setAuthor(author != null ? author.text() : "");
            metadata.setDescription(desc != null ? desc.text() : "");
            metadata.setBufferedCover(cover != null ? cover.attr("abs:src") : "");

            Elements tags = toc.select("#NovelInfo > p:nth-child(2) a");
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
