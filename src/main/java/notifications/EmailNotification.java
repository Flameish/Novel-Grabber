package notifications;

import grabber.GrabberUtils;
import grabber.Novel;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import system.Config;
import library.Library;
import library.LibraryNovel;

import javax.activation.FileDataSource;
import java.io.File;

public class EmailNotification {
    private final Mailer mailer;
    private final Config config;

    /**
     * Creates Mailer from email config.
     * @throws Exception if connection test to SMTP server fails.
     */
    public EmailNotification() throws IllegalArgumentException {
        this.config = Config.getInstance();
        if(config.getHost().isEmpty()) throw new IllegalArgumentException("SMTP server host missing");
        TransportStrategy SSL = TransportStrategy.SMTP;
        switch(config.getSsl()) {
            case "SMTP":
                SSL = TransportStrategy.SMTP;
                break;
            case "SMTPS":
                SSL = TransportStrategy.SMTPS;
                break;
            case "SMTP_TLS":
                SSL = TransportStrategy.SMTP_TLS;
                break;
        }
        mailer = MailerBuilder
                .withSMTPServer(config.getHost(), config.getPort(), config.getUsername(), config.getPassword())
                .withTransportStrategy(SSL)
                .withSessionTimeout(100 * 1000)
                .buildMailer();
        mailer.testConnection(); // Throws Exception if connection fails
    }

    /**
     * Sends Email with chapter names & links.
     */
    public void sendNotification(Novel novel) {
        LibraryNovel libNovel = Library.getInstance().getNovel(novel.novelLink);
        StringBuilder links = new StringBuilder();

        for (int i = libNovel.getLastLocalChapterNumber(); i < libNovel.getNewestChapterNumber(); i++) {
            links.append("<a href=\""+novel.chapterList.get(i).chapterURL+"\">"+novel.chapterList.get(i).name+"</a><br>");
        }
        Email email = EmailBuilder.startingBlank()
                .to(config.getInstance().getReceiverEmail())
                .from(config.getInstance().getReceiverEmail())
                .withSubject("[Novel-Grabber]"+novel.metadata.getTitle() + " - Update")
                .withHTMLText(links.toString())
                .buildEmail();

        mailer.sendMail(email);
        GrabberUtils.info("[EMAIL]Email send.");
    }

    /**
     * Sends an Email with chapter names/links and created EPUB of novel as attachment.
     */
    public void sendAttachment(Novel novel) {
        StringBuilder links = new StringBuilder();
        for (int i = novel.firstChapter-1; i < novel.lastChapter; i++) {
            links.append("<a href=\""+novel.chapterList.get(i).chapterURL+"\">"+novel.chapterList.get(i).name+"</a><br>");
        }
        File epub = new File(novel.saveLocation+"/"+novel.filename);
        Email email = EmailBuilder.startingBlank()
                .to(config.getInstance().getReceiverEmail())
                .from(config.getInstance().getReceiverEmail())
                .withSubject("[Novel-Grabber]"+novel.metadata.getTitle() +" - Update")
                .withHTMLText(links.toString())
                .withAttachment(epub.getName(), new FileDataSource(epub))
                .buildEmail();

        mailer.sendMail(email);
        GrabberUtils.info("Email send with attachment.");
    }
}
