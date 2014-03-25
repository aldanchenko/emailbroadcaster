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

    /**
     * Default logger.
     */
    private static final Logger logger = Logger.getLogger(MainForm.class);

    private JTextArea templateTextArea;
    private JPanel panel1;
    private JButton sendButton;
    private JTextField subjectTextField;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea consoleTextArea;

    /**
     * Default constructor.
     */
    public MainForm() {
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setIndeterminate(true);
                progressBar.setMinimum(0);

                SendEmailTask task = new SendEmailTask();
                task.addPropertyChangeListener(MainForm.this);
                task.execute();
            }
        });
    }

    /**
     * Generate email text string using template from input and name parameter from
     * merchandisers.xml file.
     *
     * @param templateText - source template
     * @param name         - name parameter for template
     *
     * @return String (text of email)
     */
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
        } catch (IOException | TemplateException exception) {
            logger.error(exception.getMessage());
        }

        return null;
    }

    /**
     * Listener for progress bar of email sending.
     *
     * @param evt - source event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();

            progressBar.setIndeterminate(false);

            progressBar.setValue(progress);
        }
    }

    /**
     * {@link javax.swing.SwingWorker} extension task for sending email.
     * All real work is hear.
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
            } catch (IOException | SAXException exception) {
                errorsStringBuilder.append(exception.getMessage()).append(".");

                exception.printStackTrace();

                logger.info("Ошибка: " + exception.getMessage());
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

    /**
     * Main method.
     *
     * @param args - source console arguments.
     */
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
}
