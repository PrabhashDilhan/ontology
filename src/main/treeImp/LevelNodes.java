import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Prabhash Dilhan on 10/29/2017.
 */
public class LevelNodes {
    private ArrayList<String> list;

    public LevelNodes(HashMap<String, Node> tree, String identifier){
        list = new ArrayList<String>();

        if (tree.containsKey(identifier)) {
            this.buildList(tree, identifier);
        }
    }
    private void buildList(HashMap<String, Node> tree, String identifier) {
        ArrayList<String> children = tree.get(identifier).getChildren();
        if(!children.isEmpty()) {
            for (String child : children) {

                // Recursive call
                this.buildList(tree, child);
            }
        }
        else {
            list.add(tree.get(identifier).getIdentifier());
        }
    }
    public ArrayList<String> getLeafNodes(){
        return list;
    }
}
