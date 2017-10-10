import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Prabhash Dilhan on 8/16/2017.
 */
public class OntologyDataProperty{
    public static void main(String[] args){
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology localPizza = loadedOntology.getOwlOntologyManager();
        Set<OWLDataProperty> axioms = localPizza.getDataPropertiesInSignature();
        Iterator<OWLDataProperty> itr = axioms.iterator();
        while(itr.hasNext()){
            OWLDataProperty cc = itr.next();
            System.out.println(cc.getIRI().getShortForm());

        }
    }
}
