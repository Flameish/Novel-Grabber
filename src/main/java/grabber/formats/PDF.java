package grabber.formats;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import system.Config;

import java.io.*;

public class PDF {
    private Novel novel;
    private NovelMetadata novelMetadata;
    static final String NL = System.getProperty("line.separator");
    static final String htmlHead = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + NL+
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"" + NL +
            "  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" + NL +
            "\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + NL +
            "<head>" + NL +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" + NL +
            "<title></title>" + NL +
            "<style>" +
            "* {" +
            "font-family: 'cjk', sans-serif;" +
            "}" +
            "body {" +
            "font-size:1em;" +
            "line-height:1.25;" +
            "}" +
            "p {" +
            "text-indent:2em;" +
            "margin:0;" +
            "text-align: justify;" +
            "}" +
            "</style>" + NL +
            "</head>" + NL +
            "<body>" + NL;
    static final String htmlFoot = "</body>" + NL + "</html>";

    public PDF(Novel novel) {
        this.novel = novel;
        this.novelMetadata = novel.metadata;
    }

    public void write() {
        String filename = setFilename();
        try (OutputStream os = new FileOutputStream(novel.saveLocation + "/" + filename)) {
            GrabberUtils.info(novel.window,"Writing PDF...");
            PdfRendererBuilder builder = new PdfRendererBuilder();
            File cjkFont = new File(GrabberUtils.getCurrentPath() + "/fonts/NotoSansCJKtc-Regular.ttf");
            if (cjkFont.exists()) {
                builder.useFont(cjkFont, "cjk");
            }
            builder.useFastMode();
            builder.withW3cDocument(buildHtmlFile(), novel.saveLocation);
            builder.toStream(os);
            builder.run();
            GrabberUtils.info(novel.window, "Output: " + novel.saveLocation+"/"+ filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not write PDF. "+e.getMessage(), e);
        }
    }

    private String setFilename() {
        String epubFilename = "Unknown.pdf";
        switch (Config.getInstance().getFilenameFormat()) {
            case 0:
                epubFilename = novelMetadata.getAuthor() + " - " + novelMetadata.getTitle() + ".pdf";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
            case 1:
                epubFilename = novelMetadata.getTitle() + " - " + novelMetadata.getAuthor() + ".pdf";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
            case 2:
                epubFilename = novelMetadata.getTitle() + ".pdf";
                if(novel.window.equals("checker")) epubFilename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+epubFilename.replaceAll(" ","-");
                break;
        }
        return epubFilename.replaceAll("[\\\\/:*?\"<>|]", "");
    }

    private String buildToc() {
        StringBuilder toc = new StringBuilder();
        toc.append("<bookmarks>\n");
        for (Chapter chapter: novel.successfulChapters) {
            toc.append("<bookmark name=\"" + chapter.name + "\" href=\"#" + chapter.fileName + "\" />\n");
        }
        toc.append("</bookmarks>\n");

        return toc.toString();
    }

    private Document buildHtmlFile() {
        StringBuilder html = new StringBuilder();
        html.append(htmlHead);
        html.append(buildToc());
        for (Chapter chapter: novel.successfulChapters) {
            html.append("<div id=\"" + chapter.fileName + "\" style=\"page-break-after:always;\">" + chapter.chapterContent + "</div>\n");
        }
        html.append(htmlFoot);
        org.jsoup.nodes.Document doc = Jsoup.parse(html.toString());
        return new W3CDom().fromJsoup(doc);
    }
}
