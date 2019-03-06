
public class Novel {
	String chapterLinkContainer;
	String chapterLinkSelecter;
	String titleHostName;
	String url;
	String host;
	String chapterContainer;
	String sentenceSelecter;
	public static String[] websites = { "Wuxiaworld", "Royal Road", "Gravity Tales", "Volare Novels",
			"Noodletown Translated", "BoxNovel", "Lightnovel Translations", "Fuji Translation",
			"Exiled Rebels Scanlations", "Rainbow Turtle Translations(Arkmachinetranslations)" };

	public Novel(String domain, String url) {
		switch (domain) {
		case "wuxiaworld":
			host = "https://www.wuxiaworld.com";
			chapterLinkContainer = "#accordion";
			chapterLinkSelecter = ".chapter-item";
			chapterContainer = ".fr-view";
			sentenceSelecter = "p";
			titleHostName = "-WuxiaWorld";
			break;
		case "royalroad":
			host = "https://www.royalroad.com";
			chapterLinkContainer = ".table";
			chapterLinkSelecter = "td";
			chapterContainer = ".chapter-content";
			sentenceSelecter = "p";
			titleHostName = "-Royal-Road";
			break;
		case "gravitytales":
			host = "";
			chapterLinkContainer = ".table";
			chapterLinkSelecter = "td";
			chapterContainer = ".fr-view";
			sentenceSelecter = "p";
			url = url + "/chapters";
			titleHostName = "-Gravity-Tales";
			break;
		case "volarenovels":
			host = "";
			chapterLinkContainer = ".entry-content";
			chapterLinkSelecter = "a[href^=" + url + "]";
			chapterContainer = ".entry-content";
			sentenceSelecter = "p";
			titleHostName = "-volare-novels";
			break;
		case "noodletowntranslated":
			host = "";
			chapterLinkContainer = "table";
			chapterLinkSelecter = "a";
			chapterContainer = ".post-content";
			sentenceSelecter = "p";
			titleHostName = "-Noodletown-Translated";
			break;
		case "boxnovel":
			host = "";
			chapterLinkContainer = ".listing-chapters_wrap";
			chapterLinkSelecter = "a";
			chapterContainer = ".cha-words";
			sentenceSelecter = "p";
			titleHostName = "";
			break;
		case "lightnoveltranslations":
			host = "";
			chapterLinkContainer = ".entry-content";
			chapterLinkSelecter = "a[href^=" + url + "]";
			chapterContainer = ".entry-content";
			sentenceSelecter = "p";
			titleHostName = "";
			break;
		case "fujitranslation":
			host = "";
			chapterLinkContainer = ".entry-content";
			chapterLinkSelecter = "a[href^=https://fujitranslation]";
			chapterContainer = ".entry-content";
			sentenceSelecter = "p";
			titleHostName = "";
			break;
		case "exiledrebelsscanlations":
			host = "";
			chapterLinkContainer = ".lcp_catlist";
			chapterLinkSelecter = "a[href^=https://exiledrebels]";
			chapterContainer = ".entry-content";
			sentenceSelecter = "p";
			titleHostName = "";
			break;
		case "rainbowturtletranslations(arkmachinetranslations)":
			host = "";
			chapterLinkContainer = ".entry-content";
			chapterLinkSelecter = "a[href^=https://arkmachinetranslations]";
			chapterContainer = ".entry-content";
			sentenceSelecter = "p";
			titleHostName = "";
			break;
		}
		
	}

	public String getChapterLinkContainer() {
		return chapterLinkContainer;
	}

	public String getChapterLinkSelecter() {
		return chapterLinkSelecter;
	}

	public String getTitleHostName() {
		return titleHostName;
	}

	public String getUrl() {
		return url;
	}

	public String getHost() {
		return host;
	}

	public String getChapterContainer() {
		return chapterContainer;
	}

	public String getSentenceSelecter() {
		return sentenceSelecter;
	}
}
