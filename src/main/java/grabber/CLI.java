package grabber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLI {

    /**
     * Downloads a novel fully automatic based on CLI input.
     */
    public static Novel downloadNovel(Map<String, List<String>> params) throws ClassNotFoundException, IOException, InterruptedException {
        Novel novel = new NovelBuilder().fromCLI(params).build();
        novel.check();
        NovelMetadata metadata = novel.metadata;


        // Chapter range needs to be set after fetching the chapter list
        if(params.containsKey("chapters")) {
            if(params.get("chapters").get(0).equals("all")) {
                novel.firstChapter = 1;
                novel.lastChapter = novel.chapterList.size();
            } else {
                novel.firstChapter = Integer.parseInt(params.get("chapters").get(0));
                if(params.get("chapters").get(1).equals("last")) {
                    novel.lastChapter = novel.chapterList.size();
                } else {
                    novel.lastChapter = Integer.parseInt(params.get("chapters").get(1));
                }
            }
        } else {
            novel.firstChapter = 1;
            novel.lastChapter = novel.chapterList.size();
        }

        novel.downloadChapters();

        // Change bookTitle temporarily to include chapter names
        // when creating the EPUB for library auto grabs
        String oldBookTitle = metadata.getTitle();
        if(novel.window.equals("checker")) {
            metadata.setTitle(novel.firstChapter +"-"+ novel.lastChapter +"-"+ metadata.getTitle());
        }
        novel.output();
        // Change book title back to show up in emails correctly
        if(novel.window.equals("checker")) {
            metadata.setTitle(oldBookTitle);
        }
        return novel;
    }

    public static Map<String, List<String>> createParamsFromArgs(String[] args) {
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (final String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    GrabberUtils.err("Error at argument " + a);
                    return null;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                GrabberUtils.err("Illegal parameter usage");
                return null;
            }
        }
        return params;
    }

    public static String[] createArgsFromString(String cliString) {
        List<String> args = new ArrayList<>();
        for (String s : cliString.split(" (?=(([^\"]*[\"]){2})*[^\"]*$)")) {
            if(s.startsWith("\"")) s = s.substring(1, s.lastIndexOf("\""));
            args.add(s);
        }
        return args.toArray(new String[0]);
    }
}
