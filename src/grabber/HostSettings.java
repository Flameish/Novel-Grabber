package grabber;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class HostSettings {
    private static final String[] headerlessBrowserWebsites = {"CreativeNovels", "FicFun", "Dreame", "WuxiaWorld.site","Foxaholic","FoxTeller"};
    private static final String[] noHeaderlessBrowserWebsites = {"WattPad", "FanFiction", "FanFiktion"};
    private static final String[] loginWebsites = {"Booklat","Wuxiaworld"};
    public static List<String> headerlessBrowserWebsitesList = Arrays.asList(headerlessBrowserWebsites);
    public static List<String> noHeaderlessBrowserWebsitesList = Arrays.asList(noHeaderlessBrowserWebsites);
    public static List<String> loginWebsitesList = Arrays.asList(loginWebsites);
    public String url;
    public String chapterLinkSelector;
    public String chapterContainer;
    public List<String> blacklistedTags = new ArrayList<>();
    public String bookTitleSelector;
    public String bookDescSelector;
    public String bookAuthorSelector;
    public String bookSubjectSelector;
    public String bookCoverSelector;

    public HostSettings(String domain) {
        JSONObject currentSite = (JSONObject) Config.siteSelectorsJSON.get(domain);
        url = String.valueOf(currentSite.get("url"));
        chapterLinkSelector = String.valueOf(currentSite.get("chapterLinkSelector"));
        chapterContainer = String.valueOf(currentSite.get("chapterContainer"));
        for(Object tagObject: (JSONArray) currentSite.get("blacklistedTags")) {
            blacklistedTags.add(tagObject.toString());
        }
        bookTitleSelector = String.valueOf(currentSite.get("bookTitleSelector"));
        bookDescSelector = String.valueOf(currentSite.get("bookDescSelector"));
        bookCoverSelector = String.valueOf(currentSite.get("bookCoverSelector"));
        bookAuthorSelector = String.valueOf(currentSite.get("bookAuthorSelector"));
        bookSubjectSelector = String.valueOf(currentSite.get("bookSubjectSelector"));
    }


    public List<Chapter> getChapterList(Novel novel) {
        List<Chapter> chapters = new ArrayList<>();
        try {
        switch (url) {
            case "https://boxnovel.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                Elements chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                for(Element chapterLink: chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                // Get link of last chapter (first in novel context)
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
                break;
            case "http://novelfull.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select("li.next").hasClass("disabled")) {
                    chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("li.next a").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
            case "https://zenithnovels.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
            case "https://translatinotaku.net/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select("a.page-numbers.next").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("a.page-numbers.next").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));

                }
                break;
            case "https://comrademao.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".pagination a.next").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
            case "https://wuxiaworld.online/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                // Get href link of last chapter (first in novel context)
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
                break;
            case "https://fanfiction.net/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                String fullLink = novel.tableOfContent.select("link[rel=canonical]").attr("abs:href");
                String baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
                String baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
                chapterLinks = chapterLinks.select("option[value]");
                for(int i = 0;  i < chapterLinks.size() / 2; i++)
                    chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
                break;
            // Is a reskin of fanction.net
            case "https://fanfiktion.de/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                chapterLinks = novel.tableOfContent.select(chapterLinkSelector);
                fullLink = novel.tableOfContent.select("link[rel=canonical]").attr("abs:href");
                baseLinkStart = fullLink.substring(0, GrabberUtils.ordinalIndexOf(fullLink, "/", 5) + 1);
                baseLinkEnd = fullLink.substring(baseLinkStart.length() + 1);
                chapterLinks = chapterLinks.select("option[value]");
                for(int i = 0;  i < chapterLinks.size(); i++)
                    chapters.add(new Chapter(chapterLinks.get(i).text(),baseLinkStart + chapterLinks.get(i).attr("value") + baseLinkEnd));
                break;
            case "https://tapread.com/":
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
                break;
            case "https://webnovel.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                String csrfToken = "null";
                String bookId = novel.novelLink;
                String bookTitle = novel.tableOfContent.select(bookTitleSelector).first().text().replaceAll("[\\\\/:*?\"<>|]", "");
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
                break;

            case "https://gravitytales.com/":
                //Chapter list at gravitytales.com/Novel/chapters
                novel.tableOfContent = Jsoup.connect(novel.novelLink+"/chapters").timeout(30 * 1000).get();
                for (Element chapterLink : novel.tableOfContent.select(chapterLinkSelector)) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                // Fetch "table of contents" page for metadata
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                break;
            case "https://booklat.com.ph/":
                novel.tempPage = Jsoup.connect(novel.novelLink+"/chapters").cookies(novel.cookies).timeout(30 * 1000).get();
                novel.tableOfContent = Jsoup.connect(novel.tempPage.select("#lnkRead").attr("abs:href")).cookies(novel.cookies).timeout(30 * 1000).get();
                Elements chaptersLinks = novel.tableOfContent.select("#ddChapter option[value]");
                for(Element chapterLink: chaptersLinks) {
                    chapters.add(new Chapter(chapterLink.text(), novel.novelLink.replace("/Info/", "/Read/") + "/" + chapterLink.attr("value")));
                }
                novel.tableOfContent = novel.tempPage;
            default:
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                for (Element chapterLink : novel.tableOfContent.select(chapterLinkSelector)) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    public Map<String, String> login() {
        Connection.Response res;
        Map<String, String> cookies = null;
        Document doc;
        System.out.println("[INFO]Login...");
        if(init.window != null) init.window.appendText("auto","[INFO]Login...");
        switch(url) {
            case "https://booklat.com.ph/":
                try {
                    res = Jsoup.connect("https://booklat.com.ph/Account/Login")
                            .method(Connection.Method.GET)
                            .execute();
                    doc = res.parse();
                    String token = doc.select("input[name=__RequestVerificationToken]").attr("value");
                    res = Jsoup.connect("https://booklat.com.ph/Account/Login")
                            .data("Email", Accounts.getUsername("Booklat"))
                            .data("Password", Accounts.getPassword("Booklat"))
                            .data("__RequestVerificationToken", token)
                            .data("RememberMe", "false")
                            .cookies(res.cookies())
                            .method(Connection.Method.POST)
                            .execute();

                    cookies = res.cookies();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "https://wuxiaworld.com/":
                try {
                    res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                            .method(Connection.Method.GET)
                            .execute();
                    doc = res.parse();
                    String token = doc.select("input[name=__RequestVerificationToken]").attr("value");
                    res = Jsoup.connect("https://www.wuxiaworld.com/account/login")
                            .data("Email", Accounts.getUsername("Wuxiaworld"))
                            .data("Password", Accounts.getPassword("Wuxiaworld"))
                            .data("__RequestVerificationToken", token)
                            .data("RememberMe", "false")
                            .cookies(res.cookies())
                            .method(Connection.Method.POST)
                            .execute();

                    cookies = res.cookies();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return cookies;
    }
}
