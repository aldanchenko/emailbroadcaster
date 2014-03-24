package ua.promotion.forms;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import ua.promotion.BroadcasterFileReader;
import ua.promotion.EmailSender;
import ua.promotion.bean.Recipient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Simple MainForm.
 */
public class MainForm implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(MainForm.class);

    private JTextArea templateTextArea;
    private JPanel panel1;
    private JButton sendButton;
    private JTextField subjectTextField;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea consoleTextArea;

    public MainForm() {
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setIndeterminate(true);
                progressBar.setMinimum(0);

                SendEmailTask task = new SendEmailTask();
                task.addPropertyChangeListener(MainForm.this);
                task.execute();

//                logger.info("Отправляю...");
//
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        indeterminateProgressBar.setVisible(true);
//
//                        statusLabel.setText("Отправляю...");
//                    }
//                });
//
//                final StringBuilder errorsStringBuilder = new StringBuilder();
//
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<Recipient> merchandisers = null;
//                        Properties emailProperties = null;
//
//                        try {
//                            merchandisers = BroadcasterFileReader.loadMerchandisers("merchandisers.xml");
//
//                            emailProperties = BroadcasterFileReader.loadEmailProperties("email.properties");
//                        } catch (IOException e1) {
//                            errorsStringBuilder.append(e1.getMessage()).append(".");
//
//                            e1.printStackTrace();
//
//                            logger.info("Ошибка: " + e1.getMessage());
//                        } catch (SAXException e1) {
//                            errorsStringBuilder.append(e1.getMessage()).append(".");
//
//                            e1.printStackTrace();
//
//                            logger.info("Ошибка: " + e1.getMessage());
//                        }
//
//                        if (merchandisers == null) {
//                            SwingUtilities.invokeLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    statusLabel.setText("Проблемы при чтении файла merchandisers.xml.");
//                                }
//                            });
//
//                            logger.info("Проблемы при чтении файла merchandisers.xml.");
//
//                            throw new RuntimeException("merchandisers list is null!");
//                        }
//
//                        EmailSender emailSender = new EmailSender(emailProperties);
//
//                        progressBar.setMinimum(0);
//                        progressBar.setMaximum(merchandisers.size());
//
//                        int progress = 0;
//
//                        for (Recipient merchandiser : merchandisers) {
//                            emailSender.sendEmailWithAttachment(merchandiser.getEmail(),
//                                    merchandiser.getFiles(),
//                                    subjectTextField.getText(),
//                                    processTemplateText(templateTextArea.getText(), merchandiser.getName()));
//
//                            progress++;
//
//                            progressBar.setValue(progress);
//                        }
//                    }
//                });
//
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (errorsStringBuilder.length() > 0) {
//                            statusLabel.setText(errorsStringBuilder.toString());
//                        } else {
//                            statusLabel.setText("Отправлено");
//                        }
//
//                        indeterminateProgressBar.setVisible(false);
//                    }
//                });
//
//                logger.info("Отправлено");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("MainForm");
                frame.setContentPane(new MainForm().panel1);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private String processTemplateText(String templateText, String name) {
        try {
            Template template = new Template("name",
                    new StringReader(templateText),
                    new Configuration());

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("name", name);

            StringWriter stringWriter = new StringWriter();

            template.process(parameters, stringWriter);

            return stringWriter.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (TemplateException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();

            progressBar.setIndeterminate(false);

            progressBar.setValue(progress);
        }
    }

    /**
     *
     */
    private class SendEmailTask extends SwingWorker<Void, Void> {

        @Override
        protected void done() {
            super.done();

            sendButton.setEnabled(true);

            statusLabel.setText("Отправлено");

            consoleTextArea.append("Отправлено.\n");
        }

        @Override
        protected Void doInBackground() throws Exception {
            setProgress(0);

            consoleTextArea.setText("");
            consoleTextArea.append("Отправляю...\n");

            logger.info("Отправляю...");

            final StringBuilder errorsStringBuilder = new StringBuilder();

            List<Recipient> recipients = null;
            Properties emailProperties = null;

            try {
                recipients = BroadcasterFileReader.loadMerchandisers("merchandisers.xml");

                emailProperties = BroadcasterFileReader.loadEmailProperties("email.properties");
            } catch (IOException e1) {
                errorsStringBuilder.append(e1.getMessage()).append(".");

                e1.printStackTrace();

                logger.info("Ошибка: " + e1.getMessage());
            } catch (SAXException e1) {
                errorsStringBuilder.append(e1.getMessage()).append(".");

                e1.printStackTrace();

                logger.info("Ошибка: " + e1.getMessage());
            }

            if (recipients == null) {
                statusLabel.setText("Проблемы при чтении файла merchandisers.xml.");

                logger.info("Проблемы при чтении файла merchandisers.xml.");

                throw new RuntimeException("merchandisers list is null!");
            }

            EmailSender emailSender = new EmailSender(emailProperties);

            progressBar.setMinimum(0);
            progressBar.setMaximum(recipients.size());

            int progress = 0;

            for (Recipient recipient : recipients) {
                String merchandiserName = recipient.getName();
                String merchandiserEmail = recipient.getEmail();

                emailSender.sendEmailWithAttachment(merchandiserEmail,
                        recipient.getFiles(),
                        subjectTextField.getText(),
                        processTemplateText(templateTextArea.getText(), merchandiserName));

                consoleTextArea.append("Отправлено " + merchandiserName + "\n");

                progress++;

                setProgress(progress);
            }

            if (errorsStringBuilder.length() > 0) {
                statusLabel.setText(errorsStringBuilder.toString());

                consoleTextArea.append(errorsStringBuilder.toString() + "\n");
            } else {
                statusLabel.setText("Отправлено \n");
            }

            logger.info("Отправлено");

            return null;
        }
    }
}
