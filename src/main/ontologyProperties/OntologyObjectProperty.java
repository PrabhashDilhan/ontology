import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Prabhash Dilhan on 8/16/2017.
 */
public class OntologyObjectProperty {
    public static void main(String[] args){
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology localPizza = loadedOntology.getOwlOntologyManager();
        Set<OWLObjectProperty> axioms = localPizza.getObjectPropertiesInSignature();
        Iterator<OWLObjectProperty> itr = axioms.iterator();
        while(itr.hasNext()){
            OWLObjectProperty cc = itr.next();
            System.out.println(cc.getIRI().getShortForm());

        }
    }
}
