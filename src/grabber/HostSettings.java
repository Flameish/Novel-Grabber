package grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class HostSettings {
    public static String[] websites = {
            "Wuxiaworld",
            "Royal Road",
            "Webnovel",
            "Wattpad",
            "Gravity Tales",
            "Volare Novels",
            "Creative Novels",
            //"Flying Lines",
            "TapRead",
            "WordExcerpt",
            "FanFiction",
            "BoxNovel",
            "LiberSpark",
            "Wordrain",
            "Comrade Mao",
            "Light Novels Translations",
            "Zenith Novels",
            "Translation Otaku",
            "Ebisu Translations",
            "ISO Hungry TLS",
            "Kuhaku Light Novel Translations",
            "Chrysanthemum Garden",
            "FicFun",
            "Dreame",
            "Fanfiktion",
            "WuxiaWorld.online",
            "Novelfull",
            "WuxiaWorld.site"
    };
    private static String[] headerlessBrowserWebsites = {
            "Creative Novels",
            "FicFun",
            "Dreame",
            "WuxiaWorld.site"
    };
    private static String[] noHeaderlessBrowserWebsites = {
            "Wattpad", // pages broken
            "FanFiction", // links need processing
            "Fanfiktion" // same
    };
    public static List<String> headerlessBrowserWebsitesList = Arrays.asList(headerlessBrowserWebsites);
    public static List<String> noHeaderlessBrowserWebsitesList = Arrays.asList(noHeaderlessBrowserWebsites);

    public String chapterLinkSelecter; // Table of contents chapter links
    public String titleHostName;
    public String url; //Website URL
    public List<String> blacklistedTags;
    public int ordinalIndexForBaseNovel; // To trim down string to base autoNovel url
    public String chapterContainer; //chapter text container
    public String bookTitleSelector; //From the tab title with whitespaces removed
    public String bookDescSelector;
    public String bookAuthorSelector;
    public String bookSubjectSelector;
    public String bookCoverSelector;

    public HostSettings(String domain) {
        switch (domain) {
            case "wuxiaworld": //compared to websites[] with whitespaces removed and lowercase
                url = "https://wuxiaworld.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#accordion .chapter-item a";
                chapterContainer = ".p-15 .fr-view";
                titleHostName = "-WuxiaWorld";
                blacklistedTags = new LinkedList<>(Arrays.asList("a.chapter-nav"));
                bookTitleSelector = ".novel-body h2";
                bookDescSelector = ".fr-view:not(.pt-10)";
                bookCoverSelector = ".novel-left img";
                bookAuthorSelector = ".novel-body dd";
                bookSubjectSelector = ".genres a";
                break;
            case "royalroad":
                url = "https://royalroad.com/";
                ordinalIndexForBaseNovel = 6;
                chapterLinkSelecter = "td:not([class]) a";
                chapterContainer = ".chapter-content";
                titleHostName = "-Royal-Road";
                blacklistedTags = null;
                bookTitleSelector = "h1[property=name]";
                bookDescSelector = ".description";
                bookCoverSelector = "img.thumbnail";
                bookAuthorSelector = "h4 span[property=name] a";
                bookSubjectSelector = ".tags span";
                break;
            case "fanfiktion":
                url = "https://fanfiktion.de/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "#kA option";
                chapterContainer = ".user-formatted-inner";
                titleHostName = "|FanFiktion-de";
                blacklistedTags = null;
                bookTitleSelector = ".huge-font";
                bookDescSelector = "false";
                bookCoverSelector = "false";
                bookAuthorSelector = "a.no-wrap";
                bookSubjectSelector = "false";
                break;
            case "gravitytales":
                url = "http://gravitytales.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".table td a";
                chapterContainer = "#chapterContent";
                titleHostName = "-Gravity-Tales";
                blacklistedTags = null;
                bookTitleSelector = ".main-content h3";
                bookDescSelector = "false";
                bookCoverSelector = "";
                bookAuthorSelector = ".main-content h4";
                bookSubjectSelector = "";
                break;
            case "volarenovels":
                url = "https://volarenovels.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#accordion .chapter-item a";
                chapterContainer = ".jfontsize_content.fr-view";
                titleHostName = "-volare-novels";
                blacklistedTags = new LinkedList<>(Arrays.asList("a.chapter-nav", "div[id^=div-gpt-ad]", "span[style=font-size: 0]", "span[class=hidden-text]"));
                bookTitleSelector = "h3.title";
                bookDescSelector = ".description";
                bookCoverSelector = "img.m-tb-30";
                bookAuthorSelector = "div.p-tb-10-rl-30 p";
                bookSubjectSelector = ".tags a";
                break;
            case "wordexcerpt":
                url = "https://wordexcerpt.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a";
                chapterContainer = ".text-left";
                titleHostName = "-WordExcerpt";
                blacklistedTags = new LinkedList<>(Arrays.asList("center", "meta", "script"));
                bookTitleSelector = ".post-title h1";
                bookDescSelector = ".summary__content";
                bookCoverSelector = "div.summary_image a img";
                bookAuthorSelector = ".author-content a";
                bookSubjectSelector = ".genres-content a";
                break;
            case "ficfun":
                url = "https://ficfun.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".chapter-list a";
                chapterContainer = "#article-content";
                titleHostName = "";
                bookTitleSelector = ".details .name";
                bookDescSelector = ".brief";
                bookCoverSelector = ".book_left img";
                bookAuthorSelector = ".details .author";
                bookSubjectSelector = ".novel-tags span";
                break;
            case "dreame":
                url = "https://dreame.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".chapter-list a";
                chapterContainer = "#article-content";
                titleHostName = "";
                bookTitleSelector = ".details .name";
                bookDescSelector = ".brief";
                bookCoverSelector = ".book_left img";
                bookAuthorSelector = ".details .author";
                bookSubjectSelector = ".novel-tags span";
                break;
            case "lightnovelstranslations":
                url = "https://lightnovelstranslations.com/";
                ordinalIndexForBaseNovel = 4;
                chapterLinkSelecter = ".su-spoiler-content a";
                chapterContainer = ".entry-content";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.code-block", ".sharedaddy", "#textbox"));
                bookTitleSelector = "#content h1.entry-title";
                bookDescSelector = "false";
                bookCoverSelector = "#content p img.alignnone";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "boxnovel":
                url = "https://boxnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a";
                chapterContainer = ".text-left";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.code-block",".adbox"));
                bookTitleSelector = ".post-title h3";
                bookDescSelector = "#editdescription";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content";
                bookSubjectSelector = ".genres-content a";
                break;
            case "liberspark":
                url = "https://liberspark.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#novel-chapters-list a.text-links";
                chapterContainer = "#reader-content";
                titleHostName = "|LiberSpark";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.ad-wrapper"));
                bookTitleSelector = "h1[style=text-align:left]";
                bookDescSelector = ".novel-synopsis";
                bookCoverSelector = "img#uploaded-cover-image";
                bookAuthorSelector = ".novel-author-info a h4";
                bookSubjectSelector = ".novel-author-info a h4";
                bookSubjectSelector = "";
                break;
            case "chrysanthemumgarden":
                url = "https://chrysanthemumgarden.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".translated-chapters a";
                chapterContainer = "#novel-content";
                titleHostName = "--ChrysanthemumGarden";
                blacklistedTags = new LinkedList<>(Arrays.asList(
                        ".netlink",
                        ".chrys-ads",
                        ".google",
                        "h3[style=color:transparent;height:1px;margin:0;padding:0;overflow:hidden]",
                        "p[style=height:1px;margin:0;padding:0;overflow:hidden]",
                        "span[style=height:1px;width:0;overflow:hidden;display:inline-block]",
                        ".sharedaddy",
                        ".jum"
                ));
                bookTitleSelector = ".chapter-item a";
                bookDescSelector = "false";
                bookCoverSelector = "img.materialboxed";
                bookAuthorSelector = "";
                bookSubjectSelector = ".novel-container a[href^=https://chrysanthemumgarden.com/genre/]";
                break;
            case "comrademao":
                url = "https://comrademao.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "tbody td a";
                chapterContainer = "section#content";
                titleHostName = " - Comrade Mao";
                blacklistedTags = new LinkedList<>(Arrays.asList(".hide","#advertisment","button"));
                bookTitleSelector = "figure:nth-child(1) > h5:nth-child(1)";
                bookDescSelector = "#recentnovels > span:nth-child(1) > div:nth-child(2) > p:nth-child(3)";
                bookCoverSelector = "amp-img.i-amphtml-element";
                bookAuthorSelector = "div.author";
                bookSubjectSelector = "a[rel=tag]";
                break;
            case "creativenovels":
                url = "https://creativenovels.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".post_box a";
                chapterContainer = ".post";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList(".mNS", ".support-placement"));
                bookTitleSelector = "div.e45344-16.x-text.bK_C";
                bookDescSelector = ".novel_page_synopsis";
                bookCoverSelector = "img.book_cover";
                bookAuthorSelector = "div.e45344-17.x-text.bK_C a";
                bookSubjectSelector = "div.genre_novel";
                break;
            case "wordrain":
                url = "https://wordrain69.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".main li a";
                chapterContainer = ".text-left";
                titleHostName = "-Wordrain69";
                blacklistedTags = null;
                bookTitleSelector = ".post-title h1";
                bookDescSelector = ".summary__content";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content a";
                bookSubjectSelector = ".genres-content a";
                break;
            case "wattpad":
                url = "https://wattpad.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".table-of-contents a";
                chapterContainer = "body";
                titleHostName = "-Wattpad";
                blacklistedTags = new LinkedList<>(Arrays.asList("span.comment-marker"));
                bookTitleSelector = ".container h1";
                bookDescSelector = "h2.description";
                bookCoverSelector = ".cover.cover-lg img";
                bookAuthorSelector = "a.send-author-event.on-navigate:not(.avatar)";
                bookSubjectSelector = ".tag-items li div.tag-item";
                break;
            case "fanfiction":
                url = "https://fanfiction.net/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "#chap_select option";
                chapterContainer = "#storytext";
                titleHostName = "|FanFiction";
                blacklistedTags = null;
                bookTitleSelector = "#profile_top b.xcontrast_txt";
                bookDescSelector = "div.xcontrast_txt";
                bookCoverSelector = "#profile_top img.cimage";
                bookAuthorSelector = "#profile_top a.xcontrast_txt";
                bookSubjectSelector = "";
                break;
            case "flyinglines":
                url = "https://flying-lines.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".volume-item li:has(i:not(.detail-chapter-locked)) a";
                chapterContainer = "div.content";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = "div.title h2";
                bookDescSelector = ".synopsis-detail";
                bookCoverSelector = ".novel-thumb img";
                bookAuthorSelector = ".profile";
                bookSubjectSelector = ".btn-category";
                break;
            case "tapread":
                url = "https://tapread.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".chapter-list a:not(a:has(div.item-lock))";
                chapterContainer = ".chapter-entity";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = ".book-name";
                bookDescSelector = ".desc";
                bookCoverSelector = ".book-img img";
                bookAuthorSelector = ".person-info .author .name";
                bookSubjectSelector = ".book-catalog .txt";
                break;
            case "kuhakulightnoveltranslations":
                url = "https://kuhakulightnoveltranslations.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "a.maxbutton";
                chapterContainer = ".entry-content";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("p[style=text-align: center;]"));
                bookTitleSelector = "h1.entry-title";
                bookDescSelector = "#editdescription";
                bookCoverSelector = "";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "zenithnovels":
                url = "https://zenithnovels.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".lcp_catlist li a";
                chapterContainer = ".entry";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("dl", ".code-block", "p:has(strong)", "hr"));
                bookTitleSelector = ".name.post-title.entry-title";
                bookDescSelector = "#editdescription";
                bookCoverSelector = ".entry p img";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "translationotaku":
                url = "https://translatinotaku.net/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "section:has(nav) .elementor-posts-container a";
                chapterContainer = ".elementor-widget-container:has(p)";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("script", "div[style=position:relative;text-align:center!important]", "#videoad"));
                bookTitleSelector = ".elementor-heading-title";
                bookDescSelector = "#editdescription";
                bookCoverSelector = ".elementor-image img";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "isohungrytls":
                url = "https://isohungrytls.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".collapseomatic_content  a"; // there is a space in the class nam
                chapterContainer = ".entry-content";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("span.ezoic-ad", "p[style=text-align: center;]", "hr", "p:has(span[style=color: #ffffff;])"));
                bookTitleSelector = "span[style=font-size: 24pt;]";
                bookDescSelector = "blockquote";
                bookCoverSelector = "";
                bookAuthorSelector = "p:contains(Translator:)";
                bookSubjectSelector = "p:contains(Genres:)";
                break;
            case "ebisutranslations":
                url = "https://ebisutranslations.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".page_item a";
                chapterContainer = "div.page-content:nth-child(4)";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div:has(div[class=row])", "hr", "script", "div.widget", "span[id^=ezoic]", "span[class^=ezoic]", "#disqus_thread", ".navi-div"));
                bookTitleSelector = "h1.content-header";
                bookDescSelector = "false";
                bookCoverSelector = "img";
                bookAuthorSelector = "p:contains(Translator:)";
                bookSubjectSelector = "p:contains(GenrebookDescSelector = \"false\";s:)";
                break;
            case "webnovel":
                url = "https://webnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".j_catalog_list a:not(a:has(svg))";
                chapterContainer = "div[class^=chapter_content]";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("pirate", ".cha-hr", ".cha-info", ".cha-tit p", ".j_bottom_comment_area", ".user-links-wrap", ".g_ad_ph"));
                bookTitleSelector = "p.lh24.fs16.pt24.pb24.ell.c_000 span:not(span:contains(/))";
                bookDescSelector = ".j_synopsis";
                bookCoverSelector = ".g_thumb img:eq(1)";
                bookAuthorSelector = ".ell.dib.vam span";
                bookSubjectSelector = "a[href^=/category/list?category=].c_000";
                break;
            case "wuxiaworld.online":
                url = "https://wuxiaworld.online/";
                ordinalIndexForBaseNovel = 4;
                chapterLinkSelecter = ".chapter-list a";
                chapterContainer = ".content-area";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = "h1.entry-title";
                bookDescSelector = "#noidungm";
                bookCoverSelector = ".truyen_info_left img";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "novelfull":
                url = "http://novelfull.com/";
                ordinalIndexForBaseNovel = 4;
                chapterLinkSelecter = ".list-chapter a";
                chapterContainer = "#chapter-content";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("script", "ads", "div[align=left]", ".adsbygoogle", ".cha-tit p"));
                bookTitleSelector = "h3.title";
                bookDescSelector = "div.desc-text";
                bookCoverSelector = ".book > img:nth-child(1)";
                bookAuthorSelector = ".info > div:nth-child(1)";
                bookSubjectSelector = ".info > div:nth-child(2)";
                break;
            case "wuxiaworld.site":
                url = "https://wuxiaworld.site/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a";
                chapterContainer = ".text-left";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("script", "ad"));
                bookTitleSelector = ".post-title h3";
                bookDescSelector = ".summary__content";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content";
                bookSubjectSelector = ".genres-content a";
                break;
            default:
                url = "";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = null;
                chapterContainer = null;
                titleHostName = null;
                blacklistedTags = null;
                bookTitleSelector = null;
                bookDescSelector = null;
                bookCoverSelector = null;
                bookAuthorSelector = null;
                bookSubjectSelector = null;
                break;
        }
    }

    public List<Chapter> getChapterList(Novel novel) {
        List<Chapter> chapters = new ArrayList<>();
        try {
        switch (novel.host.url) {
            case "https://boxnovel.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                Elements chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
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
                    chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("li.next a").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
            case "https://zenithnovels.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".lcp_paginator a.lcp_nextlink").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
            case "https://translatinotaku.net/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select("a.page-numbers.next").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select("a.page-numbers.next").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));

                }
                break;
            case "https://comrademao.com/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                while (!novel.tableOfContent.select(".pagination a.next").attr("abs:href").isEmpty()) {
                    chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                    for (Element chapterLink : chapterLinks) {
                        chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                    }
                    novel.tableOfContent = Jsoup.connect(novel.tableOfContent.select(".pagination a.next").attr("abs:href")).timeout(30 * 1000).get();
                }
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
                for (Element chapterLink : chapterLinks) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
            case "https://wuxiaworld.online/":
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
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
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
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
                chapterLinks = novel.tableOfContent.select(novel.host.chapterLinkSelecter);
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
                break;

            case "https://gravitytales.com/":
                //Chapter list at gravitytales.com/Novel/chapters
                novel.tableOfContent = Jsoup.connect(novel.novelLink+"/chapters").timeout(30 * 1000).get();
                for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelecter)) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                // Fetch "table of contents" page for metadata
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                break;
            default:
                novel.tableOfContent = Jsoup.connect(novel.novelLink).timeout(30 * 1000).get();
                for (Element chapterLink : novel.tableOfContent.select(novel.host.chapterLinkSelecter)) {
                    chapters.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
                }
                break;
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapters;
    }
}
