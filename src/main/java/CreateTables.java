import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.dc;

/**
 * Created by Prabhash Dilhan on 10/10/2017.
 */
public class CreateTables {
    public static void main(String[] args){
        StringQueryBuilder qb = new StringQueryBuilder();
        for (Object obj:qb.getClassArray()){
            JSONObject jj = (JSONObject) obj;
            String sql = qb.queryBuildingMethod(jj);
            DataBaseConnection dc = new DataBaseConnection();
            dc.setquerystring(sql);
            dc.dbconnect();
            for (String ff : qb.getDataspropertytables()) {
                dc.setquerystring(ff);
                dc.dbconnect();
            }
        }
    }
}
