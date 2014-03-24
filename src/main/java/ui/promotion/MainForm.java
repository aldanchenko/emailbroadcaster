package ui.promotion;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

/**
 * Main form.
 *
 * @author winter
 */
@Deprecated
public class MainForm {
    private JTextArea textArea1;
    private JPanel panel1;
    private JList list1;
    private JButton button1;
    private JButton button2;

    public MainForm() {
        button1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Configuration configuration = new Configuration();

                try {
                    configuration.setDirectoryForTemplateLoading(
                            new File("/home/winter/Projects/java/emailbroadcaster")
                    );
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Template template = null;

                try {
                    template = configuration
                            .getTemplate("freemarker_template.ftl");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                Map<String, Object> data = new HashMap<String, Object>();

                data.put("message", "Hello Alex");

                try {
                    Writer out = new OutputStreamWriter(System.out);
                    template.process(data, out);
                    out.flush();

                    // File output
                    Writer file = new FileWriter(
                            new File("/home/winter/Projects/java/emailbroadcaster/result_email_text.txt"));
                    template.process(data, file);
                    file.flush();
                    file.close();

                } catch (TemplateException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    sendEmail();
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void sendEmail() throws MessagingException {

        List<String> toUserEmailList = new ArrayList<String>();

        String subject = "Тестируем";

        String messageText = null;

        try {
            messageText = readFile("/home/winter/Projects/java/emailbroadcaster/result_email_text.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Properties emailProperties = getEmailProperties();

        String fromUser = "dev.danchenko@gmail.com";
        String fromUserPassword = "testnotifications";
        String toUser = "aldanchenko@gmail.com";

        Session session = createSession(fromUser, fromUserPassword);

        MimeMessage mimeMessage = buildMimeMessage(session, fromUser, toUser, subject, messageText);

//        mimeMessage.setHeader("Content-Type", "text/plain;charset=" + "Cp1251");
//        mimeMessage.setHeader("Content-Transfer-Encoding", "base64");

//        mimeMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse("miron.alikc@gmail.com"));

        sendEmailToGmailTestUser(session, mimeMessage, fromUser, fromUserPassword);
    }

    public void sendEmailToGmailTestUser(Session session,
                                         MimeMessage mimeMessage,
                                         String fromUser,
                                         String fromUserPassword)
            throws MessagingException {

        String smtpHost = "smtp.gmail.com";
        String transportType = "smtp";

        Transport transport = session.getTransport(transportType);

        transport.connect(smtpHost, fromUser, fromUserPassword);

        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

        transport.close();
    }

    public MimeMessage buildMimeMessage(Session session,
                                        String fromUser,
                                        String toUser,
                                        String mailSubject,
                                        String mailHtmlText) throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.setFrom(new InternetAddress(fromUser));
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toUser));

        mimeMessage.setSubject(mailSubject);

        MimeBodyPart mimeBodyPart1 = new MimeBodyPart();
        mimeBodyPart1.setContent(mailHtmlText, "text/html");

        MimeBodyPart mimeBodyPart2 = new MimeBodyPart();
        mimeBodyPart2.setContent(mailHtmlText, "text/html");

        MimeBodyPart mimeBodyPart3 = new MimeBodyPart();
        mimeBodyPart3.setContent(mailHtmlText, "text/html");

        Multipart multipart = new MimeMultipart();

        multipart.addBodyPart(mimeBodyPart1);
        multipart.addBodyPart(mimeBodyPart2);
        multipart.addBodyPart(mimeBodyPart3);

        mimeMessage.setContent(multipart);

        //mimeMessage.setContent(mailHtmlText, "text/html");

        return mimeMessage;
    }

    private Session createSession(String fromUser, String fromUserPassword) {
        Properties emailProperties = System.getProperties();

        emailProperties.setProperty("mail.smtp.starttls.enable", "true");
        emailProperties.setProperty("mail.mime.charset", "Cp1251");
        emailProperties.setProperty("mail.transport.protocol", "smtp");
        emailProperties.setProperty("mail.smtp.auth", "true");
        emailProperties.setProperty("mail.smtp.port", "" + 587);
        emailProperties.setProperty("mail.host", "smtp.gmail.com");
        emailProperties.setProperty("mail.user", fromUser);
        emailProperties.setProperty("mail.password", fromUserPassword);

        return Session.getDefaultInstance(emailProperties, null);
    }

    private String readFile(String fileName) throws IOException {
        String str = "";

        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            str = sb.toString();
        } finally {
            br.close();
        }

        return str;
    }

    private Properties getEmailProperties() {
        return null;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");

        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
