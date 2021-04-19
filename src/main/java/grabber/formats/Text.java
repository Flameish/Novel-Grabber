package grabber.formats;

import grabber.Chapter;
import grabber.GrabberUtils;
import grabber.Novel;
import grabber.NovelMetadata;
import org.jsoup.Jsoup;
import system.Config;
import system.init;

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
        GrabberUtils.info(novel.window,"Writing TXT...");
        if (init.config.isSeparateChapters()) {
            // Create dir for chapter files
            String cleanFolderName = filename
                    .replace("^\\.+", "")
                    .replaceAll("[\\\\/:*?\"<>|]", "");
            if (cleanFolderName.length() > 240) cleanFolderName = cleanFolderName.substring(0,240);
            String saveLocation = novel.saveLocation + "/" + cleanFolderName;
            GrabberUtils.createDir(saveLocation);

            for(Chapter chapter: novel.successfulChapters) {
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(saveLocation+ "/" + chapter.fileName + ".txt"), StandardCharsets.UTF_8))) {
                    writer.write(Jsoup.parse(chapter.chapterContent).wholeText());
                } catch (UnsupportedEncodingException | FileNotFoundException e) {
                    GrabberUtils.err(novel.window, e.getMessage(), e);
                } catch (IOException e) {
                    GrabberUtils.err(novel.window, "Could not write file. "+e.getMessage(), e);
                }
            }
            GrabberUtils.info("Output: " + saveLocation);
        } else {
            filename += ".txt";
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(novel.saveLocation + "/" + filename), StandardCharsets.UTF_8))) {
                for(Chapter chapter: novel.successfulChapters) {
                    // Preserve line breaks
                    writer.write(Jsoup.parse(chapter.chapterContent).wholeText());
                }
                novel.filename = filename;
                GrabberUtils.info(novel.window, "Output: " + novel.saveLocation + "/" + filename);
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                GrabberUtils.err(novel.window, e.getMessage(), e);
            } catch (IOException e) {
                GrabberUtils.err(novel.window, "Could not write file. "+e.getMessage(), e);
            }
        }
    }

    private String setFilename() {
        String filename = "Unknown";
        switch (Config.getInstance().getFilenameFormat()) {
            case 0:
                filename = novelMetadata.getAuthor() + " - " + novelMetadata.getTitle();
                if(novel.window.equals("checker")) filename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+filename.replaceAll(" ","-");
                break;
            case 1:
                filename = novelMetadata.getTitle() + " - " + novelMetadata.getAuthor();
                if(novel.window.equals("checker")) filename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+filename.replaceAll(" ","-");
                break;
            case 2:
                filename = novelMetadata.getTitle();
                if(novel.window.equals("checker")) filename =
                        novel.firstChapter + "-"+ novel.lastChapter+"-"+filename.replaceAll(" ","-");
                break;
        }
        return filename.replaceAll("[\\\\/:*?\"<>|]", "");
    }

}
