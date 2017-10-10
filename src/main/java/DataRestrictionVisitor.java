import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

public class DataRestrictionVisitor implements OWLClassExpressionVisitor {

    private boolean processInherited = true;

    private Set<OWLClass> processedClasses;

    private Set<OWLDataMaxCardinality> maxCardinalityProperties;

    private Set<OWLDataMinCardinality> minCardinalityProperties;

    private Set<OWLDataHasValue> hasValueProperties;

    private Set<OWLDataExactCardinality> exactCardinalityProperties;

    private Set<OWLDataAllValuesFrom> allValuesFromProperties;

    private Set<OWLDataSomeValuesFrom> someValuesFromsProperties;

    private Set<OWLOntology> onts;

    public DataRestrictionVisitor(Set<OWLOntology> onts) {
        maxCardinalityProperties = new HashSet<OWLDataMaxCardinality>();
        minCardinalityProperties = new HashSet<OWLDataMinCardinality>();
        hasValueProperties = new HashSet<OWLDataHasValue>();
        exactCardinalityProperties = new HashSet<OWLDataExactCardinality>();
        allValuesFromProperties = new HashSet<OWLDataAllValuesFrom>();
        someValuesFromsProperties = new HashSet<OWLDataSomeValuesFrom>();
        processedClasses = new HashSet<OWLClass>();
        this.onts = onts;
    }


    public void setProcessInherited(boolean processInherited) {
        this.processInherited = processInherited;
    }


    public Set<OWLDataMaxCardinality> getMaxCardinalityProperties() {
        return maxCardinalityProperties;
    }

    public Set<OWLDataMinCardinality> getMinCardinalityProperties() {
        return minCardinalityProperties;
    }

    public Set<OWLDataHasValue> getHasValueProperties() {
        return hasValueProperties;
    }

    public Set<OWLDataExactCardinality> getExactCardinalityProperties() {
        return exactCardinalityProperties;
    }

    public Set<OWLDataAllValuesFrom> getAllValuesFromProperties() {
        return allValuesFromProperties;
    }

    public Set<OWLDataSomeValuesFrom> getSomeValuesFromsProperties() {
        return someValuesFromsProperties;
    }

    public void visit(OWLClass desc) {
        if (processInherited && !processedClasses.contains(desc)) {
            // If we are processing inherited restrictions then
            // we recursively visit named supers.  Note that we
            // need to keep track of the classes that we have processed
            // so that we don't get caught out by cycles in the taxonomy
            processedClasses.add(desc);
            for (OWLOntology ont : onts) {
                for (OWLSubClassOfAxiom ax : ont.getSubClassAxiomsForSubClass(desc)) {
                    ax.getSuperClass().accept(this);
                }
            }
        }
    }

    public void reset() {
        processedClasses.clear();
        maxCardinalityProperties.clear();
    }

    public void visit(OWLDataMaxCardinality desc) {
        maxCardinalityProperties.add(desc);
    }
    public void visit(OWLDataMinCardinality desc){
        minCardinalityProperties.add(desc);
    }
    public void visit(OWLDataHasValue desc){
        hasValueProperties.add(desc);
    }
    public void visit(OWLDataExactCardinality desc){
        exactCardinalityProperties.add(desc);
    }
    public void visit(OWLDataAllValuesFrom desc){
        allValuesFromProperties.add(desc);
    }
    public void visit(OWLDataSomeValuesFrom desc){
        someValuesFromsProperties.add(desc);
    }
}
