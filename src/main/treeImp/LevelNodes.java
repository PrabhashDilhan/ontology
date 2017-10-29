import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prabhash Dilhan on 10/29/2017.
 */
public class LevelNodes {
    private HashMap<Integer, ArrayList<String>> levels;

    public LevelNodes(HashMap<Integer, ArrayList<String>> levels){
        this.levels = levels;
    }
    public int getDepthOfTheTree(){
        return levels.size() -1;
    }

    public ArrayList<String> getLiefNodes(int depth){
        return levels.get(depth);
    }
}
