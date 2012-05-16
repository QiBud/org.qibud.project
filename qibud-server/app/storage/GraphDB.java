package storage;

import play.Play;

import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import buds.BudNode;
import utils.QiBudException;

public class GraphDB
{

    private static GraphDB instance;

    public static synchronized GraphDB getInstance()
    {
        if ( instance == null ) {
            instance = new GraphDB();
        }
        return instance;
    }

    private GraphDatabaseService graphDatabase;

    public synchronized void startGraphDatabase()
    {
        if ( graphDatabase == null ) {
            String graphDatabasePath = Play.application().configuration().getString( "qibud.graphdb.path" );
            if ( StringUtils.isEmpty( graphDatabasePath ) ) {
                throw new QiBudException( "Neo4J Database Storage Path is empty, check your configuration" );
            }
            graphDatabase = new GraphDatabaseFactory().newEmbeddedDatabase( graphDatabasePath );
        }
    }

    public synchronized void shutdownEmbeddedDatabase()
    {
        if ( graphDatabase != null ) {
            graphDatabase.shutdown();
            graphDatabase = null;
        }
    }

    public Node createBudNode( String identity )
    {
        return createBudNode( identity, 0L );
    }

    public Node createBudNode( String identity, Long qi )
    {
        Transaction tx = graphDatabase.beginTx();
        try {

            Node budNode = graphDatabase.createNode();
            budNode.setProperty( BudNode.IDENTITY, identity );
            budNode.setProperty( BudNode.QI, qi );
            tx.success();
            return budNode;

        } finally {
            tx.finish();
        }
    }

    private GraphDB()
    {
    }

}
