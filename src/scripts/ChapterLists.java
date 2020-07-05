package scripts;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.xhrRequest;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import system.init;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterLists {
    public static void getList(Novel novel) {
        System.out.println("[INFO]Fetching chapterlist...");
        if(init.window != null) {
            init.window.appendText(novel.options.window, "[INFO]Fetching novel info...");
        }

        try {
            switch(novel.host.url) {
                case "https://boxnovel.com/":
                    novel.chapters = ChapterLists.boxnovel(novel);
                    break;
                case "http://novelfull.com/":
                    novel.chapters = ChapterLists.novelfull(novel);
                    break;
                case "https://creativenovels.com/":
                    novel.chapters = ChapterLists.creativenovels(novel);
                    break;
                case "https://zenithnovels.com/":
                    novel.chapters = ChapterLists.zenithnovels(novel);
                    break;
                case "https://translatinotaku.net/":
                    novel.chapters = ChapterLists.translatinotaku(novel);
                    break;
                case "https://comrademao.com/":
                    novel.chapters = ChapterLists.comrademao(novel);
                    break;
                case "https://wuxiaworld.online/":
                    novel.chapters = ChapterLists.wuxiaworld(novel);
                    break;
                case "https://fanfiction.net/":
                    novel.chapters = ChapterLists.fanfiction(novel);
                    break;
                case "https://fanfiktion.de/":
                    novel.chapters = ChapterLists.fanfiktion(novel);
                    break;
                case "https://tapread.com/":
                    novel.chapters = ChapterLists.tapread(novel);
                    break;
                case "https://webnovel.com/":
                    novel.chapters = ChapterLists.webnovel(novel);
                    break;
                case "https://booklat.com.ph/":
                    novel.chapters = ChapterLists.booklat(novel);
                    break;
                case "https://foxaholic.com/":
                    novel.chapters = ChapterLists.foxaholic(novel);
                    break;
                default:
                    novel.chapters = ChapterLists.defaults(novel);
                    break;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Chapter> creativenovels(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Connection.Response res = Jsoup.connect("https://creativenovels.com/wp-admin/admin-ajax.php")
                .method(Connection.Method.POST)
                .timeout(30 * 1000)
                .data("action", "crn_chapter_list")
                .data("view_id", novel.tableOfContent.select("#chapter_list_novel_page").attr("class"))
                .execute();
        Document doc = res.parse();
        String ajaxResp = doc.select("body").toString();
        ajaxResp = ajaxResp.replaceAll("success.define.","");
        ajaxResp = ajaxResp.replaceAll(".data.available.end_data.","");
        String[] test = ajaxResp.split(".data.");
        List<String> names = new ArrayList<>();
        List<String> links = new ArrayList<>();
        for (String line: test) {
            if(line.contains("locked.end")) break;
            if(line.contains("http")) {
                links.add(line.substring(line.indexOf("http")));
            } else {
                names.add(line);
            }
        }
        names.remove(names.size()-1);

        List<Chapter> chapters = new ArrayList<>();
        for(int i = 0; i < links.size(); i++) {
            chapters.add(new Chapter(names.get(i),links.get(i)));
        }
        return chapters;
    }

    private static List<Chapter> foxaholic(Novel novel) throws IOException {
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Connection.Response res = Jsoup.connect("https://foxaholic.com/wp-admin/admin-ajax.php")
                .method(Connection.Method.POST)
                .referrer("novel.novelLink")
                .timeout(30 * 1000)
                .data("action", "manga_get_chapters")
                .data("manga", novel.tableOfContent.select(".rating-post-id").attr("value"))
                .execute();
        List<Chapter> chapters = new ArrayList<>();
        for(Element link: res.parse().select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(link.text(), link.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> boxnovel(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for(Element chapterLink: chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        String boxNovelFirstChapter = chapters.get(chapters.size()-1).chapterURL;
        String boxNovelbaseLinkStart = boxNovelFirstChapter.substring(0, GrabberUtils.ordinalIndexOf(boxNovelFirstChapter, "/", 5) + 9);
        String boxNovelChapterNumberString = boxNovelFirstChapter.substring(boxNovelbaseLinkStart.length());
        int boxNovelChapterNumber;
        if(boxNovelChapterNumberString.contains("-")) {
            boxNovelChapterNumber = Integer.valueOf(boxNovelChapterNumberString.substring(0,boxNovelChapterNumberString.indexOf("-")));
        } else {
            boxNovelChapterNumber = Integer.valueOf(boxNovelChapterNumberString);
        }
        if (boxNovelChapterNumber != 1) {
            for (int i = boxNovelChapterNumber - 1; i >= 1; i--) {
                chapters.add(new Chapter("Chapter " + i, boxNovelbaseLinkStart + i));
            }
        }
        return chapters;
    }

    private static List<Chapter> novelfull(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select("li.next").hasClass("disabled")) {
            chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("li.next a").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> zenithnovels(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> translatinotaku(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select("a.page-numbers.next").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("a.page-numbers.next").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));

        }
        return chapters;
    }

    private static List<Chapter> comrademao(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks;
        while (!novel.tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
            chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
            for (Element chapterLink : chapterLinks) {
                chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
            novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".pagination a.next").attr("abs:href")).timeout(30 * 1000).get();
        }
        chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }

    private static List<Chapter> wuxiaworld(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        for (Element chapterLink : chapterLinks) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        String wuxiaonlineFirstChapter = chapters.get(chapterLinks.size() - 1).chapterURL;
        String wuxiaonlinebaseLinkStart = wuxiaonlineFirstChapter.substring(0, GrabberUtils.ordinalIndexOf(wuxiaonlineFirstChapter, "/", 4) + 9);
        String wuxiaonlineChapterNumberString = wuxiaonlineFirstChapter.substring(wuxiaonlinebaseLinkStart.length());
        int wuxiaonlineChapterNumber;
        if(wuxiaonlineChapterNumberString.contains("-"))
            wuxiaonlineChapterNumber = Integer.valueOf(wuxiaonlineChapterNumberString.substring(0,wuxiaonlineChapterNumberString.indexOf("-")));
        else
            wuxiaonlineChapterNumber = Integer.valueOf(wuxiaonlineChapterNumberString);
        if(wuxiaonlineChapterNumber != 1) {
            for(int i = wuxiaonlineChapterNumber-1; i >= 1; i--) {
                chapters.add(new Chapter("Chapter "+i, wuxiaonlinebaseLinkStart+i));
            }
        }
        return chapters;
    }

    private static List<Chapter> fanfiction(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        String fullLink = novel.tableOfContent.select("link[rel=canonical]").attr("abs:href");
        String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
        String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
        chapterLinks = chapterLinks.select("option[value]");
        for(int i = 0;  i < chapterLinks.size() / 2; i++)
            chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
        return chapters;
    }

    private static List<Chapter> fanfiktion(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        Elements chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelector);
        String fullLink = novel.tableOfContent.select("link[rel=canonical]").attr("abs:href");
        String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
        String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
        chapterLinks = chapterLinks.select("option[value]");
        for(int i = 0;  i < chapterLinks.size(); i++)
            chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
        return chapters;
    }

    private static List<Chapter> tapread(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        String novelURL = novel.novelLink;
        int tapReadNovelId = Integer.parseInt(novelURL.substring(novelURL.lastIndexOf("/") + 1));
        Map<String, String> chapterMap = xhrRequest.tapReadGetChapterList(tapReadNovelId);
        int i = 0;
        for (String chapterId : chapterMap.keySet()) {
            chapters.add(new Chapter(chapterMap.get(chapterId), "https://tapread.com/book/index/" + tapReadNovelId + "/" + chapterId));
            chapters.get(i).xhrBookId = tapReadNovelId;
            chapters.get(i).xhrChapterId = chapterId;
            i++;
        }
        return chapters;
    }

    private static List<Chapter> webnovel(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();
        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        String csrfToken = "null";
        String bookId = novel.novelLink;
        String bookTitle = novel.tableOfContent.select(novel.host.bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
        bookId = bookId.substring(GrabberUtils.ordinalIndexOf(bookId, "/", 4) + 1, GrabberUtils.ordinalIndexOf(bookId, "/", 5));

        String otherParameter = "";
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        URL url = new URL(novel.novelLink);
        URLConnection connection = url.openConnection();
        connection.getContent();

        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        for (HttpCookie cookie : cookies) {
            if (cookie.toString().startsWith("_csrfToken")) {
                csrfToken = cookie.toString().substring(11);
            }
        }
        Map<String, String> webnovelChapters = xhrRequest.webnovelGetChapterList(
                "https://www.webnovel.com/apiajax/chapter/GetChapterList?_csrfToken=" + csrfToken + "&bookId=" + bookId + "&_=" + otherParameter);
        int webnovelChapterNumber = 1;
        for (String chapterId : webnovelChapters.keySet()) {
            chapters.add(new Chapter("Chapter " + webnovelChapterNumber + ": " + webnovelChapters.get(chapterId), "https://www.webnovel.com/book/" + bookId + "/" + chapterId + "/"
                    + bookTitle.replace(" ", "-") + "/" + webnovelChapters.get(chapterId).replace(" ", "-")));
            webnovelChapterNumber++;
        }
        return chapters;
    }

    private static List<Chapter> booklat(Novel novel) throws IOException {
        novel.tempPage = Jsoup.connect(novel.novelLink+"/chapters").cookies(novel.cookies).timeout(30 * 1000).get();
        novel.tableOfContent = Jsoup.connect(novel.tempPage.select("#lnkRead").attr("abs:href")).cookies(novel.cookies).timeout(30 * 1000).get();
        Elements chaptersLinks = novel.tableOfContent.select("#ddChapter option[value]");
        List<Chapter> chapters = new ArrayList<>();
        for(Element chapterLink: chaptersLinks) {
            chapters.add(new Chapter(chapterLink.text(), novel.novelLink.replace("/Info/", "/Read/") + "/" + chapterLink.attr("value")));
        }
        novel.tableOfContent = novel.tempPage;
        return chapters;
    }

    private static List<Chapter> defaults(Novel novel) throws IOException {
        List<Chapter> chapters = new ArrayList<>();

        novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
        for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelector)) {
            chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
        }
        return chapters;
    }
}
