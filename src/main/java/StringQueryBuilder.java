import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Prabhash Dilhan on 9/5/2017.
 */
public class StringQueryBuilder {

    static OntologyClass oncl = new OntologyClass();
    private static JSONArray array = oncl.getClassArray();
    static ArrayList<String> dataspropertytables = new ArrayList<String>();
    static Set<String> earlyRestrictedHasValueProperties = new HashSet<String>();
    static Set<String> earlyRestrictedMinCardinalityProperties = new HashSet<String>();
    static Set<String> earlyRestrictedMaxCardinalityProperties = new HashSet<String>();
    static Set<String> earlyRestrictedExactCardinalityProperties = new HashSet<String>();
    public static void main(String[] args){

        //queryBuildingMethod();
    }
    public  String queryBuildingMethod(JSONObject jsonLineItem){
        System.out.println(array);
        String createclasstablequery="";
        dataspropertytables = new ArrayList<String>();
        earlyRestrictedHasValueProperties = new HashSet<String>();
        earlyRestrictedMinCardinalityProperties = new HashSet<String>();
        earlyRestrictedMaxCardinalityProperties = new HashSet<String>();
        earlyRestrictedExactCardinalityProperties = new HashSet<String>();
        JSONArray properyarray = (JSONArray) jsonLineItem.get("dataproperties");

        createclasstablequery = createclasstablequery + setDataHasValueRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"));
        createclasstablequery = createclasstablequery +setMaxCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"), (String) jsonLineItem.get("classname"));
        setMinCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),(String) jsonLineItem.get("classname"));
        createclasstablequery = createclasstablequery + setExactCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"), (String) jsonLineItem.get("classname"));
        createclasstablequery = createclasstablequery + createTableForClassProperties(properyarray,jsonLineItem);
        createclasstablequery = createclasstablequery +createColumnFromSuperclassDataProperties(jsonLineItem,(String) jsonLineItem.get("classname"));

        String finaltablequery = "CREATE TABLE " + jsonLineItem.get("classname") + " (" +
                "ID INT AUTO_INCREMENT,"+
                "instance_name VARCHAR(100),"+createclasstablequery +
                "PRIMARY KEY (ID)" +
                ");";
        System.out.println(finaltablequery);
        System.out.println(dataspropertytables);
        return finaltablequery;
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
    private static String createTableForClassProperties(JSONArray properyarray,JSONObject jsonLineItem ){
        String createdatapropertytablequery;
        String createclasstablequery = "";
        if (!properyarray.isEmpty()) {
            for (Object propobj : properyarray) {
                //we think own data property cannot restrict on their own class
                JSONObject propertyobj = (JSONObject) propobj;
                if(!isEarlyRestrictedProperty((String) propertyobj.get("datapropertyname"))) {
                    if (propertyobj.containsKey("isfunctional")) {
                        createclasstablequery = createclasstablequery + " " +
                                propertyobj.get("datapropertyname") + " " +
                                getDataType((String) propertyobj.get("datatype")) + ",";
                    } else {
                        createdatapropertytablequery = "CREATE TABLE " + propertyobj.get("datapropertyname") + " (" +
                                jsonLineItem.get("classname") + "_ID INT NOT NULL," +
                                "propertyvalue " + getDataType((String) propertyobj.get("datatype")) + "," +
                                "FOREIGN KEY (" + jsonLineItem.get("classname") + "_ID) REFERENCES " + jsonLineItem.get("classname") + "(ID)," +
                                "PRIMARY KEY (" + jsonLineItem.get("classname") + "_ID,propertyvalue)" +
                                ");";
                        dataspropertytables.add(createdatapropertytablequery);
                    }
                }
            }
        }
        return createclasstablequery;
    }
    private static String createColumnFromSuperclassDataProperties(JSONObject kk,String originalaclassname){
        String classtablequery = "";
          for (Object obj:array) {
              JSONObject jsonLineItem = (JSONObject) obj;
              JSONArray superclasses = (JSONArray) kk.get("supperclasses");
              if (!superclasses.isEmpty()) {
                  for (Object superclassname : superclasses) {
                      if (superclassname.toString().equals(jsonLineItem.get("classname")) && (!superclassname.toString().equals(originalaclassname))) {
                          JSONObject cc =(JSONObject) jsonLineItem.get("datapropertyrestrictions");
                          JSONArray klhv = (JSONArray) cc.get("data_has_value");
                          if (klhv != null) {
                              for (Object objk : klhv) {
                                  JSONObject hasdatavalueobject = (JSONObject) objk;
                                  if(!isEarlyRestrictedProperty((String)hasdatavalueobject.get("propertyname"))) {
                                      classtablequery = classtablequery + " " + setDataHasValueRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"));
                                  }
                              }
                          }
                          JSONArray klmac = (JSONArray) cc.get("data_max_cardinality");
                          if (klmac != null) {
                              for (Object objk : klmac) {
                                  JSONObject hasdatavalueobject = (JSONObject) objk;
                                  if(!isEarlyRestrictedProperty((String)hasdatavalueobject.get("propertyname"))) {
                                      classtablequery = classtablequery + " " + setMaxCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),originalaclassname);
                                  }
                              }
                          }
                          JSONArray klmic = (JSONArray) cc.get("data_min_cardinality");
                          if (klmic != null) {
                              for (Object objk : klmic) {
                                  JSONObject hasdatavalueobject = (JSONObject) objk;
                                  if(!isEarlyRestrictedProperty((String)hasdatavalueobject.get("propertyname"))) {
                                      setMinCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),originalaclassname);
                                  }
                              }
                              JSONArray klec = (JSONArray) cc.get("data_exact_cardinality");
                              if (klec != null) {
                                  for (Object objk : klec) {
                                      JSONObject hasdatavalueobject = (JSONObject) objk;
                                      if(!isEarlyRestrictedProperty((String)hasdatavalueobject.get("propertyname"))) {
                                          classtablequery = classtablequery + " " + setExactCardinalityRestriction((JSONObject) jsonLineItem.get("datapropertyrestrictions"),originalaclassname);
                                      }
                                  }
                              }
                          }
                          classtablequery = classtablequery + " " + createColumnFromSuperclassDataProperties(jsonLineItem,(String) superclassname);
                      }
                  }
              }
          }
          return classtablequery;
    }
    //there is a problem to handle in this function when same data property have 2 or more
    //has value restrictions in the same class
    private static String setDataHasValueRestriction(JSONObject restrictionobject){
        String classtablequery = "";
        JSONArray kl = (JSONArray)restrictionobject.get("data_has_value");
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject hasdatavalueobject = (JSONObject)obj;
                classtablequery = classtablequery + " " +
                        hasdatavalueobject.get("propertyname") + " " +
                        "ENUM("+"\'"+hasdatavalueobject.get("literalvalue")+"\'),";
                earlyRestrictedHasValueProperties.add(hasdatavalueobject.get("propertyname").toString());
            }
        }
        return classtablequery;
    }
    private static String setMaxCardinalityRestriction(JSONObject restrictionobject, String classtablename){
        String classtablequery = "";
        JSONArray kl = (JSONArray)restrictionobject.get("data_max_cardinality");
        String createdatapropertytablequery;
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject maxcardinalityobject = (JSONObject)obj;
                if(maxcardinalityobject.get("cardinalityvalue").toString()=="1") {
                    classtablequery = classtablequery + " " +
                            maxcardinalityobject.get("propertyname") + " " +
                            getDataType((String) maxcardinalityobject.get("datatype"))+",";
                    earlyRestrictedMaxCardinalityProperties.add(maxcardinalityobject.get("propertyname").toString());
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
                    earlyRestrictedMaxCardinalityProperties.add(maxcardinalityobject.get("propertyname").toString());

                }
            }
        }
        return classtablequery;
    }
    private static void setMinCardinalityRestriction(JSONObject restrictionobject, String classtablename){
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
                earlyRestrictedMinCardinalityProperties.add(mincardinalityobject.get("propertyname").toString());

            }
        }
    }
    private static String setExactCardinalityRestriction(JSONObject restrictionobject, String classtablename){
        String classtablequery = "";
        JSONArray kl = (JSONArray)restrictionobject.get("data_exact_cardinality");
        String createdatapropertytablequery;
        if(kl != null ) {
            for(Object obj:kl) {
                JSONObject exactcardinalityobject = (JSONObject)obj;
                if(exactcardinalityobject.get("cardinalityvalue").toString()=="1") {
                    classtablequery = classtablequery + " " +
                            exactcardinalityobject.get("propertyname") + " " +
                            getDataType((String) exactcardinalityobject.get("datatype"))+",";
                    earlyRestrictedExactCardinalityProperties.add(exactcardinalityobject.get("propertyname").toString());
                }
                else{
                    createdatapropertytablequery = "CREATE TABLE "+classtablename+"_"+exactcardinalityobject.get("propertyname")+" ("+
                            classtablename+"_ID INT NOT NULL,"+
                            "propertyvalue "+getDataType((String) exactcardinalityobject.get("datatype"))+","+
                            "FOREIGN KEY ("+classtablename+"_ID) REFERENCES "+classtablename+"(ID),"+
                            "PRIMARY KEY ("+classtablename+"_ID,propertyvalue)"+
                            ");";

                    String jj = "CREATE TRIGGER "+classtablename+"_"+exactcardinalityobject.get("propertyname")+"_exact"+" BEFORE INSERT ON "+classtablename+"_"+exactcardinalityobject.get("propertyname")+
                            " FOR EACH ROW BEGIN IF ((SELECT COUNT(*) FROM "+ classtablename+"_"+exactcardinalityobject.get("propertyname")+" WHERE "+classtablename+"_ID = NEW."+classtablename+"_ID) > "
                            +exactcardinalityobject.get("cardinalityvalue").toString()+") THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only three products per user allowed'; END IF"+";" +
                            ""+" END;";
                    dataspropertytables.add(createdatapropertytablequery);
                    dataspropertytables.add(jj);
                    earlyRestrictedExactCardinalityProperties.add(exactcardinalityobject.get("propertyname").toString());

                }

            }
        }
        return classtablequery;
    }
    private static boolean isEarlyRestrictedProperty(String propertyName){
        boolean isearlyrestricted = false;
        if(!earlyRestrictedExactCardinalityProperties.isEmpty()) {
            for (String restrictedpropertyname : earlyRestrictedExactCardinalityProperties) {
                System.out.println(restrictedpropertyname);
                if (!restrictedpropertyname.equals(propertyName)) {
                    isearlyrestricted = true;
                }
            }
        }else if((!earlyRestrictedMaxCardinalityProperties.isEmpty())) {
            for (String restrictedpropertyname : earlyRestrictedMaxCardinalityProperties) {
                System.out.println(restrictedpropertyname);
                if (!restrictedpropertyname.equals(propertyName)) {
                    isearlyrestricted = true;
                }
            }
        }
        else if((!earlyRestrictedMinCardinalityProperties.isEmpty())){
            for (String restrictedpropertyname : earlyRestrictedMinCardinalityProperties) {
                System.out.println(restrictedpropertyname);
                if (!restrictedpropertyname.equals(propertyName)) {
                    isearlyrestricted = true;
                }
            }
        }
        else if((!earlyRestrictedHasValueProperties.isEmpty())){
            for (String restrictedpropertyname : earlyRestrictedHasValueProperties) {
                System.out.println(restrictedpropertyname);
                if (!restrictedpropertyname.equals(propertyName)) {
                    isearlyrestricted = true;
                }
            }
        }
        else {
            isearlyrestricted = false;
        }
        return isearlyrestricted;
    }
    public static ArrayList<String> getDataspropertytables(){
        return dataspropertytables;
    }

    public static JSONArray getClassArray(){
        JSONArray hh = array;
        return hh;
    }
}
