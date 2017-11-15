import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;

/**
 * Created by Prabhash Dilhan on 7/10/2017.
 */
public class LoadOntology {

        // Get hold of an ontology manager

            private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File("F:\\pizzaowl\\pizza\\pizzanew.owl");




            public OWLOntology getOwlOntologyManager(){
                OWLOntology ontology = null;
                try {
                    ontology = manager.loadOntologyFromOntologyDocument(file);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
                return ontology;
            }


}
