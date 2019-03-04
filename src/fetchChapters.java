import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Chapter download handling
 */
public class fetchChapters {
	public static boolean error = false;
	private static final String NL = System.getProperty("line.separator");
	private static String tocFileName = "toc";
	public static List<String> chapterFileNames = new ArrayList<String>();
	public static List<String> chapterUrl = new ArrayList<String>();

	/**
	 * Opens novel's table of contents page, retrieves chapter all links and
	 * processes them with saveChapters().
	 */
	public static void getAllChapterLinks(String url, String saveLocation, String host, String fileType,
			boolean chapterNumeration, boolean invertOrder)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		Novel currentNovel = new Novel(host, url);
		String titleReplacement = "";
		ArrayList<String> chaptersNames = new ArrayList<String>();
		int chapterNumber = 0;
		int chapterAmount = 0;

		NovelGrabberGUI.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		tocFileName = (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(),
				titleReplacement)) + " Table of Contents";
		Element content = doc.select(currentNovel.getChapterLinkContainer()).first();
		Elements chapterItem = content.select(currentNovel.getChapterLinkSelecter());
		Elements links = chapterItem.select("a[href]");
		for (Element chapterLink : links) {
			chapterAmount++;
			chaptersNames.add(chapterLink.text());
		}
		if (invertOrder == true) {
			Collections.reverse(chaptersNames);
		}
		NovelGrabberGUI.setMaxProgress("auto", chapterAmount);
		for (Element chapterLink : links) {
			chapterNumber++;
			saveChapters(chapterLink.attr("href"), saveLocation, host, chapterNumber, fileType, chapterNumeration,
					chaptersNames.get(chapterNumber - 1));
		}
		NovelGrabberGUI.appendText("Finished! A total of " + chapterNumber + " chapter grabbed.");
	}

	/**
	 * Opens novel's table of contents page, retrieves chapter links from selected
	 * chapter range and processes them with saveChapters()
	 */
	public static void getChapterRangeLinks(String url, String saveLocation, String host, int firstChapter,
			int lastChapter, String fileType, boolean chapterNumeration, boolean invertOrder)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		error = false;
		Novel currentNovel = new Novel(host, url);
		ArrayList<String> chapters = new ArrayList<String>();
		ArrayList<String> chaptersNames = new ArrayList<String>();
		String titleReplacement = "";
		int chapterNumber = 0;
		NovelGrabberGUI.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		tocFileName = "Table-of-Contents-"
				+ (doc.title().replaceAll("[^\\w]+", "-").replace(currentNovel.getTitleHostName(), titleReplacement)
						+ "-Chapter-" + firstChapter + "-" + lastChapter);
		Element content = doc.select(currentNovel.getChapterLinkContainer()).first();
		Elements chapterItem = content.select(currentNovel.getChapterLinkSelecter());
		Elements links = chapterItem.select("a[href]");
		for (Element chapterLink : links) {
			chapters.add(chapterLink.attr("href"));
			chaptersNames.add(chapterLink.text());

		}
		if (lastChapter > chapters.size()) {
			NovelGrabberGUI.appendText("Novel does not have that many chapters.");
			error = true;
			return;
		} else {
			if (invertOrder == true) {
				Collections.reverse(chapters);
				Collections.reverse(chaptersNames);
			}
			NovelGrabberGUI.setMaxProgress("auto", (lastChapter - firstChapter) + 1);
			for (int i = firstChapter - 1; i <= lastChapter - 1; i++) {
				chapterNumber++;
				saveChapters(chapters.get(i), saveLocation, host, chapterNumber, fileType, chapterNumeration,
						chaptersNames.get(i));
			}
			NovelGrabberGUI.appendText("Finished! A total of " + chapterNumber + " chapter grabbed.");
		}
	}

	/**
	 * Opens chapter link and tries to save it's content at provided destination
	 * directory
	 */
	public static void saveChapters(String url, String saveLocation, String host, int chapterNumber, String fileType,
			boolean chapterNumeration, String fileName)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		Novel currentNovel = new Novel(host, url);
		Document doc = Jsoup.connect(currentNovel.getHost() + url).get();
		// Chapter numeration in filename
		if (chapterNumeration == false) {
			fileName = fileName.replaceAll("[^\\w]+", "-") + fileType;
		} else {
			fileName = "Ch-" + chapterNumber + "-" + fileName.replaceAll("[^\\w]+", "-") + fileType;
		}

		Element content = doc.select(currentNovel.getChapterContainer()).first();
		Elements p = content.select(currentNovel.getSentenceSelecter());
		File dir = new File(saveLocation);
		if (!dir.exists())
			dir.mkdirs();
		if (fileType == ".txt") {
			try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
				for (Element x : p) {
					out.println(x.text() + NL);
				}
			}
		} else {
			try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
				out.print("<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL
						+ "<meta charset=\"utf-8\" />" + NL + "</head>" + NL + "<body>" + NL);
				for (Element x : p) {
					out.println("<p>" + x.text() + "</p>" + NL);
				}
				out.print("</body>" + NL + "</html>");
			}
		}
		chapterFileNames.add(fileName);
		NovelGrabberGUI.appendText(fileName + " saved.");
		NovelGrabberGUI.updateProgress("auto", 1);
	}

	/**
	 * Opens chapter link and tries to save it's content in current directory
	 */
	public static void saveChapter(String url, String host)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		Novel currentNovel = new Novel(host, url);
		NovelGrabberGUI.appendText("Connecting...");
		Document doc = Jsoup.connect(url).get();
		String fileName = doc.title().replaceAll("[^\\w]+", "-") + ".html";
		Element content = doc.select(currentNovel.getChapterContainer()).first();
		Elements p = content.select(currentNovel.getSentenceSelecter());
		try (PrintStream out = new PrintStream(fileName)) {
			out.print("<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL + "<meta charset=\"utf-8\" />"
					+ NL + "</head>" + NL + "<body>" + NL);
			for (Element x : p) {
				out.println("<p>" + x.text() + "</p>" + NL);
			}
			out.print("</body>" + NL + "</html>");
		}
		NovelGrabberGUI.appendText(fileName + " saved.");
	}

	public static void createToc(String saveLocation) throws FileNotFoundException {
		String fileName = tocFileName + ".html";

		try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
			out.print("<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL + "<meta charset=\"UTF-8\" />"
					+ NL + "</head>" + NL + "<body>" + NL + "<h1>Table of Contents</h1>" + NL
					+ "<p style=\"text-indent:0pt\">" + NL);
			for (String chapterFileName : chapterFileNames) {
				out.print("<a href=\"" + chapterFileName + "\">" + chapterFileName.replace(".html", "") + "</a><br/>"
						+ NL);
			}
			out.print("</p>" + NL + "</body>" + NL + "</html>" + NL);
		}
	}

	public static void retrieveChapterLinks(String url)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		Document doc = Jsoup.connect(url).get();
		Elements links = doc.select("a[href]");
		String domain = url.substring(0, ordinalIndexOf(url, "/", 3));
		String currChapterLink = null;
		for (Element chapterLink : links) {
			if (chapterLink.attr("href").startsWith("/")) {
				currChapterLink = (domain + chapterLink.attr("href"));
			} else {
				currChapterLink = chapterLink.attr("href");
			}
			if (currChapterLink.startsWith("http") && !chapterLink.text().isEmpty()) {
				chapterUrl.add(currChapterLink);
				NovelGrabberGUI.listModelChapterLinks.addElement(chapterLink.text());
			}

		}
		System.out.println(chapterUrl);
	}

	public static void manSaveChapters(String saveLocation, String fileType, boolean chapterNumeration,
			String chapterContainer, String sentenceSelecter, boolean invertOrder)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		String fileName = null;
		int chapterNumber = 0;
		NovelGrabberGUI.setMaxProgress("manual", chapterUrl.size());
		if (invertOrder == true) {
			Collections.reverse(chapterUrl);
		}
		for (String chapter : chapterUrl) {
			chapterNumber++;
			Document doc = Jsoup.connect(chapter).get();
			if (chapterNumeration == false) {
				if (invertOrder == true) {
					fileName = NovelGrabberGUI.listModelChapterLinks
							.get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber).toString()
							.replaceAll("[^\\w]+", "-") + fileType;
				} else {
					fileName = NovelGrabberGUI.listModelChapterLinks.get(chapterNumber - 1).toString()
							.replaceAll("[^\\w]+", "-") + fileType;
				}

			} else {
				if (invertOrder == true) {
					fileName = "Ch-" + chapterNumber + "-"
							+ NovelGrabberGUI.listModelChapterLinks
									.get(NovelGrabberGUI.listModelChapterLinks.getSize() - chapterNumber).toString()
									.replaceAll("[^\\w]+", "-")
							+ fileType;
				} else {
					fileName = "Ch-" + chapterNumber + "-" + NovelGrabberGUI.listModelChapterLinks
							.get(chapterNumber - 1).toString().replaceAll("[^\\w]+", "-") + fileType;
				}

			}
			Element content = doc.select(chapterContainer).first();
			Elements p = content.select(sentenceSelecter);
			File dir = new File(saveLocation);
			if (!dir.exists())
				dir.mkdirs();
			if (fileType == ".txt") {
				try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
					for (Element x : p) {
						out.println(x.text() + NL);
					}
				}
			} else {
				try (PrintStream out = new PrintStream(saveLocation + File.separator + fileName)) {
					out.print("<!DOCTYPE html>" + NL + "<html lang=\"en\">" + NL + "<head>" + NL
							+ "<meta charset=\"UTF-8\" />" + NL + "</head>" + NL + "<body>" + NL);
					for (Element x : p) {
						out.println("<p>" + x.text() + "</p>" + NL);
					}
					out.print("</body>" + NL + "</html>");
				}
			}
			chapterFileNames.add(fileName);
			NovelGrabberGUI.updateProgress("manual", 1);
		}
		if (invertOrder == true) {
			Collections.reverse(chapterUrl);
		}
	}

	public static int ordinalIndexOf(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}
}
