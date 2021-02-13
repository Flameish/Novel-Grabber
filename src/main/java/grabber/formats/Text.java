package grabber.formats;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Jsoup;
import system.data.Settings;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Text {
    private Novel novel;
    private final NovelMetadata novelMetadata;

    public Text(Novel novel) {
        this.novel = novel;
        this.novelMetadata = novel.metadata;
    }

    public void write() {
        String filename = setFilename();
        GrabberUtils.createDir(novel.saveLocation);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(novel.saveLocation + "/" + filename), StandardCharsets.UTF_8))) {
            for(Chapter chapter: novel.successfulChapters) {
                // Preserve line breaks
                writer.write(Jsoup.parse(chapter.chapterContent).wholeText());
            }

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            GrabberUtils.err(novel.window, e.getMessage(), e);
        } catch (IOException e) {
            GrabberUtils.err(novel.window, "Could not write file. "+e.getMessage(), e);
        }
        novel.epubFilename = filename;
        GrabberUtils.info("Output: " + novel.saveLocation+"/"+ filename);
    }

    private String setFilename() {
        String filename = "Unknown.txt";
        switch (Settings.getInstance().getFilenameFormat()) {
            case 0:
                filename = novelMetadata.getAuthor() + " - " + novelMetadata.getTitle() + ".txt";
                if(novel.window.equals("checker")) filename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+filename.replaceAll(" ","-");
                break;
            case 1:
                filename = novelMetadata.getTitle() + " - " + novelMetadata.getAuthor() + ".txt";
                if(novel.window.equals("checker")) filename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+filename.replaceAll(" ","-");
                break;
            case 2:
                filename = novelMetadata.getTitle() + ".txt";
                if(novel.window.equals("checker")) filename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+filename.replaceAll(" ","-");
                break;
        }
        return filename.replaceAll("[\\\\/:*?\"<>|]", "");
    }

}
