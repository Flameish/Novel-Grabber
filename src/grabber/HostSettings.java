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
            "Comrade Mao",
            "Light Novels Translations",
            "Zenith Novels",
            "Translation Otaku",
            "Ebisu Translations",
            "ISO Hungry TLS",
            "Kuhaku Light Novel Translations",
            "Chrysanthemum Garden",
            "FicFun",
            "Fanfiktion"
    };
    private static String[] autoChapterToChapterWebsites = {
            "LiberSpark",
            "Comrade Mao",
            "Zenith Novels",
            "Translation Otaku"

    };
    private static String[] headerlessBrowserWebsites = {
            "BoxNovel",
            "Creative Novels",
            "Flying Lines",
            "FicFun"
    };
    public static List<String> headerlessBrowserWebsitesList = Arrays.asList(headerlessBrowserWebsites);
    public static List<String> autoChapterToChapterWebsitesList = Arrays.asList(autoChapterToChapterWebsites);
    private static String[] noHeaderlessBrowserWebsites = {
            "Wattpad", // pages broken
            "FanFiction", // links broeken
            "Fanfiktion"
    };
    public static List<String> noHeaderlessBrowserWebsitesList = Arrays.asList(noHeaderlessBrowserWebsites);

    public String chapterLinkSelecter;
    public String chapterLinkSelecterButton;
    String titleHostName;
    public String url;
    public String host;
    public List<String> blacklistedTags;
    int ordinalIndexForBaseNovel;
    String chapterContainer;
    String nextChapterBtn;
    String bookTitleSelector;
    String bookDescSelector;
    String bookAuthorSelector;
    String bookSubjectSelector;
    String bookCoverSelector;

    public HostSettings(String domain, String urla) {
        url = urla;
        switch (domain) {
            case "wuxiaworld": //compared from websites[] with whitespaces removed and lowercase
                host = "https://wuxiaworld.com/"; //Website URL
                ordinalIndexForBaseNovel = 5; // To trim down string to base autoNovel url
                chapterLinkSelecter = "#accordion .chapter-item a"; //Table of contents chapter links
                chapterContainer = ".p-15 .fr-view"; //chapter text
                titleHostName = "-WuxiaWorld"; //From the tab title with whitespaces removed
                blacklistedTags = new LinkedList<>(Arrays.asList("a.chapter-nav"));
                bookTitleSelector = ".p-15 h4";
                bookDescSelector = ".fr-view:not(.pt-10)";
                bookCoverSelector = "img.media-object";
                bookAuthorSelector = ".media-body dd";
                bookSubjectSelector = ".genres a";
                break;
            case "royalroad":
                host = "https://royalroad.com/";
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
                host = "https://fanfiktion.de/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "#kA option";
                chapterContainer = ".user-formatted-inner";
                nextChapterBtn = "";
                titleHostName = "|FanFiktion-de";
                blacklistedTags = null;
                bookTitleSelector = ".huge-font";
                bookDescSelector = "false";
                bookCoverSelector = "false";
                bookAuthorSelector = "a.no-wrap";
                bookSubjectSelector = "false";
                break;
            case "gravitytales":
                host = "http://gravitytales.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".table td a";
                chapterContainer = "#chapterContent";
                titleHostName = "-Gravity-Tales";
                url = urla + "/chapters"; //gravity tales' chapter list is at gravitytales.com/NOVEL/chapters
                blacklistedTags = null;
                bookTitleSelector = ".main-content h3"; //Fix
                bookDescSelector = ".desc";
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
                bookDescSelector = ".description";
                bookCoverSelector = "img.m-tb-30";
                bookAuthorSelector = "div.p-tb-10-rl-30 p"; //Fix
                bookSubjectSelector = ".tags a";
                break;
            case "wordexcerpt":
                host = "https://wordexcerpt.com/";
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
                host = "https://ficfun.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecterButton = ".button-round-red";
                chapterLinkSelecter = ".chapter-list a";
                chapterContainer = "#article-content";
                titleHostName = "";
                bookTitleSelector = ".details .name";
                bookDescSelector = ".brief";
                bookCoverSelector = ".book_left img";
                bookAuthorSelector = ".details .author";
                bookSubjectSelector = ".autoNovel-tags span";
                break;
            case "lightnovelstranslations":
                host = "https://lightnovelstranslations.com/";
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
                host = "https://boxnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a";
                chapterContainer = ".text-left";
                nextChapterBtn = ".btn.next_page";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.code-block"));
                bookTitleSelector = ".post-title h3";
                bookDescSelector = "#editdescription";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content";
                bookSubjectSelector = ".genres-content a";
                break;
            case "liberspark":
                host = "https://liberspark.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#autoNovel-chapters-list a.text-links";
                chapterContainer = "#reader-content";
                nextChapterBtn = "a:contains(Next Chapter)";
                titleHostName = "|LiberSpark";
                blacklistedTags = new LinkedList<>(Arrays.asList("div.ad-wrapper"));
                bookTitleSelector = "h1[style=text-align:left]";
                bookDescSelector = ".autoNovel-synopsis";
                bookCoverSelector = "img#uploaded-cover-image";
                bookAuthorSelector = ".autoNovel-author-info a h4";
                bookSubjectSelector = ".autoNovel-author-info a h4";
                bookSubjectSelector = "";
                break;
            case "chrysanthemumgarden":
                host = "https://chrysanthemumgarden.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".translated-chapters a";
                chapterContainer = "#autoNovel-content";
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
                bookTitleSelector = "h1.autoNovel-title";
                bookDescSelector = "false";
                bookCoverSelector = "img.materialboxed";
                bookAuthorSelector = "";
                bookSubjectSelector = ".autoNovel-container a[href^=https://chrysanthemumgarden.com/genre/]";
                break;
            case "comrademao":
                host = "https://comrademao.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "tbody tr a";
                chapterContainer = ".post > div:nth-child(2)";
                nextChapterBtn = "a.btn.btn-default.btn-sm:has(i.fa-angle-right)";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div#CollapseRaw", ".code-block"));
                bookTitleSelector = "div.wrap-content h4";
                bookDescSelector = ".wrap-content p";
                bookCoverSelector = "div.wrap-thumbnail img";
                bookAuthorSelector = "div.author";
                bookSubjectSelector = "a[rel=tag]";
                break;
            case "creativenovels":
                host = "https://creativenovels.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".post_box a";
                chapterContainer = ".post";
                nextChapterBtn = "a.x-btn.nextkey";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList(".mNS", ".support-placement"));
                bookTitleSelector = "div.e45344-16.x-text.bK_C";
                bookDescSelector = ".novel_page_synopsis";
                bookCoverSelector = "img.book_cover";
                bookAuthorSelector = "div.e45344-17.x-text.bK_C a";
                bookSubjectSelector = "div.genre_novel";
                break;
            case "wordrain":
                host = "https://wordrain69.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".main li a";
                chapterContainer = ".text-left";
                nextChapterBtn = "";
                titleHostName = "-Wordrain69";
                blacklistedTags = null;
                bookTitleSelector = ".post-title h1";
                bookDescSelector = ".summary__content";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content a";
                bookSubjectSelector = ".genres-content a";
                break;
            case "wattpad":
                host = "https://wattpad.com/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = ".table-of-contents a";
                chapterContainer = "body";
                nextChapterBtn = "";
                titleHostName = "-Wattpad";
                blacklistedTags = new LinkedList<>(Arrays.asList("span.comment-marker"));
                bookTitleSelector = ".container h1";
                bookDescSelector = "h2.description";
                bookCoverSelector = ".cover.cover-lg img";
                bookAuthorSelector = "a.send-author-event.on-navigate:not(.avatar)";
                bookSubjectSelector = ".tag-items li div.tag-item";
                break;
            case "fanfiction":
                host = "https://fanfiction.net/";
                ordinalIndexForBaseNovel = 0;
                chapterLinkSelecter = "#chap_select option";
                chapterContainer = "#storytext";
                nextChapterBtn = "";
                titleHostName = "|FanFiction";
                blacklistedTags = null;
                bookTitleSelector = "#profile_top b.xcontrast_txt";
                bookDescSelector = "div.xcontrast_txt";
                bookCoverSelector = "#profile_top img.cimage";
                bookAuthorSelector = "#profile_top a.xcontrast_txt";
                bookSubjectSelector = "";
                break;
            case "flyinglines":
                host = "https://flying-lines.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".volume-item a";
                chapterContainer = "div.content";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = "div.title h2";
                bookDescSelector = ".synopsis-detail";
                bookCoverSelector = ".autoNovel-thumb img";
                bookAuthorSelector = ".profile";
                bookSubjectSelector = ".btn-category";
                break;
            case "tapread":
                host = "https://tapread.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".chapter-list a:not(a:has(div.item-lock))";
                chapterContainer = ".chapter-entity";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = null;
                bookTitleSelector = ".book-name";
                bookDescSelector = ".desc";
                bookCoverSelector = ".book-img img";
                bookAuthorSelector = ".person-info .author .name";
                bookSubjectSelector = ".book-catalog .txt";
                break;
            case "kuhakulightnoveltranslations":
                host = "https://kuhakulightnoveltranslations.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "a.maxbutton";
                chapterContainer = ".entry-content";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("p[style=text-align: center;]"));
                bookTitleSelector = "h1.entry-title";
                bookDescSelector = "#editdescription";
                bookCoverSelector = "";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "zenithnovels":
                host = "https://zenithnovels.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".lcp_catlist li a";
                chapterContainer = ".entry";
                nextChapterBtn = ".post-next a";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("dl", ".code-block", "p:has(strong)", "hr"));
                bookTitleSelector = ".name.post-title.entry-title";
                bookDescSelector = "#editdescription";
                bookCoverSelector = ".entry p img";
                bookAuthorSelector = "";
                bookSubjectSelector = "";
                break;
            case "translationotaku":
                host = "https://translatinotaku.net/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".elementor-posts-container a";
                chapterContainer = ".text";
                nextChapterBtn = ".elementor-post-navigation__next a";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div[style=float: none; margin:10px 0 10px 0; text-align:center;]"));
                bookTitleSelector = ".elementor-heading-title";
                bookDescSelector = "#editdescription";
                bookCoverSelector = ".elementor-image img";
                bookAuthorSelector = "";
                bookSubjectSelector = ".elementor-text-editor p";
                break;
            case "isohungrytls":
                host = "https://isohungrytls.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".collapseomatic_content  a"; // there is a space in the class nam
                chapterContainer = ".entry-content";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("span.ezoic-ad", "p[style=text-align: center;]", "hr", "p:has(span[style=color: #ffffff;])"));
                bookTitleSelector = "span[style=font-size: 24pt;]";
                bookDescSelector = "blockquote";
                bookCoverSelector = "";
                bookAuthorSelector = "p:contains(Translator:)";
                bookSubjectSelector = "p:contains(Genres:)";
                break;
            case "ebisutranslations":
                host = "https://ebisutranslations.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".page_item a";
                chapterContainer = "div.page-content:nth-child(4)";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("div:has(div[class=row])", "hr", "script", "div.widget", "span[id^=ezoic]", "span[class^=ezoic]", "#disqus_thread", ".navi-div"));
                bookTitleSelector = "h1.content-header";
                bookDescSelector = "false";
                bookCoverSelector = "img";
                bookAuthorSelector = "p:contains(Translator:)";
                bookSubjectSelector = "p:contains(GenrebookDescSelector = \"false\";s:)";
                break;
            case "webnovel":
                host = "https://webnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".j_catalog_list a:not(a:has(svg))";
                chapterContainer = "div[class^=chapter_content]";
                nextChapterBtn = "";
                titleHostName = "";
                blacklistedTags = new LinkedList<>(Arrays.asList("pirate", ".cha-hr", ".cha-info", ".cha-tit p", ".j_bottom_comment_area", ".user-links-wrap", ".g_ad_ph"));
                bookTitleSelector = "p.lh24.fs16.pt24.pb24.ell.c_000 span:not(span:contains(/))";
                bookDescSelector = ".j_synopsis";
                bookCoverSelector = ".g_thumb img:eq(1)";
                bookAuthorSelector = ".ell.dib.vam span";
                bookSubjectSelector = "a[href^=/category/list?category=].c_000";
                break;
        }
    }
}
