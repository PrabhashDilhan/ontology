import org.semanticweb.owlapi.model.*;

import java.util.Collections;
import java.util.Set;

public class OWLRestrictionVisitor {


    public static void main(String[] args) {
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology ontology = loadedOntology.getOwlOntologyManager();
        Set<OWLClass> classes = ontology.getClassesInSignature();
        for(OWLClass cls:classes) {
            DataRestrictionVisitor restrictionVisitor = new DataRestrictionVisitor(Collections.singleton(ontology));
            Set<OWLClassAxiom> tempAx=ontology.getAxioms(cls);
            for(OWLClassAxiom ax: tempAx) {
                for (OWLClassExpression nce : ax.getNestedClassExpressions()) {
                    nce.accept(restrictionVisitor);
                }
            }
            System.out.println("Restricted properties for " + cls + ": " + restrictionVisitor.getMaxCardinalityProperties().size());
            for (OWLDataMaxCardinality prop : restrictionVisitor.getMaxCardinalityProperties()) {
                System.out.println("    " + prop);
                System.out.println("    cardinalityValue:" + prop.getCardinality());
                DataRangeRestrictionVisitor dataRangerestrictionvisitor = new DataRangeRestrictionVisitor(Collections.singleton(ontology));
                prop.getFiller().accept(dataRangerestrictionvisitor);
                for(OWLDatatypeRestriction dtr:dataRangerestrictionvisitor.getRestrictedProperties()){
                    System.out.println("    Datatype:"+dtr.getDatatype());
                    for (OWLFacetRestriction hh:dtr.getFacetRestrictions()){
                        System.out.println("    facet:"+hh.getFacet());
                        System.out.println("    fecetValue:"+hh.getFacetValue().getLiteral());
                    }
                }
            }
        }
    }

}
