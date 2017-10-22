import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

public class ObjectRestrictionVisitor implements OWLClassExpressionVisitor {

    private boolean processInherited = true;

    private Set<OWLClass> processedClasses;

    private Set<OWLObjectMaxCardinality> maxCardinalityProperties;

    private Set<OWLObjectMinCardinality> minCardinalityProperties;

    private Set<OWLObjectHasValue> hasValueProperties;

    private Set<OWLObjectExactCardinality> exactCardinalityProperties;

    private Set<OWLObjectAllValuesFrom> allValuesFromProperties;

    private Set<OWLObjectSomeValuesFrom> someValuesFromsProperties;

    private Set<OWLObjectComplementOf> complementOfProperties;

    private Set<OWLObjectHasSelf> hasSelfProperties;

    private Set<OWLObjectIntersectionOf> intersectionOfProperties;

    private Set<OWLObjectUnionOf> unionOfProperties;

    private Set<OWLObjectOneOf> oneOfProperties;

    private Set<OWLOntology> onts;

    public ObjectRestrictionVisitor(Set<OWLOntology> onts) {
        maxCardinalityProperties = new HashSet<OWLObjectMaxCardinality>();
        minCardinalityProperties = new HashSet<OWLObjectMinCardinality>();
        hasValueProperties = new HashSet<OWLObjectHasValue>();
        exactCardinalityProperties = new HashSet<OWLObjectExactCardinality>();
        allValuesFromProperties = new HashSet<OWLObjectAllValuesFrom>();
        someValuesFromsProperties = new HashSet<OWLObjectSomeValuesFrom>();
        complementOfProperties = new HashSet<OWLObjectComplementOf>();
        hasSelfProperties = new HashSet<OWLObjectHasSelf>();
        intersectionOfProperties = new HashSet<OWLObjectIntersectionOf>();
        unionOfProperties = new HashSet<OWLObjectUnionOf>();
        oneOfProperties = new HashSet<OWLObjectOneOf>();
        processedClasses = new HashSet<OWLClass>();
        this.onts = onts;
    }


    public void setProcessInherited(boolean processInherited) {
        this.processInherited = processInherited;
    }


    public Set<OWLObjectMaxCardinality> getMaxCardinalityProperties() {
        return maxCardinalityProperties;
    }

    public Set<OWLObjectMinCardinality> getMinCardinalityProperties() {
        return minCardinalityProperties;
    }

    public Set<OWLObjectHasValue> getHasValueProperties() {
        return hasValueProperties;
    }

    public Set<OWLObjectExactCardinality> getExactCardinalityProperties() {
        return exactCardinalityProperties;
    }

    public Set<OWLObjectAllValuesFrom> getAllValuesFromProperties() {
        return allValuesFromProperties;
    }

    public Set<OWLObjectSomeValuesFrom> getSomeValuesFromsProperties() {
        return someValuesFromsProperties;
    }

    public Set<OWLObjectComplementOf> getComplementOfProperties(){return complementOfProperties;}

    public Set<OWLObjectHasSelf> getHasSelfProperties(){return hasSelfProperties;}

    public Set<OWLObjectIntersectionOf> getIntersectionOfProperties(){return intersectionOfProperties;}

    public Set<OWLObjectUnionOf> getUnionOfProperties(){return unionOfProperties;}

    public Set<OWLObjectOneOf> getOneOfProperties(){return oneOfProperties;}

    public void visit(OWLClass desc) {
        if (processInherited && !processedClasses.contains(desc)) {
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

    public void visit(OWLObjectMaxCardinality desc) {
        maxCardinalityProperties.add(desc);
    }
    public void visit(OWLObjectMinCardinality desc){
        minCardinalityProperties.add(desc);
    }
    public void visit(OWLObjectHasValue desc){
        hasValueProperties.add(desc);
    }
    public void visit(OWLObjectExactCardinality desc){
        exactCardinalityProperties.add(desc);
    }
    public void visit(OWLObjectAllValuesFrom desc){
        allValuesFromProperties.add(desc);
    }
    public void visit(OWLObjectSomeValuesFrom desc){
        someValuesFromsProperties.add(desc);
    }
    public void visit(OWLObjectComplementOf desc){complementOfProperties.add(desc);}
    public void visit(OWLObjectHasSelf desc){hasSelfProperties.add(desc);}
    public void visit(OWLObjectUnionOf desc){unionOfProperties.add(desc);}
    public void visit(OWLObjectIntersectionOf desc){intersectionOfProperties.add(desc);}
    public void visit(OWLObjectOneOf desc){oneOfProperties.add(desc);}
}
