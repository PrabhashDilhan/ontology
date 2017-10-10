import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Prabhash Dilhan on 8/17/2017.
 */
public class NeoDatabase {
    public static void main(String[] args){
        LoadOntology loadedOntology = new LoadOntology();
        OWLOntology ontology = loadedOntology.getOwlOntologyManager();
        Configuration config=new Configuration();
        // Lets make HermiT show information about the tableau for each reasoning task at the
        // start and end of a task and in certain time intervals.
        //config.tableauMonitorType= Configuration.TableauMonitorType.TIMING;
        OWLReasoner reasoner = new Reasoner(config,ontology);
        if (!reasoner.isConsistent()) {
            System.out.println("ontology is inconsistnt");
            //throw your exception of choice here
            //throw new Exception("Ontology is inconsistent");
        }
        File dbDir = new File("F:\\noe4jdatabase\\ontoloy");
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( dbDir );
        //registerShutdownHook( db );

        Transaction tx = db.beginTx();
        try {
            Node thingNode = getOrCreateNodeWithUniqueFactory("owl:Thing",db);
            for (OWLClass c :ontology.getClassesInSignature(true)) {
                String classString = c.toString();
                if (classString.contains("#")) {
                    classString = classString.substring(
                            classString.indexOf("#")+1,classString.lastIndexOf(">"));
                }
                Node classNode = getOrCreateNodeWithUniqueFactory(classString,db);
                NodeSet<OWLClass> superclasses = reasoner.getSuperClasses(c, true);
                if (superclasses.isEmpty()) {
                    classNode.createRelationshipTo(thingNode,
                            DynamicRelationshipType.withName("isA"));
                } else {
                    for (org.semanticweb.owlapi.reasoner.Node<OWLClass>
                            parentOWLNode: superclasses) {
                        OWLClassExpression parent =
                                parentOWLNode.getRepresentativeElement();
                        String parentString = parent.toString();
                        if (parentString.contains("#")) {
                            parentString = parentString.substring(
                                    parentString.indexOf("#")+1,
                                    parentString.lastIndexOf(">"));
                        }
                        Node parentNode =
                                getOrCreateNodeWithUniqueFactory(parentString,db);
                        classNode.createRelationshipTo(parentNode,
                                DynamicRelationshipType.withName("isA"));
                    }
                }
                for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> in
                        : reasoner.getInstances(c, true)) {
                    OWLNamedIndividual i = in.getRepresentativeElement();
                    String indString = i.toString();
                    if (indString.contains("#")) {
                        indString = indString.substring(
                                indString.indexOf("#")+1,indString.lastIndexOf(">"));
                    }
                    Node individualNode =
                            getOrCreateNodeWithUniqueFactory(indString,db);
                    individualNode.createRelationshipTo(classNode,
                            DynamicRelationshipType.withName("isA"));
                    for (OWLObjectPropertyExpression objectProperty:
                            ontology.getObjectPropertiesInSignature()) {
                        for
                                (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual>
                                object: reasoner.getObjectPropertyValues(i,
                                objectProperty)) {
                            String reltype = objectProperty.toString();
                            reltype = reltype.substring(reltype.indexOf("#")+1,
                                    reltype.lastIndexOf(">"));
                            String s =
                                    object.getRepresentativeElement().toString();
                            s = s.substring(s.indexOf("#")+1,
                                    s.lastIndexOf(">"));
                            Node objectNode =
                                    getOrCreateNodeWithUniqueFactory(s,db);
                            individualNode.createRelationshipTo(objectNode,
                                    DynamicRelationshipType.withName(reltype));
                        }
                    }
                    for (OWLDataPropertyExpression dataProperty:
                            ontology.getDataPropertiesInSignature()) {
                        for (OWLLiteral object: reasoner.getDataPropertyValues(
                                i, dataProperty.asOWLDataProperty())) {
                            String reltype =
                                    dataProperty.asOWLDataProperty().toString();
                            reltype = reltype.substring(reltype.indexOf("#")+1,
                                    reltype.lastIndexOf(">"));
                            String s = object.toString();
                            individualNode.setProperty(reltype, s);
                        }
                    }
                }
            }
            tx.success();
        } finally {
            tx.terminate();
        }

    }
    private static Node getOrCreateNodeWithUniqueFactory(String nodeName,
                                                         GraphDatabaseService graphDb) {
        UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory(
                graphDb, "index") {
            @Override
            protected void initialize(Node created,
                                      Map<String, Object> properties) {
                created.setProperty("name", properties.get("name"));
            }
        };

        return factory.getOrCreate("name", nodeName);
    }
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
}
