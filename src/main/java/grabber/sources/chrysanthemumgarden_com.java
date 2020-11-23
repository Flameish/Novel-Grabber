package grabber.sources;

import grabber.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import system.init;

public class chrysanthemumgarden_com implements Source {
    private final Novel novel;
    private Document toc;
    private final HashMap<String, String> charMap;

    public chrysanthemumgarden_com(Novel novel) {
        this.novel = novel;
        charMap = new HashMap<>();
        charMap.put("a","t");
        charMap.put("b","o");
        charMap.put("c","n");
        charMap.put("d","q");
        charMap.put("e","u");
        charMap.put("f","e");
        charMap.put("g","r");
        charMap.put("h","z");
        charMap.put("i","l");
        charMap.put("j","a");
        charMap.put("k","w");
        charMap.put("l","i");
        charMap.put("m","c");
        charMap.put("n","v");
        charMap.put("o","f");
        charMap.put("p","j");
        charMap.put("q","p");
        charMap.put("r","s");
        charMap.put("s","y");
        charMap.put("t","h");
        charMap.put("u","g");
        charMap.put("v","d");
        charMap.put("w","m");
        charMap.put("x","k");
        charMap.put("y","b");
        charMap.put("z","x");
        charMap.put("A","J");
        charMap.put("B","K");
        charMap.put("C","A");
        charMap.put("D","B");
        charMap.put("E","R");
        charMap.put("F","U");
        charMap.put("G","D");
        charMap.put("H","Q");
        charMap.put("I","Z");
        charMap.put("J","C");
        charMap.put("K","T");
        charMap.put("L","H");
        charMap.put("M","F");
        charMap.put("N","V");
        charMap.put("O","L");
        charMap.put("P","I");
        charMap.put("Q","W");
        charMap.put("R","N");
        charMap.put("S","E");
        charMap.put("T","Y");
        charMap.put("U","P");
        charMap.put("V","S");
        charMap.put("W","X");
        charMap.put("X","G");
        charMap.put("Y","O");
        charMap.put("Z","M");
    }

    public List<Chapter> getChapterList() {
        List<Chapter> chapterList = new ArrayList();
        try {
            toc = Jsoup.connect(novel.novelLink).get();
            Elements chapterLinks = toc.select(".translated-chapters a");
            for(Element chapterLink: chapterLinks) {
                chapterList.add(new Chapter(chapterLink.text(), chapterLink.attr("abs:href")));
            }
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        }
        return chapterList;
    }

    public Element getChapterContent(Chapter chapter) {
        Element chapterBody = null;
        try {
            Document doc = Jsoup.connect(chapter.chapterURL).get();

            Elements encodedStrings = doc.select(".jum");

            for(Element string: encodedStrings) {
                string.text(decrypt(string.text()));
            }

            chapterBody = doc.select("#novel-content").first();
        } catch (HttpStatusException httpEr) {
            String errorMsg;
            int errorCode = httpEr.getStatusCode();
            switch(errorCode) {
                case 403:
                    errorMsg = "[ERROR] Forbidden! (403)";
                    break;
                case 404:
                    errorMsg = "[ERROR] Page not found! (404)";
                    break;
                case 500:
                    errorMsg = "[ERROR] Server error! (500)";
                    break;
                case 503:
                    errorMsg = "[ERROR] Service Unavailable! (503)";
                    break;
                case 504:
                    errorMsg = "[ERROR] Gateway Timeout! (504)";
                    break;
                default:
                    errorMsg = "[ERROR] Could not connect to webpage!";
            }
            System.err.println(errorMsg);
            if (init.gui != null) {
                init.gui.appendText(novel.window, errorMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (init.gui != null) {
                init.gui.appendText(novel.window, "[ERROR] Could not connect to webpage!");
            }
        }
        return chapterBody;
    }

    public String decrypt(String encryptedString) {
        StringBuilder decryptedString = new StringBuilder();
        for(int i = 0; i < encryptedString.length(); i++) {
            char toReplace = encryptedString.charAt(i);
            String replaced = charMap.get(String.valueOf(toReplace));
            if(replaced == null) replaced = String.valueOf(toReplace);
            decryptedString.append(replaced);
        }
        return decryptedString.toString();
    }

    public NovelMetadata getMetadata() {
        NovelMetadata metadata = new NovelMetadata();

        if(toc != null) {
            metadata.setTitle(toc.select("h1").first().text());
            metadata.setBufferedCover(toc.select("img.materialboxed").attr("abs:src"));

            Elements tags = toc.select(".novel-container a[href^=https://chrysanthemumgarden.com/genre/]");
            List<String> subjects = new ArrayList<>();
            for(Element tag: tags) {
                subjects.add(tag.text());
            }
            metadata.setSubjects(subjects);
        }

        return metadata;
    }

    public List<String> getBlacklistedTags() {
        List blacklistedTags = new ArrayList();
        blacklistedTags.add("p[style=height:1px;width:0;overflow:hidden;display:inline-block]");
        blacklistedTags.add(".netlink");
        blacklistedTags.add(".chrys-ads");
        blacklistedTags.add(".google");
        blacklistedTags.add("h3[style=color:transparent;height:1px;margin:0;padding:0;overflow:hidden]");
        blacklistedTags.add("p[style=height:1px;margin:0;padding:0;overflow:hidden]");
        blacklistedTags.add("span[style=height:1px;width:0;overflow:hidden;display:inline-block]");
        blacklistedTags.add(".sharedaddy");
        return blacklistedTags;
    }

    public Map<String, String> getLoginCookies() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
