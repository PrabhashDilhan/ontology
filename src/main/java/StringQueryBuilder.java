import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by Prabhash Dilhan on 9/5/2017.
 */
public class StringQueryBuilder {

    static OntologyClass oncl = new OntologyClass();
    private static JSONArray array = oncl.getClassArray();
    static ArrayList<String> dataspropertytables = new ArrayList<String>();
    public static void main(String[] args){

        //queryBuildingMethod();
    }
    public  String queryBuildingMethod(){
        String createclasstablequery=null;
        for (Object obj:array){

            JSONObject jsonLineItem = (JSONObject) obj;
            createclasstablequery = "CREATE TABLE "+jsonLineItem.get("classname")+" ("+
                    "ID INT AUTO_INCREMENT,";
            String createdatapropertytablequery = "";
            JSONArray properyarray = (JSONArray) jsonLineItem.get("dataproperties");
            if(!properyarray.isEmpty()){
                for(Object propobj:properyarray){
                    //we think own data property cannot restrict on their own class
                    JSONObject propertyobj = (JSONObject)propobj;
                    if(propertyobj.containsKey("isfunctional")){
                        createclasstablequery = createclasstablequery+" "+
                                propertyobj.get("datapropertyname")+" "+
                                getDataType((String) propertyobj.get("datatype"))+",";
                    }
                    else{
                        createdatapropertytablequery = "CREATE TABLE "+propertyobj.get("datapropertyname")+" ("+
                                jsonLineItem.get("classname")+"_ID INT NOT NULL,"+
                                "propertyvalue "+getDataType((String) propertyobj.get("datatype"))+","+
                                "FOREIGN KEY ("+jsonLineItem.get("classname")+"_ID) REFERENCES "+jsonLineItem.get("classname")+"(ID),"+
                                "PRIMARY KEY ("+jsonLineItem.get("classname")+"_ID,propertyvalue)"+
                                ");";
                        dataspropertytables.add(createdatapropertytablequery);
                    }
                }
            }
            createColumnFromSuperclassDataProperties(jsonLineItem,createclasstablequery);
            setDataHasValueRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),createclasstablequery);
            setMaxCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),createclasstablequery,(String)jsonLineItem.get("classname"));
            setMinCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),createclasstablequery,(String)jsonLineItem.get("classname"));

            createclasstablequery = createclasstablequery +
                    "PRIMARY KEY (ID)"+
                    ");";
            System.out.println(createclasstablequery);
            System.out.println(dataspropertytables);


        }
        return createclasstablequery;
    }
    private static String getDataType(String owldatatype) {
        if (owldatatype.equals("string")) {
            return "VARCHAR(100)";
        }
        if (owldatatype.equals("nonNegativeInteger")) {
            return "INT(10)";
        }
        if (owldatatype.equals("int")) {
            return "INT(10)";
        }
        return null;

    }
    private static void createColumnFromSuperclassDataProperties(JSONObject obj,String classtablequery){

    }
    private static void setDataHasValueRestriction(JSONObject restrictionobject,String classtablequery){
        JSONArray kl = (JSONArray)restrictionobject.get("data_has_value");
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject hasdatavalueobject = (JSONObject)obj;
                classtablequery = classtablequery + " " +
                        hasdatavalueobject.get("propertyname") + " " +
                        "ENUM("+hasdatavalueobject.get("literalvalue")+"),";
            }
        }
    }
    private static void setMaxCardinalityRestriction(JSONObject restrictionobject,String classtablequery, String classtablename){
        JSONArray kl = (JSONArray)restrictionobject.get("data_max_cardinality");
        String createdatapropertytablequery;
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject maxcardinalityobject = (JSONObject)obj;
                if(maxcardinalityobject.get("cardinalityvalue").toString()=="1") {
                    classtablequery = classtablequery + " " +
                            maxcardinalityobject.get("propertyname") + " " +
                            getDataType((String) maxcardinalityobject.get("datatype"))+",";
                }
                else{
                    createdatapropertytablequery = "CREATE TABLE "+classtablename+"_"+maxcardinalityobject.get("propertyname")+" ("+
                            classtablename+"_ID INT NOT NULL,"+
                            "propertyvalue "+getDataType((String) maxcardinalityobject.get("datatype"))+","+
                            "FOREIGN KEY ("+classtablename+"_ID) REFERENCES "+classtablename+"(ID),"+
                            "PRIMARY KEY ("+classtablename+"_ID,propertyvalue)"+
                            ");";

                    String jj = "CREATE TRIGGER "+classtablename+"kk BEFORE INSERT ON "+classtablename+"_"+maxcardinalityobject.get("propertyname")+
                    " FOR EACH ROW BEGIN IF ((SELECT COUNT(*) FROM "+ classtablename+"_"+maxcardinalityobject.get("propertyname")+" WHERE "+classtablename+"_ID = NEW."+classtablename+"_ID) > "
                            +maxcardinalityobject.get("cardinalityvalue").toString()+") THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only three products per user allowed'; END IF"+";" +
                            ""+" END;";
                    dataspropertytables.add(createdatapropertytablequery);
                    dataspropertytables.add(jj);
                }
            }
        }
    }
    private static void setMinCardinalityRestriction(JSONObject restrictionobject,String classtablequery, String classtablename){
        JSONArray kl = (JSONArray)restrictionobject.get("data_min_cardinality");
        String createdatapropertytablequery;
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject mincardinalityobject = (JSONObject)obj;
                    createdatapropertytablequery = "CREATE TABLE "+classtablename+"_"+mincardinalityobject.get("propertyname")+" ("+
                            classtablename+"_ID INT NOT NULL,"+
                            "propertyvalue "+getDataType((String) mincardinalityobject.get("datatype"))+","+
                            "FOREIGN KEY ("+classtablename+"_ID) REFERENCES "+classtablename+"(ID),"+
                            "PRIMARY KEY ("+classtablename+"_ID,propertyvalue)"+
                            ");";
                    dataspropertytables.add(createdatapropertytablequery);
            }
        }
    }
    private static void setExactCardinalityRestriction(JSONObject restrictionobject,String classtablequery, String classtablename){
        JSONArray kl = (JSONArray)restrictionobject.get("data_exact_cardinality");
        String createdatapropertytablequery;
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject mincardinalityobject = (JSONObject)obj;
                    createdatapropertytablequery = "CREATE TABLE "+classtablename+"_"+mincardinalityobject.get("propertyname")+" ("+
                            classtablename+"_ID INT NOT NULL,"+
                            "propertyvalue "+getDataType((String) mincardinalityobject.get("datatype"))+","+
                            "FOREIGN KEY ("+classtablename+"_ID) REFERENCES "+classtablename+"(ID),"+
                            "PRIMARY KEY ("+classtablename+"_ID,propertyvalue)"+
                            ");";
                    dataspropertytables.add(createdatapropertytablequery);
            }
        }
    }
    public static ArrayList<String> getDataspropertytables(){
        return dataspropertytables;
    }
}
