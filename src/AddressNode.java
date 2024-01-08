import java.util.ArrayList;
import java.util.List;

public class AddressNode {
    final private String objectId;
    final private List<AddressNode> child;

    public AddressNode(String objectId) {
        this.objectId = objectId;
        this.child = new ArrayList<>();
    }

    public void addChild(AddressNode objectId) {
        if (! child.contains(objectId)) {
            child.add(objectId);
        }
    }

    public String getObjectId() {
        return objectId;
    }

    public List<AddressNode> getChild() {
        return new ArrayList<>(child);
    }

    @Override
    public String toString() {
        return "AN{" + objectId + " \n" + child + '}';
    }
}
