import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Prabhash Dilhan on 8/16/2017.
 */
public class OntologyClass {

    public  JSONArray array;
    public static void main(String[] args){
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology ontology = loadedOntology.getOwlOntologyManager();
        Set<OWLClass> classes = ontology.getClassesInSignature();
        createDataBaseTables(classes,ontology);

    }
    public OntologyClass(){
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology ontology = loadedOntology.getOwlOntologyManager();
        Set<OWLClass> classes = ontology.getClassesInSignature();
        array = createDataBaseTables(classes,ontology);
    }
    private static Set<OWLClass>  getSupperClasses(OWLOntology ontology, OWLClass dd){
        //System.out.println("\tSub classes");
        Set<OWLClass> cc = new HashSet<OWLClass>();
        for(OWLSubClassOfAxiom subclass:ontology.getAxioms(AxiomType.SUBCLASS_OF)){
            if(subclass.getSubClass().equals(dd)){
                for(OWLClass owlcLass:subclass.getClassesInSignature()) {
                    //System.out.println("\t\t +:"+owlcLass.getIRI().getShortForm());
                    cc.add(owlcLass);
                }
            }
        }
        //System.out.println(dd.getIRI().getShortForm());
        //System.out.println("\t\t\t:"+cc);
        return cc;
    }
    private static Set<OWLObjectProperty>  getObjectPropertyDomain(OWLOntology ontology, OWLClass dd){
        //System.out.println(" \tObject Property Domain");

        Set<OWLObjectProperty> ff = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            if (op.getDomain().equals(dd)) {
                for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
                    //System.out.println("\t\t +: " + oop.getIRI().getShortForm());
                    //System.out.println("\t\t\t\t :"+ );
                    ff.add(oop);
                }
                //System.out.println("\t\t +: " + op.getProperty().getNamedProperty().getIRI().getShortForm());
            }
        }
        return ff;
    }
    private static Set<OWLDataProperty>  getDataPropertyDomain(OWLOntology ontology, OWLClass dd){
        //System.out.println(" \tData Property Domain");
        Set<OWLDataProperty> ff = new HashSet<OWLDataProperty>();
        for (OWLDataPropertyDomainAxiom dp : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
            if (dp.getDomain().equals(dd)){
                for(OWLDataProperty odp : dp.getDataPropertiesInSignature()){
                    //System.out.println("\t\t +: " + odp.getIRI().getShortForm());
                    ff.add(odp);
                }
                //System.out.println("\t\t +:" + dp.getProperty());
            }
        }
        return ff;
    }
    private static Set<OWLObjectProperty>  getObjectPropertyRange(OWLOntology ontology, OWLClass dd){
        //System.out.println(" \tData Property Domain");
        Set<OWLObjectProperty> ff = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyRangeAxiom dp : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
            if (dp.getRange().equals(dd)){
                for(OWLObjectProperty odp : dp.getObjectPropertiesInSignature()){
                    //System.out.println("\t\t +: " + odp.getIRI().getShortForm());
                    ff.add(odp);
                }
                //System.out.println("\t\t +:" + dp.getProperty());
            }
        }
        return ff;
    }
    private static JSONArray getobjectpropertydetailsforspecifclass(OWLClass cls, OWLOntology ontology){
        Set<OWLObjectProperty> dd = getObjectPropertyDomain(ontology,cls);
        JSONArray objectpropertyarray = new JSONArray();

        for (OWLObjectProperty dp : dd) {
            JSONObject objectproperty = new JSONObject();
            objectproperty.put("objectpropertyname", dp.getIRI().getShortForm());
            for (OWLObjectPropertyRangeAxiom dpra : ontology.getObjectPropertyRangeAxioms(dp)) {
                String dpraString = dpra.toString();
                if (dpraString.contains("#")) {
                    dpraString = dpraString.substring(
                            dpraString.indexOf("#",dpraString.indexOf("#")+1) + 1,
                            dpraString.lastIndexOf(">"));
                }
                objectproperty.put("rangeclassname", dpraString);
                for (OWLFunctionalObjectPropertyAxiom fdp : ontology.getFunctionalObjectPropertyAxioms(dp)) {
                    String fdpString = fdp.toString();
                    if (fdpString.contains("#")) {
                        fdpString = fdpString.substring(
                                fdpString.indexOf("#") + 1,
                                fdpString.lastIndexOf(">"));
                    }
                    if (fdpString.equals(dp.getIRI().getShortForm())) {
                        objectproperty.put("isfunctional", true);
                    }
                    //System.out.println(fdp.getAxiomType().compareTo(AxiomType.FUNCTIONAL_DATA_PROPERTY));
                }
            }
            for(OWLInverseObjectPropertiesAxiom iop : ontology.getInverseObjectPropertyAxioms(dp)){
                String iopString = iop.toString();
                if (iopString.contains("#")) {
                    iopString = iopString.substring(
                            iopString.indexOf("#",iopString.indexOf("#")+1) + 1,
                            iopString.lastIndexOf(">"));
                }
                objectproperty.put("inverseobjectproperty", iopString);

                if(iop.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)){
                    objectproperty.put("inverseobjectpropertyisfunctional", true);
                }
            }
            objectpropertyarray.add(objectproperty);
            //for(OWLDataMinCardinality odpmc:ontology.)
        }
        return objectpropertyarray;
    }
    private static JSONArray getobjectpropertyrangedetailsforspecifclass(OWLClass cls, OWLOntology ontology) {
        Set<OWLObjectProperty> dd = getObjectPropertyRange(ontology, cls);
        JSONArray objectpropertyarray = new JSONArray();

        for (OWLObjectProperty dp : dd) {
            JSONObject objectproperty = new JSONObject();
            objectproperty.put("objectpropertyname", dp.getIRI().getShortForm());
            for (OWLFunctionalObjectPropertyAxiom fdp : ontology.getFunctionalObjectPropertyAxioms(dp)) {
                String fdpString = fdp.toString();
                if (fdpString.contains("#")) {
                    fdpString = fdpString.substring(
                            fdpString.indexOf("#") + 1,
                            fdpString.lastIndexOf(">"));
                }
                if (fdpString.equals(dp.getIRI().getShortForm())) {
                    objectproperty.put("isfunctional", true);
                }
                //System.out.println(fdp.getAxiomType().compareTo(AxiomType.FUNCTIONAL_DATA_PROPERTY));
            }
            for(OWLInverseObjectPropertiesAxiom iop : ontology.getInverseObjectPropertyAxioms(dp)){
                String iopString = iop.toString();
                if (iopString.contains("#")) {
                    iopString = iopString.substring(
                            iopString.indexOf("#",iopString.indexOf("#")+1) + 1,
                            iopString.lastIndexOf(">"));
                }
                objectproperty.put("inverseobjectproperty", iopString);

                if(iop.isOfType(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY)){
                    objectproperty.put("inverseobjectpropertyisfunctional", true);
                }
            }
            objectpropertyarray.add(objectproperty);
        }
        return objectpropertyarray;
    }
    private static JSONArray getdataproprtydetailsforspecificlass(OWLClass cls,OWLOntology ontology){
        Set<OWLDataProperty> dd = getDataPropertyDomain(ontology,cls);
        JSONArray datapropertyarray = new JSONArray();

            for (OWLDataProperty dp : dd) {
                JSONObject dataproperty = new JSONObject();
                dataproperty.put("datapropertyname", dp.getIRI().getShortForm());
                for (OWLDataPropertyRangeAxiom dpra : ontology.getDataPropertyRangeAxioms(dp)) {
                    String dpraString = dpra.toString();
                    dataproperty.put("datatype", dpra.getRange().asOWLDatatype().getIRI().getShortForm());
                    if (dpraString.contains("#")) {
                        dpraString = dpraString.substring(
                                dpraString.indexOf("#") + 1,
                                dpraString.lastIndexOf(">"));
                    }
                    for (OWLFunctionalDataPropertyAxiom fdp : ontology.getFunctionalDataPropertyAxioms(dp)) {
                        String fdpString = fdp.toString();
                        if (fdpString.contains("#")) {
                            fdpString = fdpString.substring(
                                    fdpString.indexOf("#") + 1,
                                    fdpString.lastIndexOf(">"));
                        }
                        if (fdpString.equals(dpraString)) {
                            dataproperty.put("isfunctional", true);
                        }
                        //System.out.println(fdp.getAxiomType().compareTo(AxiomType.FUNCTIONAL_DATA_PROPERTY));
                    }
                }
                datapropertyarray.add(dataproperty);
                //for(OWLDataMinCardinality odpmc:ontology.)
            }
        return datapropertyarray;

    }
    private static JSONArray createDataBaseTables(Set<OWLClass> classes,OWLOntology ontology){

        JSONArray classarray = new JSONArray();


        for (OWLClass cls : classes) {
            if (cls.getIRI().getShortForm().equals("Crop")) {
                JSONObject ontoloyclassess = new JSONObject();
                JSONArray superclsarry = new JSONArray();
                ontoloyclassess.put("classname", cls.getIRI().getShortForm());
                Set<OWLClass> supperclasses = getSupperClasses(ontology, cls);
                int count = 0;
                for (OWLClass suppercls : supperclasses) {
                    //System.out.println("\t\t"+suppercls.getIRI().getShortForm());
                    superclsarry.add(suppercls.getIRI().getShortForm());
                }
                ontoloyclassess.put("supperclasses", superclsarry);
                ontoloyclassess.put("dataproperties", getdataproprtydetailsforspecificlass(cls, ontology));
                //System.out.println("\tDataproperties:");
                ontoloyclassess.put("objectproperties", getobjectpropertydetailsforspecifclass(cls, ontology));
                ontoloyclassess.put("objectpropertyrange", getobjectpropertyrangedetailsforspecifclass(cls, ontology));
                ontoloyclassess.put("datapropertyrestrictions", getdatapropertyclassaxioms(cls, ontology));
                getobjectpropertyclassaxioms(cls, ontology);
                classarray.add(ontoloyclassess);
            }
        }
        System.out.println(classarray);
        return classarray;
    }
    private static JSONObject getdatapropertyclassaxioms(OWLClass cls,OWLOntology ontology){
        Set<OWLClassAxiom> tempAx=ontology.getAxioms(cls);
        JSONObject datapropertyrestrictions = new JSONObject();
        JSONArray data_has_value = new JSONArray();
        JSONArray data_max_cardinality = new JSONArray();
        JSONArray data_all_values_from = new JSONArray();
        JSONArray data_exact_cardinality = new JSONArray();
        JSONArray data_min_cardinality = new JSONArray();
        JSONArray data_some_values_from = new JSONArray();

        for(OWLClassAxiom ax: tempAx){
            for(OWLClassExpression nce:ax.getNestedClassExpressions()) {
                DataRestrictionVisitor datarestrictionVisitor = new DataRestrictionVisitor(Collections.singleton(ontology));
                nce.accept(datarestrictionVisitor);
                for(OWLDataHasValue dmc:datarestrictionVisitor.getHasValueProperties()){
                    JSONObject hasdatavalueobj = new JSONObject();
                    hasdatavalueobj.put("propertyname",dmc.getProperty().asOWLDataProperty().getIRI().getShortForm());
                    hasdatavalueobj.put("literalvalue",dmc.getFiller().getLiteral());
                    hasdatavalueobj.put("datatype",dmc.getFiller().getDatatype().getIRI().getShortForm());
                    data_has_value.add(hasdatavalueobj);
                }
                for(OWLDataMaxCardinality dmc:datarestrictionVisitor.getMaxCardinalityProperties()){
                    data_max_cardinality.add(getMaxCardinalityDetails(dmc,ontology));
                    //System.out.println(data_max_cardinality);
                }
                for(OWLDataAllValuesFrom dmc:datarestrictionVisitor.getAllValuesFromProperties()){
                    data_all_values_from.add(getAllValuesFromsDetails(dmc,ontology));
                    //System.out.println(data_all_values_from);
                    //System.out.println(getSomeValuesFromsDetails(dmc,ontology));
                }
                for(OWLDataExactCardinality dmc:datarestrictionVisitor.getExactCardinalityProperties()){
                    data_exact_cardinality.add(getExactCardinalityDetails(dmc,ontology));
                    //System.out.println(data_exact_cardinality);
                }
                for(OWLDataMinCardinality dmc:datarestrictionVisitor.getMinCardinalityProperties()){
                    data_min_cardinality.add(getMinCardinalityDetails(dmc,ontology));
                    //System.out.println(data_min_cardinality);
                }
                for(OWLDataSomeValuesFrom dmc:datarestrictionVisitor.getSomeValuesFromsProperties()){
                    data_some_values_from.add(getSomeValuesFromsDetails(dmc,ontology));
                    //System.out.println(getSomeValuesFromsDetails(dmc,ontology));
                }
            }
        }
        datapropertyrestrictions.put("data_has_value",data_has_value);
        datapropertyrestrictions.put("data_max_cardinality",data_max_cardinality);
        datapropertyrestrictions.put("data_all_values_from",data_all_values_from);
        datapropertyrestrictions.put("data_exact_cardinality",data_exact_cardinality);
        datapropertyrestrictions.put("data_min_cardinality",data_min_cardinality);
        datapropertyrestrictions.put("data_some_values_from",data_some_values_from);
        return datapropertyrestrictions;
    }
    private static JSONObject getMaxCardinalityDetails(OWLDataMaxCardinality dmc,OWLOntology ontology){
        JSONObject hasdatavalueobj = new JSONObject();
        JSONArray facets = new JSONArray();
        String propertyname = dmc.getProperty().asOWLDataProperty().getIRI().getShortForm();
        int cardinalityvalue = dmc.getCardinality();
        String datatype = "";
        DataRangeRestrictionVisitor dataRangerestrictionvisitor = new DataRangeRestrictionVisitor(Collections.singleton(ontology));
        dmc.getFiller().accept(dataRangerestrictionvisitor);
        if(dmc.getFiller().isOWLDatatype()) {
            datatype = dmc.getFiller().asOWLDatatype().toString().split(":")[1];
        }
        else{
            for(OWLDatatypeRestriction gg:dataRangerestrictionvisitor.getRestrictedProperties()){
                datatype = gg.getDatatype().toString().split(":")[1];
                break;
            }
        }
        hasdatavalueobj.put("propertyname",propertyname);
        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
        hasdatavalueobj.put("datatype", datatype);
        hasdatavalueobj.put("facetdetails",getDatatypeRestrictions(dataRangerestrictionvisitor.getRestrictedProperties()));
        return hasdatavalueobj;
    }
    private static JSONObject getMinCardinalityDetails(OWLDataMinCardinality dminc,OWLOntology ontology){
        JSONArray facets = new JSONArray();
        JSONObject hasdatavalueobj = new JSONObject();
        String propertyname = dminc.getProperty().asOWLDataProperty().getIRI().getShortForm();
        int mincardinalityvalue = dminc.getCardinality();
        String datatype = "";
        DataRangeRestrictionVisitor dataRangerestrictionvisitor = new DataRangeRestrictionVisitor(Collections.singleton(ontology));
        dminc.getFiller().accept(dataRangerestrictionvisitor);
        if(dminc.getFiller().isOWLDatatype()) {
            datatype = dminc.getFiller().asOWLDatatype().toString().split(":")[1];
        }
        else{
            for(OWLDatatypeRestriction gg:dataRangerestrictionvisitor.getRestrictedProperties()){
                datatype = gg.getDatatype().toString().split(":")[1];
                break;
            }
        }
        hasdatavalueobj.put("propertyname",propertyname);
        hasdatavalueobj.put("cardinalityvalue",mincardinalityvalue);
        hasdatavalueobj.put("datatype", datatype);
        hasdatavalueobj.put("facetdetails",getDatatypeRestrictions(dataRangerestrictionvisitor.getRestrictedProperties()));
        return hasdatavalueobj;
    }
    private static JSONObject getExactCardinalityDetails(OWLDataExactCardinality dminc,OWLOntology ontology){
        JSONArray facets = new JSONArray();
        JSONObject hasdatavalueobj = new JSONObject();
        int exactcardinalityvalue = dminc.getCardinality();
        String propertyname = dminc.getProperty().asOWLDataProperty().getIRI().getShortForm();
        String datatype = "";
        DataRangeRestrictionVisitor dataRangerestrictionvisitor = new DataRangeRestrictionVisitor(Collections.singleton(ontology));
        dminc.getFiller().accept(dataRangerestrictionvisitor);
        if(dminc.getFiller().isOWLDatatype()) {
            datatype = dminc.getFiller().asOWLDatatype().toString().split(":")[1];
        }
        else{
            for(OWLDatatypeRestriction gg:dataRangerestrictionvisitor.getRestrictedProperties()){
                datatype = gg.getDatatype().toString().split(":")[1];
                break;
            }
        }
        hasdatavalueobj.put("propertyname",propertyname);
        hasdatavalueobj.put("cardinalityvalue",exactcardinalityvalue);
        hasdatavalueobj.put("datatype", datatype);
        hasdatavalueobj.put("facetdetails",getDatatypeRestrictions(dataRangerestrictionvisitor.getRestrictedProperties()));
        return hasdatavalueobj;
    }
    private static JSONObject getSomeValuesFromsDetails(OWLDataSomeValuesFrom dmc,OWLOntology ontology){
        JSONObject hasdatavalueobj = new JSONObject();
        JSONArray facets = new JSONArray();
        String propertyname = dmc.getProperty().asOWLDataProperty().getIRI().getShortForm();
        String datatype = "";
        DataRangeRestrictionVisitor dataRangerestrictionvisitor = new DataRangeRestrictionVisitor(Collections.singleton(ontology));
        dmc.getFiller().accept(dataRangerestrictionvisitor);
        if(dmc.getFiller().isOWLDatatype()) {
            datatype = dmc.getFiller().asOWLDatatype().toString().split(":")[1];
        }
        else{
            for(OWLDatatypeRestriction gg:dataRangerestrictionvisitor.getRestrictedProperties()){
                datatype = gg.getDatatype().toString().split(":")[1];
                break;
            }
        }
        hasdatavalueobj.put("propertyname",propertyname);
        hasdatavalueobj.put("datatype", datatype);
        hasdatavalueobj.put("facetdetails",getDatatypeRestrictions(dataRangerestrictionvisitor.getRestrictedProperties()));
        return hasdatavalueobj;
    }
    private static JSONObject getAllValuesFromsDetails(OWLDataAllValuesFrom dmc,OWLOntology ontology){
        JSONArray facets = new JSONArray();
        JSONObject hasdatavalueobj = new JSONObject();
        String propertyname = dmc.getProperty().asOWLDataProperty().getIRI().getShortForm();
        String datatype = "";
        DataRangeRestrictionVisitor dataRangerestrictionvisitor = new DataRangeRestrictionVisitor(Collections.singleton(ontology));
        dmc.getFiller().accept(dataRangerestrictionvisitor);
        if(dmc.getFiller().isOWLDatatype()) {
            datatype = dmc.getFiller().asOWLDatatype().toString().split(":")[1];
        }
        else{
            for(OWLDatatypeRestriction gg:dataRangerestrictionvisitor.getRestrictedProperties()){
                datatype = gg.getDatatype().toString().split(":")[1];
                break;
            }
        }
        hasdatavalueobj.put("propertyname",propertyname);
        hasdatavalueobj.put("datatype", datatype);
        hasdatavalueobj.put("facetdetails",getDatatypeRestrictions(dataRangerestrictionvisitor.getRestrictedProperties()));
        return hasdatavalueobj;
    }
    private static JSONArray getDatatypeRestrictions(Set<OWLDatatypeRestriction> dtrarray){
        JSONArray facets = new JSONArray();
        for(OWLDatatypeRestriction dtr:dtrarray){
            for (OWLFacetRestriction hh:dtr.getFacetRestrictions()){
                JSONObject facet = new JSONObject();
                facet.put("facetname",hh.getFacet().getShortForm());
                facet.put("facetvalue",hh.getFacetValue().getLiteral());
                facets.add(facet);
            }
        }
        return facets;
    }
    private static void getobjectpropertyclassaxioms(OWLClass cls,OWLOntology ontology) {
        Set<OWLClassAxiom> tempAx = ontology.getAxioms(cls);
        JSONObject objectropertyrestrictions = new JSONObject();
        JSONArray object_has_value = new JSONArray();
        JSONArray object_max_cardinality = new JSONArray();
        JSONArray object_all_values_from = new JSONArray();
        JSONArray object_exact_cardinality = new JSONArray();
        JSONArray object_min_cardinality = new JSONArray();
        JSONArray object_some_values_from = new JSONArray();

        for (OWLClassAxiom ax : tempAx) {
            for (OWLClassExpression nce : ax.getNestedClassExpressions()) {
                ObjectRestrictionVisitor objectrestrictionVisitor = new ObjectRestrictionVisitor(Collections.singleton(ontology));
                nce.accept(objectrestrictionVisitor);
                for(OWLObjectHasValue dmc:objectrestrictionVisitor.getHasValueProperties()){
                   // System.out.println(dmc.toString());
                    ///dmc.getFiller().
                }
                for(OWLObjectExactCardinality dmc: objectrestrictionVisitor.getExactCardinalityProperties()){
                    //System.out.println(dmc.toString());
                }
            }
        }
    }

    public JSONArray getClassArray(){
        return this.array;
    }
}
