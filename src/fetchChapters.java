import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/*
 * Chapter download handling
 */
public class fetchChapters {
	public static final String NL = System.getProperty("line.separator");
	
	/**
	 * Opens novel's table of contents page, 
	 * retrieves chapter all links and processes them with saveChapters()
	 */
	public static void getAllChapterLinks(String url, String saveLocation, String host) throws IllegalArgumentException, FileNotFoundException, IOException  {
		String domain = host;
		String chapterLinkContainer = "";
		String chapterLinkSelecter = "";
		int chapterNumber = 0;
		int chapterAmount = 0;
		switch (domain) {
			case "wuxiaworld":
				chapterLinkContainer = "#accordion";
				chapterLinkSelecter = ".chapter-item";
				break;
			case "royalroad":
				chapterLinkContainer = ".table";
				chapterLinkSelecter = "td";
				break;
			case "gravitytales":
				chapterLinkContainer = ".table";
				chapterLinkSelecter = "td";
				url = url + "/chapters";
				break;
			case "volarenovels":
				chapterLinkContainer = ".entry-content";
				chapterLinkSelecter = "a[href^="+url+"]";
				break;
		}
		NovelGrabber.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		Element content = doc.select(chapterLinkContainer).first();
		Elements chapterItem = content.select(chapterLinkSelecter);
		Elements links = chapterItem.select("a[href]");
		for (Element chapterLink : links) {
			chapterAmount++;
		}
		NovelGrabber.setMaxProgress(chapterAmount);
		for (Element chapterLink : links) {
			chapterNumber++;
			saveChapters(chapterLink.attr("href"), saveLocation, host, chapterNumber);
		}
		NovelGrabber.appendText("Finished! A total of " + chapterNumber + " chapters grabbed.");
	}
	/**
	 * Opens novel's table of contents page, 
	 * retrieves chapter links from selected chapter range and processes them with saveChapters()
	 */
	public static void getChapterRangeLinks(String url, String saveLocation, String host, int firstChapter, int lastChapter) throws IllegalArgumentException, FileNotFoundException, IOException  {
		String domain = host;
		String chapterLinkContainer = "";
		String chapterLinkSelecter = "";
		ArrayList<String> chapters = new ArrayList<String>();
		int chapterNumber = 0;
		switch (domain) {
			case "wuxiaworld":
				chapterLinkContainer = "#accordion";
				chapterLinkSelecter = ".chapter-item";
				break;
			case "royalroad":
				chapterLinkContainer = ".table";
				chapterLinkSelecter = "td";
				break;
			case "gravitytales":
				chapterLinkContainer = ".table";
				chapterLinkSelecter = "td";
				url = url + "/chapters";
				break;
			case "volarenovels":
				chapterLinkContainer = ".entry-content";
				chapterLinkSelecter = "a[href^="+url+"]";
				break;
		}
		NovelGrabber.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		Element content = doc.select(chapterLinkContainer).first();
		Elements chapterItem = content.select(chapterLinkSelecter);
		Elements links = chapterItem.select("a[href]");
		for (Element chapterLink : links) {
			chapters.add(chapterLink.attr("href"));

		}
		if(lastChapter > chapters.size()) {
			NovelGrabber.appendText("Novel does not have that many chapters.");
			return;
		}
		else {
		NovelGrabber.setMaxProgress((lastChapter-firstChapter)+1);
		for (int i = firstChapter-1; i <= lastChapter-1; i++) {
			chapterNumber++;
			saveChapters(chapters.get(i), saveLocation, host, chapterNumber);
		}
		NovelGrabber.appendText("Finished! A total of " + chapterNumber + " chapters grabbed.");
		}
	}
	/**
	 * Opens chapter link and tries to save it's content at provided destination directory
	 */
	public static void saveChapters(String url, String saveLocation, String host, int chapterNumber) throws IllegalArgumentException, FileNotFoundException, IOException {
		String domainName = host;
		String chapterContainer = "";
		String sentenceSelecter = "";
		switch (domainName) {
			case "wuxiaworld":
				host = "https://www.wuxiaworld.com";
				chapterContainer = ".fr-view";
				sentenceSelecter = "p";
				break;
			case "royalroad":
				host = "https://www.royalroad.com";
				chapterContainer = ".chapter-content";
				sentenceSelecter = "p";
				break;
			case "gravitytales":
				host = "";
				chapterContainer = ".fr-view";
				sentenceSelecter = "p";
				break;
			case "volarenovels":
				host = "";
				chapterContainer = ".entry-content";
				sentenceSelecter = "p";
				break;
		}
		Document doc = Jsoup.connect(host + url).get();
		String fileName = chapterNumber + "-" + doc.title().replaceAll("[^\\w]+", "-") + ".txt";
		Element content = doc.select(chapterContainer).first();
		Elements p = content.select(sentenceSelecter);
		File dir = new File(saveLocation);
		if (!dir.exists()) dir.mkdirs();
		try(PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
			for (Element x : p) {
				out.println(x.text() + NL);
			}
		}
		NovelGrabber.appendText(fileName + " saved.");
		NovelGrabber.updateProgress(1);
	}
	/**
	 * Opens chapter link and tries to save it's content in current directory
	 */
	public static void saveChapter(String url, String host) throws IllegalArgumentException, FileNotFoundException, IOException {
		String domainName = host;
		String chapterContainer = "";
		String sentenceSelecter = "";
		switch (domainName) {
			case "wuxiaworld":
				chapterContainer = ".fr-view";
				sentenceSelecter = "p";
				break;
			case "royalroad":
				chapterContainer = ".chapter-content";
				sentenceSelecter = "p";
				break;
			case "gravitytales":
				chapterContainer = ".fr-view";
				sentenceSelecter = "p";
				break;
			case "volarenovels":
				host = "https://archiveofourown.org";
				chapterContainer = ".entry-content";
				sentenceSelecter = "p";
				break;
		}
		NovelGrabber.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		String fileName = doc.title().replaceAll("[^\\w]+", "-") + ".txt";
		Element content = doc.select(chapterContainer).first();
		Elements p = content.select(sentenceSelecter);
		try(PrintStream out = new PrintStream(fileName)) {
			for (Element x : p) {
				out.println(x.text() + NL );
			}
		}
		NovelGrabber.appendText(fileName + " saved.");
	}
}
