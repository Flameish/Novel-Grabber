package grabber.sources;

import grabber.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class novelfull_com extends Source implements Initializable {
    private final Novel novel;
    private Document toc;
    private String bookTitle;
    private String bookAuthor;
    private String bookCover;
    private String bookDesc;
    private List<Chapter> chapters = new ArrayList<>();
    private String nextChapterBtn = "";
    private boolean reGrab = false;

    public novelfull_com(Novel novel) {
        this.novel = novel;
    }

    @Override
    public void init() {
    }

    public List<Chapter> getChapterList() {
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select(".list-chapter li a");
            for (Element chapterLink : chapterLinks) {
                String chapterName = chapterLink.text();
                String chapterUrl = chapterLink.attr("abs:href");
                chapters.add(new Chapter(chapterName, chapterUrl));
            }
        } catch (IOException | NullPointerException e) {
            GrabberUtils.err(novel.window, "Could not get chapter list from: " + novel.novelLink, e);
        }
        return chapters;
    }

    public String getChapterContent(Chapter chapter) {
        Element chapterContent = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).get();
            chapterContent = doc.select("#chapter-content").first();
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not get chapter content from: " + chapter.chapterURL, e);
        }
        return chapterContent != null ? chapterContent.toString() : "";
    }

    public void getNovelMetadata() {
        if (toc == null) {
            GrabberUtils.err("Table of contents not loaded. Cannot get novel metadata.");
            return;
        }
        Element titleElement = toc.select(".book h3").first();
        bookTitle = titleElement != null ? titleElement.text() : "";
        Element authorElement = toc.select(".info a[href*=author]").first();
        bookAuthor = authorElement != null ? authorElement.text() : "";
        Element descElement = toc.select(".desc-text").first();
        bookDesc = descElement != null ? descElement.text() : "";
        Element coverElement = toc.select(".book img").first();
        bookCover = coverElement != null ? coverElement.attr("abs:src") : "";
        novel.setMetadata(bookTitle, bookAuthor, bookCover, null, bookDesc);
    }

    @Override
    public List<String> getBlacklistedTags() {
        List<String> blacklistedTags = new ArrayList<>();
        blacklistedTags.add("script");
        blacklistedTags.add("style");
        return blacklistedTags;
    }

    @Override
    public Map<String, String> getCookies() {
        return null;
    }
}
