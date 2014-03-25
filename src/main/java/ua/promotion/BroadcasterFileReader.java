package ua.promotion;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;
import ua.promotion.bean.Recipient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Simple XML parser.
 *
 * @author aldanchenko
 */
public class BroadcasterFileReader {

    /**
     * Load Email properties.
     *
     * @param fileName - path to file
     *
     * @throws IOException
     *
     * @return Properties
     */
    public static Properties loadEmailProperties(String fileName) throws IOException {
        Properties properties = System.getProperties();

        properties.load(new FileInputStream(fileName));

        return properties;
    }

    /**
     * Parse XML with Apache Digester.
     *
     * @param xmlName - source XML file name
     *
     * @throws IOException
     * @throws SAXException
     *
     * @return List<Recipient>
     */
    public static List<Recipient> loadMerchandisers(String xmlName) throws IOException, SAXException {
        Digester digester = new Digester();

        digester.addObjectCreate("merchandisers", ArrayList.class);
        digester.addObjectCreate("merchandisers/merchandiser", Recipient.class);
        digester.addCallMethod("merchandisers/merchandiser/name", "setName", 1);
        digester.addCallParam("merchandisers/merchandiser/name", 0);

        digester.addCallMethod("merchandisers/merchandiser/email", "setEmail", 1);
        digester.addCallParam("merchandisers/merchandiser/email", 0);

        digester.addCallMethod("merchandisers/merchandiser/files/file", "addFile", 1);
        digester.addCallParam("merchandisers/merchandiser/files/file", 0);

        digester.addSetNext("merchandisers/merchandiser", "add");

        return (List<Recipient>) digester.parse(xmlName);
    }
}
