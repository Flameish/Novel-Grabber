package grabber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CLI {
    /**
     * Downloads a novel fully automatic based on CLI input.
     * @param params
     */
    public static void downloadNovel(Map<String, List<String>> params) {
        Novel autoNovel = new NovelBuilder().fromCLI(params).build();
        autoNovel.fetchChapterList();
        autoNovel.getMetadata();
        // Change bookTitle temporarily to include chapter names
        // when creating the EPUB for system.library auto grabs
        String oldBookTitle = autoNovel.bookTitle;
        if(autoNovel.window.equals("checker")) {
            autoNovel.bookTitle = autoNovel.firstChapter
                    +"-"+ autoNovel.lastChapter
                    +"-"+ autoNovel.bookTitle;
        }

        // Chapter range needs to be set AFTER fetching the chapter list
        if(params.containsKey("chapters")) {
            if(params.get("chapters").get(0).equals("all")) {
                autoNovel.firstChapter = 1;
                autoNovel.lastChapter = autoNovel.chapterList.size();
            } else {
                autoNovel.firstChapter = Integer.parseInt(params.get("chapters").get(0));
                if(params.get("chapters").get(1).equals("last")) {
                    autoNovel.lastChapter = autoNovel.chapterList.size();
                } else {
                    autoNovel.lastChapter = Integer.parseInt(params.get("chapters").get(1));
                }
            }
        } else {
            autoNovel.firstChapter = 1;
            autoNovel.lastChapter = autoNovel.chapterList.size();
        }

        try {
            autoNovel.downloadChapters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        autoNovel.writeEpub();
        // Change book title back to show up in emails correctly
        if(autoNovel.window.equals("checker")) {
            autoNovel.bookTitle = oldBookTitle;
        }
        autoNovel.report();
    }

    public static Map<String, List<String>> createParamsFromArgs(String[] args) {
        final Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;
        for (final String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return null;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return null;
            }
        }
        return params;
    }

    public static String[] createArgsFromString(String cliString) {
        List<String> args = new ArrayList<>();
        for (String s : cliString.split(" (?=(([^'\"]*['\"]){2})*[^'\"]*$)")) {
            if(s.startsWith("\"")) s = s.substring(1, s.lastIndexOf("\""));
            args.add(s);
        }
        return args.toArray(new String[0]);
    }
}
