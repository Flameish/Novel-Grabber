
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
			"Exiled Rebels Scanlations", "Rainbow Turtle Translations(Arkmachinetranslations)"};

	public Novel(String domain, String urla) {
		url = urla;
		switch (domain) { //compared with websites[] (spaces removed + all letters to lowercase)
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
			this.chapterLinkContainer = ".entry-content";
			this.chapterLinkSelecter = "a[href^=" + urla + "]";
			this.chapterContainer = ".entry-content";
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
			this.chapterLinkSelecter = "a[href^=https://fujitranslation]";
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
		case "rainbowturtletranslations(arkmachinetranslations)":
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

	public String getChapterLinkContainer() {
		return this.chapterLinkContainer;
	}

	public String getChapterLinkSelecter() {
		return this.chapterLinkSelecter;
	}

	public String getTitleHostName() {
		return this.titleHostName;
	}

	public String getUrl() {
		return this.url;
	}

	public String getHost() {
		return this.host;
	}

	public String getChapterContainer() {
		return this.chapterContainer;
	}

	public String getSentenceSelecter() {
		return this.sentenceSelecter;
	}
}
