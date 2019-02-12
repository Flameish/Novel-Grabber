import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
/*
 * Chapter download handling
 */
public class fetchChapters {
	private static final String NL = System.getProperty("line.separator");
	private static String novelName = "toc";
	public static List<String> chapterFileNames = new ArrayList<String>(); 
	/**
	 * Opens novel's table of contents page, 
	 * retrieves chapter all links and processes them with saveChapters()
	 */
	public static void getAllChapterLinks(String url, String saveLocation, String host, String fileType) throws IllegalArgumentException, FileNotFoundException, IOException  {
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
		novelName = (doc.title().replaceAll("[^\\w]+", "-")) + " Table of Contents";
		Element content = doc.select(chapterLinkContainer).first();
		Elements chapterItem = content.select(chapterLinkSelecter);
		Elements links = chapterItem.select("a[href]");
		for (Element chapterLink : links) {
			chapterAmount++;
		}
		NovelGrabber.setMaxProgress(chapterAmount);
		for (Element chapterLink : links) {
			chapterNumber++;
			saveChapters(chapterLink.attr("href"), saveLocation, host, chapterNumber, fileType);
		}
		NovelGrabber.appendText("Finished! A total of " + chapterNumber + " chapters grabbed.");
	}
	/**
	 * Opens novel's table of contents page, 
	 * retrieves chapter links from selected chapter range and processes them with saveChapters()
	 */
	public static void getChapterRangeLinks(String url, String saveLocation, String host, int firstChapter, int lastChapter, String fileType) throws IllegalArgumentException, FileNotFoundException, IOException  {
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
		novelName = "Table-of-Contents-" + (doc.title().replaceAll("[^\\w]+", "-") + "-Chapter-" + firstChapter + "-" + lastChapter);
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
			saveChapters(chapters.get(i), saveLocation, host, chapterNumber, fileType);
		}
		NovelGrabber.appendText("Finished! A total of " + chapterNumber + " chapters grabbed.");
		}
	}
	/**
	 * Opens chapter link and tries to save it's content at provided destination directory
	 */
	public static void saveChapters(String url, String saveLocation, String host, int chapterNumber, String fileType) throws IllegalArgumentException, FileNotFoundException, IOException {
		String domainName = host;
		String chapterContainer = "";
		String sentenceSelecter = "";
		String titleReplacement = "";
		String titleHostName = "";
		switch (domainName) {
			case "wuxiaworld":
				host = "https://www.wuxiaworld.com";
				chapterContainer = ".fr-view";
				sentenceSelecter = "p";
				titleHostName = "-WuxiaWorld";
				titleReplacement = "";
				break;
			case "royalroad":
				host = "https://www.royalroad.com";
				chapterContainer = ".chapter-content";
				sentenceSelecter = "p";
				titleHostName = "-Royal-Road";
				titleReplacement = "";
				break;
			case "gravitytales":
				host = "";
				chapterContainer = ".fr-view";
				sentenceSelecter = "p";
				titleHostName = "-Gravity-Tales";
				titleReplacement = "";
				break;
			case "volarenovels":
				host = "";
				chapterContainer = ".entry-content";
				sentenceSelecter = "p";
				titleHostName = "-volare-novels";
				titleReplacement = "";
				break;
		}
		Document doc = Jsoup.connect(host + url).get();
		String fileName = "Ch-" + chapterNumber + "-" + (doc.title().replaceAll("[^\\w]+", "-").replace(titleHostName, "")) + fileType;
		Element content = doc.select(chapterContainer).first();
		Elements p = content.select(sentenceSelecter);
		File dir = new File(saveLocation);
		if (!dir.exists()) dir.mkdirs();
		if(fileType == ".txt") {
			try(PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
				for (Element x : p) {
					out.println(x.text()+ NL);
				}
			}	
		}
		else {
			try(PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
				out.print("<!DOCTYPE html>" + NL +"<html lang=\"en\">" + NL + "<head>" + NL + "<meta charset=\"utf-8\" />" + NL + "</head>" + NL +  "<body>" + NL);
				for (Element x : p) {
					out.println("<p>" + x.text() + "</p>" + NL);
				}
				out.print("</body>" + NL + "</html>");
			}
		}
		chapterFileNames.add(fileName);
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
				host = "volarenovels";
				chapterContainer = ".entry-content";
				sentenceSelecter = "p";
				break;
		}
		NovelGrabber.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		String fileName = doc.title().replaceAll("[^\\w]+", "-") + ".html";
		Element content = doc.select(chapterContainer).first();
		Elements p = content.select(sentenceSelecter);
		try(PrintStream out = new PrintStream(fileName)) {
			out.print("<!DOCTYPE html>" + NL +
					"<html lang=\"en\">" + NL + "<head>" + NL + "<meta charset=\"utf-8\" />" + NL + "</head>" + NL +  "<body>" + NL);
			for (Element x : p) {
				out.println("<p>" + x.text() + "</p>" + NL);
			}
			out.print("</body>" + NL + "</html>");
		}
		NovelGrabber.appendText(fileName + " saved.");
	}
	public static void createToc(String saveLocation) throws FileNotFoundException {
		String tocFileName = novelName + ".html";

		try(PrintStream out = new PrintStream(saveLocation + File.separator + tocFileName)) {
			out.print("<!DOCTYPE html>" + NL +"<html lang=\"en\">" + NL + "<head>" + NL + "<meta charset=\"utf-8\" />" + NL + "</head>" + NL + "<body>" + NL + "<h1>Table of Contents</h1>" + NL + "<p style=\"text-indent:0pt\">" + NL);
			for (String chapterFileName : chapterFileNames) {
			        out.print("<a href=\"" + chapterFileName + "\">" + chapterFileName +"</a><br/>" + NL);
			}
			out.print("</p>" + NL + "</body>" + NL + "</html>" + NL);
		}
	}
}
