import java.util.Arrays;
import java.util.List;

/**
 * Data class 101.
 */
class HostSettings {
    static String[] websites = {
            "Wuxiaworld",
            "Royal Road",
            "Gravity Tales",
            "Volare Novels",
            "Light Novels Translations",
            "WordExcerpt",
            "BoxNovel",
            "LiberSpark",
            "Chrysanthemum Garden"
    };
    String chapterLinkSelecter;
    String titleHostName;
    String url;
    String host;
    String chapterContainer;
    String bookTitleSelector;
    String bookCoverSelector;
    String bookAuthorSelector;
    List<String> blacklistedTags;
    int ordinalIndexForBaseNovel;

    HostSettings(String domain, String urla) {
        url = urla;
        switch (domain) {
            case "wuxiaworld": //compared from websites[] with whitespaces removed and lowercase
                host = "https://wuxiaworld.com/"; //Website URL
                ordinalIndexForBaseNovel = 5; // To trim down string to base novel url
                chapterLinkSelecter = "#accordion .chapter-item"; //Table of contents chapter links
                chapterContainer = ".p-15 .fr-view"; //chapter text
                titleHostName = "-WuxiaWorld"; //From the tab title with whitespaces removed
                blacklistedTags = Arrays.asList("a.chapter-nav");
                bookTitleSelector = ".p-15 h4";
                bookCoverSelector = "img.media-object";
                bookAuthorSelector = ".media-body dd";
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
                break;
            case "gravitytales":
                host = "http://gravitytales.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".table td";
                chapterContainer = "#chapterContent";
                titleHostName = "-Gravity-Tales";
                url = urla + "/chapters"; //gravity tales' chapter list is at gravitytales.com/NOVEL/chapters
                blacklistedTags = null;
                bookTitleSelector = ".p-tb-10-rl-30 h3"; //Fix
                bookCoverSelector = "";
                bookAuthorSelector = "div.p-tb-10-rl-30 p :not(b)";
                break;
            case "volarenovels":
                host = "https://volarenovels.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#accordion .chapter-item a";
                chapterContainer = ".jfontsize_content.fr-view";
                titleHostName = "-volare-novels";
                blacklistedTags = Arrays.asList("a.chapter-nav", "div[id^=div-gpt-ad]", "span[style=font-size: 0]", "span[class=hidden-text]");
                bookTitleSelector = "h3.title";
                bookCoverSelector = "img.m-tb-30";
                bookAuthorSelector = "div.p-tb-10-rl-30 p"; //Fix
                break;
            case "wordexcerpt":
                host = "https://wordexcerpt.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a[href]";
                chapterContainer = ".text-left";
                titleHostName = "-WordExcerpt";
                blacklistedTags = Arrays.asList("center", "meta", "script");
                bookTitleSelector = ".post-title h1";
                bookCoverSelector = "div.summary_image a img";
                bookAuthorSelector = ".author-content a";
                break;
            case "lightnovelstranslations":
                host = "https://lightnovelstranslations.com/";
                ordinalIndexForBaseNovel = 4;
                chapterLinkSelecter = ".entry-content a[href^=" + urla + "]:not(a[rel])";
                chapterContainer = ".entry-content";
                titleHostName = "";
                blacklistedTags = Arrays.asList("div.code-block", ".sharedaddy", "#textbox");
                bookTitleSelector = "#content h1.entry-title";
                bookCoverSelector = "#content p img.alignnone";
                bookAuthorSelector = "";
                break;
            case "boxnovel":
                host = "https://boxnovel.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".listing-chapters_wrap a";
                chapterContainer = ".text-left";
                titleHostName = "";
                blacklistedTags = Arrays.asList("div.code-block");
                bookTitleSelector = ".post-title h3";
                bookCoverSelector = ".summary_image img";
                bookAuthorSelector = ".author-content";
                break;
            case "liberspark":
                host = "https://liberspark.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = "#novel-chapters-list a.text-links";
                chapterContainer = "#reader-content";
                titleHostName = "|LiberSpark";
                blacklistedTags = Arrays.asList("div.ad-wrapper");
                bookTitleSelector = "h1[style=text-align:left]";
                bookCoverSelector = "img#uploaded-cover-image";
                bookAuthorSelector = ".novel-author-info a h4";
                break;
            case "chrysanthemumgarden":
                host = "https://chrysanthemumgarden.com/";
                ordinalIndexForBaseNovel = 5;
                chapterLinkSelecter = ".translated-chapters a";
                chapterContainer = "#novel-content";
                titleHostName = "--ChrysanthemumGarden";
                blacklistedTags = Arrays.asList("div.chrys-ads",
                        "h3[style=color:transparent;height:1px;margin:0;padding:0;overflow:hidden]",
                        "p[style=height:1px;margin:0;padding:0;overflow:hidden]",
                        "span[style=height:1px;width:0;overflow:hidden;display:inline-block]",
                        ".sharedaddy",
                        ".jum");
                bookTitleSelector = "h1.novel-title";
                bookCoverSelector = "img.materialboxed";
                bookAuthorSelector = "";
                break;
        }
    }
}
