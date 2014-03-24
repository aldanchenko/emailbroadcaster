package ua.promotion;

import org.apache.log4j.Logger;
import sun.misc.IOUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * TODO: under construction.
 */
public class EmailSender {

    private static final Logger logger = Logger.getLogger(EmailSender.class);

    private Properties emailProperties;

    //       private String ENCODE="koi8-r";
    private String ENCODE="Cp1251"; //windows-1251

    public void sendEmailWithAttachment(String emailRecipient, List<String> attachments,
                                        String emailSubject, String emailText) {
        Session session = Session.getInstance(emailProperties, null);
        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            InternetAddress recipients[] = new InternetAddress[]{new InternetAddress(emailRecipient)};

            String fromMailUser = (String) emailProperties.get("mail.user");

            mimeMessage.setFrom(new InternetAddress(fromMailUser)); //FIXME

            mimeMessage.setRecipients(Message.RecipientType.TO, recipients);
            mimeMessage.setSubject(emailSubject, ENCODE);
            mimeMessage.setText(emailText, ENCODE);

            mimeMessage.setHeader("Content-Type", "text/plain;charset=" + ENCODE);
            mimeMessage.setHeader("Content-Transfer-Encoding", "base64");

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(emailText, ENCODE);

            // create the Multipart and add its parts to it
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textBodyPart);

            // attach the file to the message
            for (String attachment : attachments) {// create the second message part
                MimeBodyPart fileBodyPart = new MimeBodyPart();

                FileDataSource fileDataSource = new FileDataSource(attachment);
                fileBodyPart.setDataHandler(new DataHandler(fileDataSource));
                fileBodyPart.setHeader("Content-Type", "");
//                fileBodyPart.setFileName(fileDataSource.getName());
                fileBodyPart.setFileName(MimeUtility.decodeText(fileDataSource.getName()));

                multipart.addBodyPart(fileBodyPart);
            }

            mimeMessage.setContent(multipart);

            Transport transport = session.getTransport("smtp");

            String hostMail = (String) emailProperties.get("mail.host");
            String fromMailPassword = (String) emailProperties.get("mail.password");

            transport.connect(hostMail, fromMailUser, fromMailPassword);

            mimeMessage.saveChanges();

            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

            transport.close();
        } catch (AddressException ex) {
            ex.printStackTrace();

            logger.error(ex.getMessage());
        } catch (MessagingException ex) {
            ex.printStackTrace();
            System.out.print("" + ex.hashCode());

            logger.error(ex.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(String emailRecipient, String emailSubject, String emailText){
        Session session = Session.getInstance(emailProperties, null);
        session.setDebug(true);

        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            InternetAddress recipients[] = new InternetAddress[]{new InternetAddress(emailRecipient)};

            String fromMailUser = (String) emailProperties.get("mail.user");

            mimeMessage.setFrom(new InternetAddress(fromMailUser)); //FIXME

            mimeMessage.setRecipients(Message.RecipientType.TO, recipients);
            mimeMessage.setSubject(emailSubject, ENCODE);
            mimeMessage.setText(emailText, ENCODE);

            mimeMessage.setHeader("Content-Type", "text/plain;charset=" + ENCODE);
            mimeMessage.setHeader("Content-Transfer-Encoding", "base64");

            Transport transport = session.getTransport("smtp");

            String hostMail = (String) emailProperties.get("mail.host");
            String fromMailPassword = (String) emailProperties.get("mail.password");

            transport.connect(hostMail, fromMailUser, fromMailPassword);

            mimeMessage.saveChanges();

            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

            transport.close();
        } catch (AddressException ex) {
            ex.printStackTrace();

            logger.error(ex.getMessage());
        } catch (MessagingException ex) {
            ex.printStackTrace();
            System.out.print("" + ex.hashCode());

            logger.error(ex.getMessage());
        }
    }

    public EmailSender(Properties properties) {
        this.emailProperties = properties;
    }

    public static void main(String[] args) {
//        EmailSender emailSender = new EmailSender("dev.danchenko@gmail.com", "aldanchenko@gmail.com");

//        List<Recipient> merchandisers = emailSender.loadMerchandisers("merchandisers.xml");

//        emailSender.sendEmail("Тестируем почту");
    }
}
