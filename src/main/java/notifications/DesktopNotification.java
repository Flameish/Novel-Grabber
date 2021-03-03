package notifications;

import dorkbox.notify.Notify;
import dorkbox.util.ImageUtil;
import grabber.GrabberUtils;
import grabber.Novel;
import library.Library;
import library.LibraryNovel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopNotification {

    public static void sendChapterReleaseNotification(LibraryNovel libNovel, Novel autoNovel) {
        try {
            URI uri = new URI(autoNovel.chapterList.get(libNovel.getLastLocalChapterNumber()).chapterURL);
            File imageFile = new File(Library.libraryFolder + "/"
                    + libNovel.getMetadata().getTitle() + "/"
                    + libNovel.getMetadata().getCoverName() + "."
                    + libNovel.getMetadata().getCoverFormat());
            InputStream resourceAsStream = new FileInputStream(imageFile);
            Image image = ImageUtil.getImageImmediate(ImageIO.read(resourceAsStream));
            image = image.getScaledInstance(100, 133, Image.SCALE_SMOOTH);

            Notify.create()
                    .title(libNovel.getMetadata().getTitle())
                    .text(autoNovel.chapterList.get(libNovel.getLastLocalChapterNumber()).name)
                    .darkStyle()
                    .image(image)
                    .hideAfter(5000)
                    .hideCloseButton()
                    .onAction(arg0 -> GrabberUtils.openWebpage(uri))
                    .show();

        } catch (IOException | URISyntaxException e) {
            GrabberUtils.err(e.getMessage(), e);
        }
    }
}
