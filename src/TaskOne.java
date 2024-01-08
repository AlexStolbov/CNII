import org.w3c.dom.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class TaskOne {

    public static void main(String[] args) {

        String pathToFileDir = System.getProperty("user.dir");
        String addresses = new TaskOne().getAddresses("2005-02-24",
                "1460061, 1415465, 1450709, 7777",
                pathToFileDir);
        System.out.println(addresses);
    }

    /**
     * Формирует вывод с адресами по заданным OBJECTID
     *
     * @param onDate      - дата, на которую адрес дейстителен.
     * @param objectId    - текстовый список OBJECTID
     * @param pathToFileDir - путь к папке с файлом с адресами.
     * @return
     */
    public String getAddresses(String onDate, String objectId, String pathToFileDir) {
        String pathToFile = Path.of(pathToFileDir, "AS_ADDR_OBJ.XML").toString();
        Document doc = Loader.loadDataFromFile(pathToFile);
        if (doc == null) {
            return "Can't open file.";
        }
        Map<String, List<Address>> allAddresses = Loader.convertDOMtoMap(doc);

        String[] id = Arrays.stream(objectId.split(",")).map(String::strip).toArray(String[]::new);
        LocalDate addressOnDate = LocalDate.parse(onDate);
        Map<String, Address> findAddresses = new HashMap<>();
        for (String currentId : id) {
            List<Address> addresses = allAddresses.get(currentId);
            if (addresses != null) {
                for (Address address : addresses) {
                    if (address.isUpToDate(addressOnDate)) {
                        findAddresses.put(currentId, address);
                    }
                }
            } else {
                findAddresses.put(currentId, new Address());
            }
        }
        return formatOutput(findAddresses);
    }

    /**
     * Преобразует коллекцию с адресами в текстовый формат для вывода пользователю.
     */
    private String formatOutput(Map<String, Address> findAddresses) {
        List<String> res = new ArrayList<>();
        for (Map.Entry<String, Address> entry : findAddresses.entrySet()) {
            Address currentAddress = entry.getValue();
            res.add(entry.getKey() + ": " + (currentAddress.isEmpty() ? " not found" : currentAddress));
        }
        return String.join(String.format("%n"), res);
    }

}
