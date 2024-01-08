import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader {
    /**
     * Загружает данные из XML файла.
     * @param pathToFile - папка с файлами
     * @return - DOM документ.
     */
    static public Document loadDataFromFile(String pathToFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            Path path = Path.of(pathToFile);
            File f = path.toFile();
            doc = builder.parse(f);
        } catch (ParserConfigurationException e) {
            System.out.printf("Can't create DocumentBuilder %s %n", e);
        } catch (FileNotFoundException e) {
            System.out.printf("File non found: %s %n", pathToFile);
        } catch (IOException | SAXException e) {
            System.out.println(e);
        }
        return doc;
    }

    /**
     * Перобразует переданный DOM в коллекцию.
     */
    static public Map<String, List<Address>> convertDOMtoMap(Document doc) {
        Map<String, List<Address>> allAddresses = new HashMap<>();
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                NamedNodeMap attrib = child.getAttributes();
                String currentId = attrib.getNamedItem("OBJECTID").getNodeValue();
                List<Address> addressesOfCurrentId = allAddresses.getOrDefault(currentId, new ArrayList<>());
                Address newAddress = new Address(
                        attrib.getNamedItem("TYPENAME").getNodeValue(),
                        attrib.getNamedItem("NAME").getNodeValue(),
                        LocalDate.parse(attrib.getNamedItem("STARTDATE").getNodeValue()),
                        LocalDate.parse(attrib.getNamedItem("ENDDATE").getNodeValue())
                );
                addressesOfCurrentId.add(newAddress);
                allAddresses.put(currentId, addressesOfCurrentId);
            }
        }
        return allAddresses;
    }

}
