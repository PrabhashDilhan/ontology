/**
 * Created by Prabhash Dilhan on 10/29/2017.
 */
import java.util.ArrayList;
import java.util.HashMap;


/*
 * See URL: http://en.wikipedia.org/wiki/Depth-first_search
 */

public class LiefPath{
    private ArrayList<String> list;

    public LiefPath(HashMap<String, Node> tree, String identifier) {
        list = new ArrayList<String>();

        if (tree.containsKey(identifier)) {
            this.buildList(tree, identifier);
        }
    }

    private void buildList(HashMap<String, Node> tree, String identifier) {
        list.add(tree.get(identifier).getIdentifier());
        String parent = tree.get(identifier).getParent();
        if(parent!=null){
            // Recursive call
            this.buildList(tree, parent);
        }
    }

    public ArrayList<String> getPath() {
        return list;
    }
}