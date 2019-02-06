import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class fetchChapters {
	public static final String NL = System.getProperty("line.separator");
	public static void getChapterList(String url, String saveLocation, String host) throws IllegalArgumentException, FileNotFoundException, IOException  {
		String domain = host;
		String contentID = "";
		String chapterSelect = "";
		int chapterNumber = 0;
		int chapterAmount = 0;
		switch (domain) {
			case "wuxiaworld":
				contentID = "#accordion";
				chapterSelect = ".chapter-item";
				break;
			case "royalroad":
				contentID = ".table";
				chapterSelect = "td";
				break;
			case "gravitytales":
				contentID = ".table";
				chapterSelect = "td";
				url = url + "/chapters";
				break;
		}
		NovelGrabber.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		Element content = doc.select(contentID).first();
		Elements chapterItem = content.select(chapterSelect);
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
	public static void saveChapters(String url, String saveLocation, String host, int chapterNumber) throws IllegalArgumentException, FileNotFoundException, IOException {
		String domain = host;
		String contentID = "";
		String chapterID = "";
		switch (domain) {
			case "wuxiaworld":
				host = "https://www.wuxiaworld.com";
				contentID = ".fr-view";
				chapterID = "p";
				break;
			case "royalroad":
				host = "https://www.royalroad.com";
				contentID = ".chapter-content";
				chapterID = "p";
				break;
			case "gravitytales":
				host = "";
				contentID = ".fr-view";
				chapterID = "p";
				break;
		}
		Document doc = Jsoup.connect(host + url).get();
		String fileName = chapterNumber + "-" + doc.title().replaceAll("[^\\w]+", "-") + ".txt";
		Element content = doc.select(contentID).first();
		Elements p = content.select(chapterID);
		
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
	public static void saveChapter(String url, String host) throws IllegalArgumentException, FileNotFoundException, IOException {
		String domain = host;
		String contentID = "";
		String chapterID = "";
		switch (domain) {
			case "wuxiaworld":
				contentID = ".fr-view";
				chapterID = "p";
				break;
			case "royalroad":
				contentID = ".chapter-content";
				chapterID = "p";
				break;
			case "gravitytales":
				contentID = ".fr-view";
				chapterID = "p";
				break;
		}
		NovelGrabber.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		String fileName = doc.title().replaceAll("[^\\w]+", "-") + ".txt";
		Element content = doc.select(contentID).first();
		Elements p = content.select(chapterID);
		try(PrintStream out = new PrintStream(fileName)) {
			for (Element x : p) {
				out.println(x.text() + NL );
			}
		}
		NovelGrabber.appendText(fileName + " saved.");
	}
}
