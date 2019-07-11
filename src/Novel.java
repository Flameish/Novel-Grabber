/**
 * Data class 101.
 */
public class Novel {
    static String[] websites = {"Wuxiaworld", "Royal Road", "Gravity Tales", "Volare Novels",
            "Noodletown Translated", "BoxNovel", "Lightnovel Translations",
            "Exiled Rebels Scanlations", "Practical Guide to Evil"};
    private String chapterLinkSelecter;
    private String titleHostName;
    private String url;
    private String host;
    private String chapterContainer;

    public Novel(String domain, String urla) {
        url = urla;
        switch (domain) {
            case "wuxiaworld": //compared from websites[] with whitespaces removed and lowercase
                this.host = "https://wuxiaworld.com/"; //Website URL
                this.chapterLinkSelecter = "#accordion .chapter-item"; //Table of contents chapter links
                this.chapterContainer = ".p-15 .fr-view"; //chapter text
                this.titleHostName = "-WuxiaWorld"; //From the tab title with whitespaces removed
                break;
            case "royalroad":
                this.host = "https://www.royalroad.com/";
                this.chapterLinkSelecter = ".table td:not([class])";
                this.chapterContainer = ".chapter-content";
                this.titleHostName = "-Royal-Road";
                break;
            case "gravitytales":
                this.host = "http://gravitytales.com/";
                this.chapterLinkSelecter = ".table td";
                this.chapterContainer = "#chapterContent";
                this.titleHostName = "-Gravity-Tales";
                url = urla + "/chapters"; //gravity tales' chapter list is at gravitytales.com/NOVEL/chapters
                break;
            case "volarenovels":
                this.host = "https://volarenovels.com/";
                this.chapterLinkSelecter = "#accordion .chapter-item a";
                this.chapterContainer = ".jfontsize_content.fr-view";
                this.titleHostName = "-volare-novels";
                break;
            case "noodletowntranslated":
                this.host = "https://www.noodletowntranslated.com/";
                this.chapterLinkSelecter = "table a";
                this.chapterContainer = ".post-inner .post-content";
                this.titleHostName = "-Noodletown-Translated";
                break;
            case "boxnovel":
                this.host = "https://boxnovel.com/";
                this.chapterLinkSelecter = ".listing-chapters_wrap a";
                this.chapterContainer = ".text-left";
                this.titleHostName = "";
                break;
            case "lightnoveltranslations":
                this.host = "https://lightnovelstranslations.com/";
                this.chapterLinkSelecter = ".entry-content a[href^=" + urla + "]";
                this.chapterContainer = ".entry-content";
                this.titleHostName = "";
                break;
            case "exiledrebelsscanlations":
                this.host = "https://exiledrebelsscanlations.com/";
                this.chapterLinkSelecter = ".lcp_catlist a[href^=https://exiledrebelsscanlations.com/]";
                this.chapterContainer = ".entry-content";
                this.titleHostName = "";
                break;
            case "practicalguidetoevil":
                this.host = "https://practicalguidetoevil.wordpress.com/";
                this.chapterLinkSelecter = ".entry-content li a";
                this.chapterContainer = ".entry-content";
                this.titleHostName = "";
                break;
        }
    }
    String getChapterLinkSelector() {
        return this.chapterLinkSelecter;
    }

    String getTitleHostName() {
        return this.titleHostName;
    }

    String getUrl() {
        return this.url;
    }

    String getHost() {
        return this.host;
    }

    String getChapterContainer() {
        return this.chapterContainer;
    }
}
