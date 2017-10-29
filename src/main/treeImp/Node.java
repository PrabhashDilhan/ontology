/**
 * Created by Prabhash Dilhan on 10/29/2017.
 */

import java.util.ArrayList;

public class Node {

    private String identifier;
    private ArrayList<String> children;
    private String parent;

    // Constructor
    public Node(String identifier) {
        this.identifier = identifier;
        children = new ArrayList<String>();
    }

    // Properties
    public String getIdentifier() {
        return identifier;
    }

    public ArrayList<String> getChildren() {
        return children;
    }

    public String getParent(){return parent;}

    // Public interface
    public void addChild(String identifier) {
        children.add(identifier);
    }

    public void addParent(String identifier){
        this.parent = identifier;
    }
}
