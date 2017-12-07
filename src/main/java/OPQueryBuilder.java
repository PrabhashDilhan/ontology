import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.util.ArrayList;

/**
 * Created by Prabhash Dilhan on 11/14/2017.
 */
public class OPQueryBuilder {
    static OntologyClass oncl = new OntologyClass();
    private static JSONArray array = oncl.getClassArray();
    static ArrayList<String> propertytables = new ArrayList<String>();


    public static void main(String[] args)throws OWLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException{
        for (Object obj:array){
            JSONObject lineitem = (JSONObject)obj;
            System.out.println(queryBuilderForClassProperties(lineitem));
        }
    }

    public static String queryBuilderForClassProperties(JSONObject lineItem)throws OWLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException{

        String classtablequery = "";
        String kk = "";
        String classtablename = (String) lineItem.get("classname");
        Tree tree = OPQueryBuilder.getOntologyTree();
        LevelNodes ln = tree.selectLeafNodes(classtablename);
        ArrayList<String> liefnames = ln.getLeafNodes();
        JSONArray objectproperties = (JSONArray)lineItem.get("objectproperties");
        JSONObject objectpropertyrestrictions = (JSONObject) lineItem.get("objectpropertyrestrictions");
        JSONArray hasvalue = (JSONArray)objectpropertyrestrictions.get("object_has_value");
        //here have to check whether they are restricted or not
        for(Object propertyobject:objectproperties){
            JSONObject propertyobj = (JSONObject) propertyobject;
            if(propertyobj.containsKey("isfunctional")){
                kk = ""+propertyobj.get("objectpropertyname")+" INT, "+
                        propertyobj.get("objectpropertyname")+"_Referential_id VARCHAR(50),";
                classtablequery  = "CREATE TRIGGER "+classtablename+"_"+propertyobj.get("objectpropertyname")+" BEFORE INSERT ON "+classtablename+
                        " FOR EACH ROW BEGIN " +
                        "IF (!("+createFirstIfCondition(liefnames,classtablename)+")) " +
                        "THEN " +
                            "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'violated the referential integrity with user tables'; " +
                        "ELSE "+
                        createObjectPropertyTriggerIfCondition(liefnames,(String) propertyobj.get("objectpropertyname"))+
                        "END IF"+";" +
                        " END;";
                setHasValue(hasvalue,classtablename,propertyobj);
            }else{

            }
        }
        System.out.println(kk);
        propertytables.add(classtablequery);
        return kk;
    }
    private static Tree getOntologyTree()throws OWLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException{
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // We load an ontology from the URI specified
        // on the command line

        // Now load the ontology.
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology ontology = loadedOntology.getOwlOntologyManager();
        // Report information about the ontology
        System.out.println("Ontology Loaded...");
        System.out.println("Ontology : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));
        // / Create a new SimpleHierarchy object with the given reasoner.
        SimpleHierarchy simpleHi = new SimpleHierarchy(
                new StructuralReasonerFactory(), ontology);
        // Get Thing
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        System.out.println("Class       : " + clazz);
        // Print the hierarchy below thing
        simpleHi.printHierarchy(clazz);
        Tree tree = simpleHi.getOntologyTree();
        return tree;
    }
    private static String createObjectPropertyTriggerIfCondition(ArrayList<String> children, String propertyname){
        String ifcondition = "";
        for (String classname:children){
            ifcondition = ifcondition +
                    "IF(new."+propertyname+"_Referential_id"+"="+classname+")"+
                    "THEN"+
                        " IF (!((select ID from "+classname+ " where Referential_id="+propertyname+"_Referential_id"+")=new."+propertyname+")) " +
                        "THEN " +
                        "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'violated the referential integrity with "+classname+" table'; " +
                        "END IF"+";"+
                    "END IF"+";";
        }
        return ifcondition;
    }
    private static String createFirstIfCondition(ArrayList<String> children, String tablename){
        String ifcondition = "";
        int count = 0;
        children.remove(tablename);
        for (String classname:children){
            count++;
            if (count == children.size()) {
                ifcondition = ifcondition +
                        classname;
            } else {
                ifcondition = ifcondition +
                        classname + " || ";
            }
        }
        return ifcondition;
    }
    private static void setHasValue(JSONArray restrictions,String classtablename,JSONObject propertyobj){
        String trigger  = "";
        int count = 1;
        if(!restrictions.isEmpty()){
            System.out.println("dfdsfds");
            for(Object jj:restrictions) {
                JSONObject hasvalue = (JSONObject) jj;
                String propertyname = (String)hasvalue.get("propertyname");
                JSONArray fillers = (JSONArray)hasvalue.get("fillerclasses") ;
                trigger =
                        "CREATE TRIGGER " + classtablename + "_" + propertyobj.get("objectpropertyname") + "_hasvaluetrigger"+count+ " BEFORE INSERT ON " + classtablename +
                                " FOR EACH ROW BEGIN " +
                                "IF (!("+createIfConditionHasValueTrigger(fillers,propertyname,hasvalue)+")) " +
                                "THEN " +
                                "SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'has value restriction should be with the instance "+hasvalue.get("invidualname")+" in class'; " +
                                "END IF" + ";" +
                                " END;";
                count++;
                System.out.println(trigger);
            }
        }
    }
    private static String createIfConditionHasValueTrigger(JSONArray fillers,String propertyname, JSONObject propertyobj){
        String ifcondition = "";
        int count = 0;
        for (Object hh:fillers){
            String classname = (String)hh;
            count++;
            if (count == fillers.size()) {
                ifcondition = ifcondition +
                    "(new." + propertyname + "_Referential_id" + "=" + classname +
                    " && (select ID from " + classname +
                    " where Instance_name="+propertyobj.get("invidualname")+") = new." +
                        propertyname + " )";
            } else {
                ifcondition = ifcondition +
                    "(new." + propertyname + "_Referential_id" + "=" + classname +
                    " && (select ID from " + classname +
                    " where Instance_name="+propertyobj.get("invidualname")+") = new." +
                        propertyname + " )"
                    + " && ";
            }
        }
        return ifcondition;
    }
    public static ArrayList<String> getObjectPropertytriggers(){
        return propertytables;
    }
}
