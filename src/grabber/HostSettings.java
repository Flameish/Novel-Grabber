package grabber;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Data class 101.
 */
public class HostSettings {
    public static String[] websites = {
            "Wuxiaworld",
            "Royal Road",
            "Webnovel",
            "Wattpad",
            "Gravity Tales",
            "Volare Novels",
            "Creative Novels",
            "Flying Lines",
            "TapRead",
            "WordExcerpt",
            "FanFiction",
            "BoxNovel",
            "LiberSpark",
            "Wordrain",
            "Comrademao",
            "Light Novels Translations",
            "Zenith Novels",
            "Translation Otaku",
            "Ebisu Translations",
            "ISO Hungry TLS",
            "Kuhaku Light Novel Translations",
            "Chrysanthemum Garden"
    };
    private static String[] autoChapterToChapterWebsites = {
            "Comrademao",
            "Zenith Novels",
            "Translation Otaku",
            "Creative Novels",
            "BoxNovel"
    };
    public static List<String> autoChapterToChapterWebsitesList = Arrays.asList(autoChapterToChapterWebsites);


    public String chapterLinkSelecter;
    String titleHostName;
    public String url;
    public String host;
    public List<String> blacklistedTags;
    int ordinalIndexForBaseNovel;
    String chapterContainer;
    String nextChapterBtn;
    String bookTitleSelector;
    String bookAuthorSelector;
    String bookSubjectSelector;
    String bookCoverSelector;

    public HostSettings(String domain, String urla) {
        url = urla;
        switch (domain) {
            case "wuxiaworld": //compared from websites[] with whitespaces removed and lowercase
                host = "https://wuxiaworld.com/"; //Website URL
                ordinalIndexForBaseNovel = 5; // To trim down string to base novel url
                chapterLinkSelecter = "#accordion .chapter-item"; //Table of contents chapter links
                chapterContainer = ".p-15 .fr-view"; //chapter text
                titleHostName = "-WuxiaWorld"; //From the tab title with whitespaces removed
                blacklistedTags = new LinkedList<>(Arrays.asList("a.chapter-nav"));
                bookTitleSelector = ".p-15 h4";
                bookCoverSelector = "img.media-object";
                bookAuthorSelector = ".media-body dd";
                bookSubjectSelector = ".genres a";
                break;
            case "royalroad":
                host = "https://www.royalroad.com/";
                ordinalIndexForBaseNovel = 6;
                chapterLinkSelecter = ".table td:not([class])";
                chapterContainer = ".chapter-content";
                titleHostName = "-Royal-Road";
                blacklistedTags = null;
                bookTitleSelector = "h1[property=name]";
                bookCoverSelector = "img.thumbnail";
                bookAuthorSelector = "h4 span[property=name] a";
                bookSubjectSelector = ".tags span";
                break;
            case "gravitytales":
                host = "http://gravitytales.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".table td";
                chapterContainer = "#chapterContent";
                titleHostName = "-Gravity-Tales";
                url = urla + "/chapters"; //gravity tales' chapter list is at gravitytales.com/NOVEL/chapters
                blacklistedTags = null;
                bookTitleSelector = ".main-content h3"; //Fix
                bookCoverSelector = "#coverImg";
                bookAuthorSelector = ".main-content h4";
                bookSubjectSelector = ".desc p";
                break;
            case "volarenovels":
                host = "https://volarenovels.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#accordion .chapter-item a";
                chapterContainer = ".jfontsize_content.fr-view";
                titleHostName = "-volare-novels";
                blacklistedTags = new LinkedList<>(Arrays.asList("a.chapter-nav", "div[id^=div-gpt-ad]", "span[style=font-size: 0]", "span[class=hidden-text]"));
                bookTitleSelector = "h3.title";
                bookCoverSelector = "img.m-tb-30";
                bookAuthorSelector = "div.p-tb-10-rl-30 p"; //Fix
                bookSubjectSelector = ".tags a";
                break;
            case "wordexcerpt":
                host = "https://wordexcerpt.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a[href]";
                chapterContainer = ".text-left";
                titleHostName = "-WordExcerpt";
                blacklistedTags = new LinkedList<>(Arrays.asList("center", "meta", "script"));
                bookTitleSelector = ".post-title h1";
                bookCoverSelector = "div.summary_image a img";
                bookAuthorSelector = ".author-content a";
                bookSubjectSelector = ".genres-content a";
                break;
            case "lightnovelstranslations":
                host = "https://lightnovelstranslations.com/";
                ordinalIndexForBaseNovel = 4;
                chapterLinkSelecter = ".entry-content a[href^=" + urla + "]:not(a[rel])";
                chapterContainer = ".entry-content";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.code-block", ".sharedaddy", "#textbox"));
                bookTitleSelector = "#content h1.entry-title";
                bookCoverSelector = "#content p img.alignnone";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "boxnovel":
                host = "https://boxnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a";
                chapterContainer = ".text-left";
                nextChapterBtn = ".btn.next_page";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.code-block"));
                bookTitleSelector = ".post-title h3";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content";
                bookSubjectSelector = ".genres-content a";
                break;
            case "liberspark":
                host = "https://liberspark.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#novel-chapters-list a.text-links";
                chapterContainer = "#reader-content";
                titleHostName = "|LiberSpark";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.ad-wrapper"));
                bookTitleSelector = "h1[style=text-align:left]";
                bookCoverSelector = "img#uploaded-cover-image";
                bookAuthorSelector = ".novel-author-info a h4";
                bookSubjectSelector = ".novel-author-info a h4";
                bookSubjectSelector = "";
                break;
            case "chrysanthemumgarden":
                host = "https://chrysanthemumgarden.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".translated-chapters a";
                chapterContainer = "#novel-content";
                titleHostName = "--ChrysanthemumGarden";
                blacklistedTags = new LinkedList<>(Arrays.asList(".chrys-ads",
                        ".google",
                        "h3[style=color:transparent;height:1px;margin:0;padding:0;overflow:hidden]",
                        "p[style=height:1px;margin:0;padding:0;overflow:hidden]",
                        "span[style=height:1px;width:0;overflow:hidden;display:inline-block]",
                        ".sharedaddy",
                        ".jum"));
                bookTitleSelector = "h1.novel-title";
                bookCoverSelector = "img.materialboxed";
                bookAuthorSelector = "";
                bookSubjectSelector = ".novel-container a[href^=https://chrysanthemumgarden.com/genre/]";
                break;
            case "comrademao":
                host = "https://comrademao.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "";
                chapterContainer = "article div";
                nextChapterBtn = "a.btn.btn-default.btn-sm:has(i.fa-angle-right)";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div#CollapseRaw"));
                bookTitleSelector = "div.wrap-content h4";
                bookCoverSelector = "div.wrap-thumbnail img";
                bookAuthorSelector = "div.author";
                bookSubjectSelector = "a[rel=tag]";
                break;
            case "creativenovels":
                host = "https://creativenovels.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "";
                chapterContainer = ".post";
                nextChapterBtn = "a.x-btn.nextkey";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList(".mNS", ".support-placement"));
                bookTitleSelector = "div.e45344-16.x-text.bK_C";
                bookCoverSelector = "img.book_cover";
                bookAuthorSelector = "div.e45344-17.x-text.bK_C a";
                bookSubjectSelector = "div.genre_novel";
                break;
            case "wordrain":
                host = "https://wordrain69.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "ul.main.version-chap li a:not([title])";
                chapterContainer = ".text-left";
                nextChapterBtn = "";
                titleHostName = "-Wordrain69";
                blacklistedTags = null;
                bookTitleSelector = ".post-title h1";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content a";
                bookSubjectSelector = ".genres-content a";
                break;
            case "wattpad":
                host = "https://www.wattpad.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".table-of-contents a";
                chapterContainer = "div.page pre";
                nextChapterBtn = "";
                titleHostName = "-Wattpad";
                blacklistedTags = new LinkedList<>(Arrays.asList("span.comment-marker"));
                bookTitleSelector = ".container h1";
                bookCoverSelector = ".cover.cover-lg img";
                bookAuthorSelector = "a.send-author-event.on-navigate:not(.avatar)";
                bookSubjectSelector = ".tag-items li div.tag-item";
                break;
            case "fanfiction":
                host = "https://www.fanfiction.net/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "#chap_select option";
                chapterContainer = "#storytext";
                nextChapterBtn = "";
                titleHostName = "|FanFiction";
                blacklistedTags = null;
                bookTitleSelector = "#profile_top b.xcontrast_txt";
                bookCoverSelector = "#profile_top img.cimage";
                bookAuthorSelector = "#profile_top a.xcontrast_txt";
                bookSubjectSelector = "";
                break;
            case "flyinglines":
                host = "https://www.flying-lines.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".volume-item a";
                chapterContainer = "body";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = "div.title h2";
                bookCoverSelector = ".novel-thumb img";
                bookAuthorSelector = ".profile";
                bookSubjectSelector = ".btn-category";
                break;
            case "tapread":
                host = "https://www.tapread.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".volume-item a";
                chapterContainer = "";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = ".book-name";
                bookCoverSelector = ".book-img img";
                bookAuthorSelector = ".person-info .author .name";
                bookSubjectSelector = ".book-catalog .txt";
                break;
            case "kuhakulightnoveltranslations":
                host = "https://kuhakulightnoveltranslations.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".maxbutton";
                chapterContainer = ".entry-content";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("p[style=text-align: center;]"));
                bookTitleSelector = "h1.entry-title";
                bookCoverSelector = "";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "zenithnovels":
                host = "https://zenithnovels.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "";
                chapterContainer = ".entry";
                nextChapterBtn = ".post-next a";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("dl", ".code-block", "p:has(strong)", "hr"));
                bookTitleSelector = ".name.post-title.entry-title";
                bookCoverSelector = ".entry p img";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "translationotaku":
                host = "https://translatinotaku.net/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "";
                chapterContainer = ".elementor-widget-container:has(p)";
                nextChapterBtn = ".elementor-post-navigation__next a";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div[style=float: none; margin:10px 0 10px 0; text-align:center;]"));
                bookTitleSelector = ".elementor-heading-title";
                bookCoverSelector = ".elementor-image img";
                bookAuthorSelector = "";
                bookSubjectSelector = ".elementor-text-editor p";
                break;
            case "isohungrytls":
                host = "https://isohungrytls.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "a[href^=" + url.substring(0, url.length() - 1) + "]:not(a[aria-current])";
                chapterContainer = ".entry-content";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("span.ezoic-ad", "p[style=text-align: center;]", "hr", "p:has(span[style=color: #ffffff;])"));
                bookTitleSelector = "span[style=font-size: 24pt;]";
                bookCoverSelector = "img";
                bookAuthorSelector = "p:contains(Translator:)";
                bookSubjectSelector = "p:contains(Genres:)";
                break;
            case "ebisutranslations":
                host = "https://ebisutranslations.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".page_item";
                chapterContainer = ".page-content:has(h1)";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div:has(div[class=row])", "hr", "script", "div.widget", "span[id^=ezoic]", "span[class^=ezoic]", "#disqus_thread"));
                bookTitleSelector = "h1.content-header";
                bookCoverSelector = "img";
                bookAuthorSelector = "p:contains(Translator:)";
                bookSubjectSelector = "p:contains(Genres:)";
                break;
            case "webnovel":
                host = "https://www.webnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "a";
                chapterContainer = ".cha-words";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("pirate"));
                bookTitleSelector = "p.lh24.fs16.pt24.pb24.ell.c_000 span:not(span:contains(/))";
                bookCoverSelector = ".g_thumb img:eq(1)";
                bookAuthorSelector = ".ell.dib.vam span";
                bookSubjectSelector = "a[href^=/category/list?category=].c_000";
                break;
        }
    }
}
