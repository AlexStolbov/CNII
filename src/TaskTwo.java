import org.w3c.dom.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTwo {

    Map<String, List<Address>> allAddresses;

    public static void main(String[] args) {
        String pathToFile = System.getProperty("user.dir");
        String addresses = new TaskTwo().showAddresses(pathToFile);
        System.out.println(addresses);
    }

    public String showAddresses(String pathToFileDir) {

        String pathToFileAddress = Path.of(pathToFileDir, "AS_ADDR_OBJ.XML").toString();
        Document docAddress = Loader.loadDataFromFile(pathToFileAddress);
        if (docAddress == null) {
            return "Can't open file.";
        }
        allAddresses = Loader.convertDOMtoMap(docAddress);
        allAddresses = filterAddressType(allAddresses);

        String pathToFileHierarchy = Path.of(pathToFileDir, "AS_ADM_HIERARCHY.XML").toString();
        Document docHierarchy = Loader.loadDataFromFile(pathToFileHierarchy);
        if (docHierarchy == null) {
            return "Can't open file.";
        }
        AddressNode rootAddress = createAddressHierarchy(docHierarchy);

        // printTree(rootAddress, "");

        List<String> chainAddress = getChainActualAddress(rootAddress);

        return chainAddress.toString();
    }

    private Map<String, List<Address>> filterAddressType(Map<String, List<Address>> allAddresses) {
        Map<String, List<Address>> result = new HashMap<>();
        for (Map.Entry<String, List<Address>> address : allAddresses.entrySet()) {
            List<Address> filtered = address.getValue().stream().filter(el -> el.typeIs("проезд")).toList();
            if (!filtered.isEmpty()) {
                result.put(address.getKey(), filtered);
            }
        }
        return result;
    }

    /**
     * Создает дерево из кодов адресов
     *
     * @param doc - документ XML
     * @return - корневой элемент дерева
     */
    private AddressNode createAddressHierarchy(Document doc) {
        String RELATION_ISACTIVE = "1";
        String rootObjectId = null;
        Map<String, AddressNode> addressesAll = new HashMap<>();
        Element root = doc.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                NamedNodeMap attrib = child.getAttributes();
                if (attrib.getNamedItem("ISACTIVE").getNodeValue().equals(RELATION_ISACTIVE)) {
                    String currentId = attrib.getNamedItem("OBJECTID").getNodeValue();
                    String parentObjId = attrib.getNamedItem("PARENTOBJID").getNodeValue();

                    if (parentObjId.equals("0")) {
                        rootObjectId = currentId;
                    }
                    addAddressToHierarchy(currentId, parentObjId, addressesAll);
                }
            }
        }
        return addressesAll.get(rootObjectId);
    }

    private void addAddressToHierarchy(String objectId, String parentObjId, Map<String, AddressNode> addressAll) {
        AddressNode findNode = addressAll.computeIfAbsent(objectId, AddressNode::new);
        AddressNode findParent = addressAll.computeIfAbsent(parentObjId, AddressNode::new);
        findParent.addChild(findNode);
    }

    private List<String> getChainActualAddress(AddressNode root) {
        List<String> chain = new ArrayList<>();
        String currentObjId = root.getObjectId();
        List<Address> currentAddress = allAddresses.get(currentObjId);
        if (currentAddress != null) {
            chain.add(currentAddress.toString());
        }
        for (AddressNode address : root.getChild()) {
            List<String> childAddress = getChainActualAddress(address);
            chain.addAll(childAddress);
        }
        return chain;
    }

    /**
     * Visual representation
     */
    private void printTree(AddressNode root, String tab) {
        System.out.println(tab + root.getObjectId());
        for (AddressNode address : root.getChild()) {
            printTree(address, tab + "  ");
        }
    }

}
