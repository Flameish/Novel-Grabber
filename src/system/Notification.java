package system;

import grabber.Novel;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import system.persistent.EmailConfig;

import javax.activation.FileDataSource;
import java.io.File;

public class Notification {

    public static void sendNotification(Novel novel) {
        StringBuilder links = new StringBuilder();
        for (int i = novel.options.firstChapter-1; i < novel.options.lastChapter; i++) {
            links.append(novel.chapters.get(i).chapterURL).append("\n");
        }
        Email email = EmailBuilder.startingBlank()
                .to(EmailConfig.getReceiverEmail())
                .from(EmailConfig.getReceiverEmail())
                .withSubject(novel.metadata.bookTitle + " - New Chapter")
                .withPlainText(links.toString())
                .buildEmail();

        Mailer mailer;
        switch (EmailConfig.getSSL()) {
            case 0:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTP)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
            case 1:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTPS)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
            case 2:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTP_TLS)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
            default:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTP)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
        }
        mailer.sendMail(email);
    }
    public static void sendAttachment(Novel novel) {
        File epub = new File(novel.options.saveLocation+"/"+novel.epubFilename);
        Email email = EmailBuilder.startingBlank()
                .to(EmailConfig.getReceiverEmail())
                .from(EmailConfig.getReceiverEmail())
                .withSubject(novel.metadata.bookTitle+" - New Chapter")
                .withPlainText("")
                .withAttachment(epub.getName(), new FileDataSource(epub))
                .buildEmail();

        Mailer mailer;
        switch (EmailConfig.getSSL()) {
            case 0:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTP)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
            case 1:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTPS)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
            case 2:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTP_TLS)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
            default:
                mailer = MailerBuilder
                        .withSMTPServer(EmailConfig.getHost(), EmailConfig.getPort(), EmailConfig.getUsername(), EmailConfig.getPassword())
                        .withTransportStrategy(TransportStrategy.SMTP_TLS)
                        .withSessionTimeout(10 * 1000)
                        .buildMailer();
                break;
        }
        mailer.sendMail(email);
    }
}
