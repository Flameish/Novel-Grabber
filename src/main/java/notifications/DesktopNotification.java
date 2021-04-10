package notifications;

import dorkbox.notify.Notify;
import grabber.GrabberUtils;
import grabber.Novel;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopNotification {

    public static void sendChapterReleaseNotification(Novel novel) {
        try {
            URI uri = new URI(novel.chapterList.get(novel.chapterList.size()-1).chapterURL);
            Notify.create()
                    .title(novel.metadata.getTitle())
                    .text(novel.chapterList.get(novel.chapterList.size()-1).name)
                    .darkStyle()
                    .image(novel.metadata.getBufferedCover())
                    .hideAfter(5000)
                    .hideCloseButton()
                    .onAction(arg0 -> GrabberUtils.openWebpage(uri))
                    .show();
        } catch (URISyntaxException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }
    public static void sendDownloadFinishedNotification(Novel novel) {
        Image image = novel.metadata.getBufferedCover();
        Notify.create()
                .title(novel.metadata.getTitle())
                .text("Download finished!")
                .darkStyle()
                .image(image)
                .hideAfter(5000)
                .hideCloseButton()
                .show();
    }
}
