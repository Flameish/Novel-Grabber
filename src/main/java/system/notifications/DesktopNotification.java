package system.notifications;

import dorkbox.notify.Notify;
import dorkbox.util.ImageUtil;
import grabber.GrabberUtils;
import grabber.Novel;
import system.data.library.LibraryNovel;
import system.data.library.LibrarySettings;

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
            File imageFile = new File(LibrarySettings.libraryFolder + "/"
                    + libNovel.getTitle() + "/" + libNovel.getCover());
            InputStream resourceAsStream = new FileInputStream(imageFile);
            Image image = ImageUtil.getImageImmediate(ImageIO.read(resourceAsStream));
            image = image.getScaledInstance(100, 133, Image.SCALE_SMOOTH);

            Notify.create()
                    .title(libNovel.getTitle())
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
