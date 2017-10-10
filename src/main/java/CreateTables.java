/**
 * Created by Prabhash Dilhan on 10/10/2017.
 */
public class CreateTables {
    public static void main(String[] args){
        StringQueryBuilder qb = new StringQueryBuilder();
        String sql = qb.queryBuildingMethod();
        DataBaseConnection dc = new DataBaseConnection();
        dc.setquerystring(sql);
        dc.dbconnect();
        for (String ff:qb.getDataspropertytables()){
            dc.setquerystring(ff);
            dc.dbconnect();
        }
    }
}
