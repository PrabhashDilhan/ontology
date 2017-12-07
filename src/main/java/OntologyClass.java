import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


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
        OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(ontology);
        Set<OWLClass> cc = new HashSet<OWLClass>();
        for(OWLClass subclass:reasoner.getSuperClasses(dd,true).getFlattened()){
            cc.add(subclass);
        }
        return cc;
    }
    private static Set<OWLObjectProperty>  getObjectPropertyDomain(OWLOntology ontology, OWLClass dd){

        Set<OWLObjectProperty> ff = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
            if (op.getDomain().equals(dd)) {
                for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
                    ff.add(oop);
                }
            }
        }
        return ff;
    }
    private static Set<OWLDataProperty>  getDataPropertyDomain(OWLOntology ontology, OWLClass dd){
        Set<OWLDataProperty> ff = new HashSet<OWLDataProperty>();
        for (OWLDataPropertyDomainAxiom dp : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
            if (dp.getDomain().equals(dd)){
                for(OWLDataProperty odp : dp.getDataPropertiesInSignature()){
                    ff.add(odp);
                }
            }
        }
        return ff;
    }
    private static Set<OWLObjectProperty>  getObjectPropertyRange(OWLOntology ontology, OWLClass dd){
        Set<OWLObjectProperty> ff = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyRangeAxiom dp : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
            if (dp.getRange().equals(dd)){
                for(OWLObjectProperty odp : dp.getObjectPropertiesInSignature()){
                    ff.add(odp);
                }
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
                    System.out.println();
                    if (fdpString.contains("#")) {
                        fdpString = fdpString.substring(
                                fdpString.indexOf("#") + 1,
                                fdpString.lastIndexOf(">"));
                    }
                    if (fdpString.equals(dp.getIRI().getShortForm())) {
                        objectproperty.put("isfunctional", true);
                    }
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
                    System.out.println();
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
                    System.out.println();
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
                    }
                }
                datapropertyarray.add(dataproperty);
            }
        return datapropertyarray;

    }
    private static JSONArray createDataBaseTables(Set<OWLClass> classes,OWLOntology ontology){

        JSONArray classarray = new JSONArray();


        for (OWLClass cls : classes) {
            if (cls.getIRI().getShortForm().equals("MeatyPizza")) {
                JSONObject ontoloyclassess = new JSONObject();
                JSONArray superclsarry = new JSONArray();
                ontoloyclassess.put("classname", cls.getIRI().getShortForm());
                Set<OWLClass> supperclasses = getSupperClasses(ontology, cls);
                int count = 0;
                for (OWLClass suppercls : supperclasses) {
                    superclsarry.add(suppercls.getIRI().getShortForm());
                }
                ontoloyclassess.put("supperclasses", superclsarry);
                ontoloyclassess.put("dataproperties", getdataproprtydetailsforspecificlass(cls, ontology));
                ontoloyclassess.put("objectproperties", getobjectpropertydetailsforspecifclass(cls, ontology));
                ontoloyclassess.put("objectpropertyrange", getobjectpropertyrangedetailsforspecifclass(cls, ontology));
                ontoloyclassess.put("datapropertyrestrictions", getdatapropertyclassaxioms(cls, ontology));
                ontoloyclassess.put("objectpropertyrestrictions",getobjectpropertyclassaxioms(cls,ontology));
                getobjectpropertyclassaxioms(cls, ontology);
                classarray.add(ontoloyclassess);
            }
        }
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
                }
                for(OWLDataAllValuesFrom dmc:datarestrictionVisitor.getAllValuesFromProperties()){
                    data_all_values_from.add(getAllValuesFromsDetails(dmc,ontology));
                }
                for(OWLDataExactCardinality dmc:datarestrictionVisitor.getExactCardinalityProperties()){
                    data_exact_cardinality.add(getExactCardinalityDetails(dmc,ontology));
                }
                for(OWLDataMinCardinality dmc:datarestrictionVisitor.getMinCardinalityProperties()){
                    data_min_cardinality.add(getMinCardinalityDetails(dmc,ontology));
                }
                for(OWLDataSomeValuesFrom dmc:datarestrictionVisitor.getSomeValuesFromsProperties()){
                    data_some_values_from.add(getSomeValuesFromsDetails(dmc,ontology));
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
    private static JSONObject getobjectpropertyclassaxioms(OWLClass cls,OWLOntology ontology) {
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
                    String propertyname = dmc.getProperty().asOWLObjectProperty().getIRI().getShortForm();
                    JSONObject hasdatavalueobj = new JSONObject();
                    hasdatavalueobj.put("propertyname",propertyname);
                    JSONArray fillerclasses = new JSONArray();
                    String individualname = dmc.getFiller().asOWLNamedIndividual().getIRI().getShortForm();
                    Set<Object> hh = EntitySearcher.getTypes(dmc.getFiller().asOWLNamedIndividual(), ontology).collect(Collectors.toSet());
                    for (Object gg:hh){
                        OWLClassExpression kk = (OWLClassExpression)gg;
                        fillerclasses.add(kk.asOWLClass().getIRI().getShortForm());
                    }
                    hasdatavalueobj.put("invidualname",individualname);
                    hasdatavalueobj.put("fillerclasses",fillerclasses);
                    object_has_value.add(hasdatavalueobj);
                }
                for(OWLObjectExactCardinality oec: objectrestrictionVisitor.getExactCardinalityProperties()){
                    String propertyname = oec.getProperty().asOWLObjectProperty().getIRI().getShortForm();
                    if(!oec.getFiller().isOWLClass()) {
                        int cardinalityvalue = oec.getCardinality();
                        JSONArray intersection = new JSONArray();
                        JSONArray union = new JSONArray();
                        JSONObject hasdatavalueobj = new JSONObject();
                        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
                        hasdatavalueobj.put("propertyname",propertyname);
                        getUnionAndIntersection(oec.getFiller(),ontology, hasdatavalueobj, union,intersection);
                        hasdatavalueobj.put("union",union);
                        hasdatavalueobj.put("intersection",intersection);
                        object_exact_cardinality.add(hasdatavalueobj);
                    }
                    else {
                        int cardinalityvalue = oec.getCardinality();
                        String fillerclass = oec.getFiller().asOWLClass().getIRI().getShortForm();
                        JSONObject hasdatavalueobj = new JSONObject();
                        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
                        hasdatavalueobj.put("propertyname",propertyname);
                        hasdatavalueobj.put("fillerclass",fillerclass);
                        object_exact_cardinality.add(hasdatavalueobj);
                    }
                }
                for(OWLObjectMinCardinality omic: objectrestrictionVisitor.getMinCardinalityProperties()){
                    String propertyname = omic.getProperty().asOWLObjectProperty().getIRI().getShortForm();
                    if(!omic.getFiller().isOWLClass()) {
                        JSONObject hasdatavalueobj = new JSONObject();
                        JSONArray intersection = new JSONArray();
                        int cardinalityvalue = omic.getCardinality();
                        JSONArray union = new JSONArray();
                        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
                        hasdatavalueobj.put("propertyname",propertyname);
                        getUnionAndIntersection(omic.getFiller(),ontology, hasdatavalueobj, union,intersection);
                        hasdatavalueobj.put("union",union);
                        hasdatavalueobj.put("intersection",intersection);
                        object_min_cardinality.add(hasdatavalueobj);
                    }
                    else {
                        String fillerclass = omic.getFiller().asOWLClass().getIRI().getShortForm();
                        int cardinalityvalue = omic.getCardinality();
                        JSONObject hasdatavalueobj = new JSONObject();
                        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
                        hasdatavalueobj.put("propertyname",propertyname);
                        hasdatavalueobj.put("fillerclass",fillerclass);
                        object_min_cardinality.add(hasdatavalueobj);
                    }
                }
                for(OWLObjectMaxCardinality omac: objectrestrictionVisitor.getMaxCardinalityProperties()){
                    String propertyname = omac.getProperty().asOWLObjectProperty().getIRI().getShortForm();
                    if(!omac.getFiller().isOWLClass()) {
                        JSONArray union = new JSONArray();
                        int cardinalityvalue = omac.getCardinality();
                        JSONArray intersection = new JSONArray();
                        JSONObject hasdatavalueobj = new JSONObject();
                        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
                        hasdatavalueobj.put("propertyname",propertyname);
                        getUnionAndIntersection(omac.getFiller(),ontology, hasdatavalueobj, union,intersection);
                        hasdatavalueobj.put("union",union);
                        hasdatavalueobj.put("intersection",intersection);
                        object_max_cardinality.add(hasdatavalueobj);
                    }
                    else {
                        JSONObject hasdatavalueobj = new JSONObject();
                        int cardinalityvalue = omac.getCardinality();
                        String fillerclass = omac.getFiller().asOWLClass().getIRI().getShortForm();
                        hasdatavalueobj.put("cardinalityvalue",cardinalityvalue);
                        hasdatavalueobj.put("propertyname",propertyname);
                        hasdatavalueobj.put("fillerclass",fillerclass);
                        object_max_cardinality.add(hasdatavalueobj);
                    }
                }
                for(OWLObjectAllValuesFrom oav: objectrestrictionVisitor.getAllValuesFromProperties()){
                    String propertyname = oav.getProperty().asOWLObjectProperty().getIRI().getShortForm();
                    if(!oav.getFiller().isOWLClass()) {
                        JSONObject hasdatavalueobj = new JSONObject();
                        JSONArray union = new JSONArray();
                        JSONArray intersection = new JSONArray();
                        hasdatavalueobj.put("propertyname",propertyname);
                        getUnionAndIntersection(oav.getFiller(),ontology, hasdatavalueobj, union,intersection);
                        hasdatavalueobj.put("union",union);
                        hasdatavalueobj.put("intersection",intersection);
                        object_all_values_from.add(hasdatavalueobj);
                    }
                    else {
                        JSONObject hasdatavalueobj = new JSONObject();
                        String fillerclass = oav.getFiller().asOWLClass().getIRI().getShortForm();
                        hasdatavalueobj.put("propertyname",propertyname);
                        hasdatavalueobj.put("fillerclass",fillerclass);
                        object_all_values_from.add(hasdatavalueobj);
                    }
                }
                for(OWLObjectSomeValuesFrom osv: objectrestrictionVisitor.getSomeValuesFromsProperties()){
                    String propertyname = osv.getProperty().asOWLObjectProperty().getIRI().getShortForm();
                    if(!osv.getFiller().isOWLClass()) {
                        JSONArray union = new JSONArray();
                        JSONArray intersection = new JSONArray();
                        JSONObject hasdatavalueobj = new JSONObject();
                        hasdatavalueobj.put("propertyname",propertyname);
                        getUnionAndIntersection(osv.getFiller(),ontology, hasdatavalueobj, union,intersection);
                        hasdatavalueobj.put("union",union);
                        hasdatavalueobj.put("intersection",intersection);
                        object_some_values_from.add(hasdatavalueobj);
                    }
                    else {
                        String fillerclass = osv.getFiller().asOWLClass().getIRI().getShortForm();
                        JSONObject hasdatavalueobj = new JSONObject();
                        hasdatavalueobj.put("propertyname",propertyname);
                        hasdatavalueobj.put("fillerclass",fillerclass);
                        object_some_values_from.add(hasdatavalueobj);
                    }
                }
            }
        }
        objectropertyrestrictions.put("object_has_value",object_has_value);
        objectropertyrestrictions.put("object_max_cardinality",object_max_cardinality);
        objectropertyrestrictions.put("object_all_values_from",object_all_values_from);
        objectropertyrestrictions.put("object_exact_cardinality",object_exact_cardinality);
        objectropertyrestrictions.put("object_min_cardinality",object_min_cardinality);
        objectropertyrestrictions.put("object_some_values_from",object_some_values_from);
        System.out.println(objectropertyrestrictions);
        return objectropertyrestrictions;
    }
    public static void getUnionAndIntersection(OWLClassExpression oav, OWLOntology ontology, JSONObject hasdatavalueobj,JSONArray union, JSONArray intersection){
        if(!oav.isOWLClass()) {
            ObjectRestrictionVisitor objrestrictionVisitor = new ObjectRestrictionVisitor(Collections.singleton(ontology));
            oav.accept(objrestrictionVisitor);
            if (!objrestrictionVisitor.getUnionOfProperties().isEmpty()) {
                for (OWLObjectUnionOf gg : objrestrictionVisitor.getUnionOfProperties()) {
                    for (OWLClassExpression ff : gg.asDisjunctSet()) {
                        if (ff.isOWLClass()) {
                            union.add(ff.asOWLClass().getIRI().getShortForm());
                        } else {
                            getUnionAndIntersection(ff, ontology,hasdatavalueobj, union,intersection);
                        }
                    }
                }
            }
            if (!objrestrictionVisitor.getIntersectionOfProperties().isEmpty()) {
                for (OWLObjectIntersectionOf gg : objrestrictionVisitor.getIntersectionOfProperties()) {
                    for (OWLClassExpression ff : gg.asConjunctSet()) {
                        if (ff.isOWLClass()) {
                            intersection.add(ff.asOWLClass().getIRI().getShortForm());
                        } else {
                            getUnionAndIntersection(ff, ontology,hasdatavalueobj, union,intersection);
                        }
                    }
                }
            }
        }else {
            System.out.println(oav.asOWLClass().getIRI().getShortForm());
        }
    }

    public JSONArray getClassArray(){
        return this.array;
    }
}
