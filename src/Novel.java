public class Novel {
    static String[] websites = {"Wuxiaworld", "Royal Road", "Gravity Tales", "Volare Novels",
            "Noodletown Translated", "BoxNovel", "Lightnovel Translations", "Fuji Translation",
            "Exiled Rebels Scanlations", "Rainbow Turtle Translations"};
    private String chapterLinkContainer;
    private String chapterLinkSelecter;
    private String titleHostName;
    private String url;
    private String host;
    private String chapterContainer;
    private String sentenceSelecter;

    public Novel(String domain, String urla) {
        url = urla;
        switch (domain) {
            case "wuxiaworld":
                this.host = "https://wuxiaworld.com/";
                this.chapterLinkContainer = "#accordion";
                this.chapterLinkSelecter = ".chapter-item";
                this.chapterContainer = ".p-15 .fr-view";
                this.sentenceSelecter = "p";
                this.titleHostName = "-WuxiaWorld";
                break;
            case "royalroad":
                this.host = "https://www.royalroad.com/";
                this.chapterLinkContainer = ".table";
                this.chapterLinkSelecter = "td";
                this.chapterContainer = ".chapter-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "-Royal-Road";
                break;
            case "gravitytales":
                this.host = "http://gravitytales.com/";
                this.chapterLinkContainer = ".table";
                this.chapterLinkSelecter = "td";
                this.chapterContainer = ".fr-view";
                this.sentenceSelecter = "p";
                this.titleHostName = "-Gravity-Tales";
                url = urla + "/chapters";
                break;
            case "volarenovels":
                this.host = "https://volarenovels.com/";
                this.chapterLinkContainer = "#accordion";
                this.chapterLinkSelecter = "a";
                this.chapterContainer = ".panel .fr-view";
                this.sentenceSelecter = "p";
                this.titleHostName = "-volare-novels";
                break;
            case "noodletowntranslated":
                this.host = "https://www.noodletowntranslated.com/";
                this.chapterLinkContainer = "table";
                this.chapterLinkSelecter = "a";
                this.chapterContainer = ".post-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "-Noodletown-Translated";
                break;
            case "boxnovel":
                this.host = "https://boxnovel.com/";
                this.chapterLinkContainer = ".listing-chapters_wrap";
                this.chapterLinkSelecter = "a";
                this.chapterContainer = ".cha-words";
                this.sentenceSelecter = "p";
                this.titleHostName = "";
                break;
            case "lightnoveltranslations":
                this.host = "https://lightnovelstranslations.com/";
                this.chapterLinkContainer = ".entry-content";
                this.chapterLinkSelecter = "a[href^=" + urla + "]";
                this.chapterContainer = ".entry-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "";
                break;
            case "fujitranslation":
                this.host = "https://fujitranslation.com/";
                this.chapterLinkContainer = ".entry-content";
                this.chapterLinkSelecter = "a[href^=" + urla + "]";
                this.chapterContainer = ".entry-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "";
                break;
            case "exiledrebelsscanlations":
                this.host = "https://exiledrebelsscanlations.com/";
                this.chapterLinkContainer = ".lcp_catlist";
                this.chapterLinkSelecter = "a[href^=https://exiledrebels]";
                this.chapterContainer = ".entry-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "";
                break;
            case "rainbowturtletranslations":
                this.host = "https://arkmachinetranslations.wordpress.com/";
                this.chapterLinkContainer = ".entry-content";
                this.chapterLinkSelecter = "a[href^=https://arkmachinetranslations]";
                this.chapterContainer = ".entry-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "";
                break;
            case "creativenovels":
                this.host = "https://creativenovels.com/";
                this.chapterLinkContainer = ".post_box";
                this.chapterLinkSelecter = "a";
                this.chapterContainer = ".entry-content";
                this.sentenceSelecter = "p";
                this.titleHostName = "";
                break;
        }
    }

    String getChapterLinkContainer() {
        return this.chapterLinkContainer;
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

    String getSentenceSelector() {
        return this.sentenceSelecter;
    }
}
