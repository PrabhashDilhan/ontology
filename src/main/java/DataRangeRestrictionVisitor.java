import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

public  class DataRangeRestrictionVisitor implements OWLDataRangeVisitor {

    private boolean processInherited = true;

    private Set<OWLClass> processedClasses;

    private Set<OWLDatatypeRestriction> restrictedProperties;

    private Set<OWLOntology> onts;

    public DataRangeRestrictionVisitor(Set<OWLOntology> onts) {
        restrictedProperties = new HashSet<OWLDatatypeRestriction>();
        processedClasses = new HashSet<OWLClass>();
        this.onts = onts;
    }


    public void setProcessInherited(boolean processInherited) {
        this.processInherited = processInherited;
    }


    public Set<OWLDatatypeRestriction> getRestrictedProperties() {
        return restrictedProperties;
    }

    public void reset() {
        processedClasses.clear();
        restrictedProperties.clear();
    }

    public void visit(OWLDatatypeRestriction node) {
        restrictedProperties.add(node);
    }
}
