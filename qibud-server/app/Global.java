
import play.Application;
import play.GlobalSettings;
import play.Play;

import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import utils.QiBudException;

public class Global
        extends GlobalSettings
{

    private GraphDatabaseService embeddedNeo4j;

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );
        // Load configuration
        String graphDatabasePath = Play.application().configuration().getString( "qibud.graphdb.path" );
        if ( StringUtils.isEmpty( graphDatabasePath ) ) {
            throw new QiBudException( "Neo4J Database Storage Path is empty, check your configuration" );
        }
        embeddedNeo4j = new GraphDatabaseFactory().newEmbeddedDatabase( graphDatabasePath );
        registerEmergencyShutdownHook();

    }

    @Override
    public void onStop( Application aplctn )
    {
        embeddedNeo4j.shutdown();
        embeddedNeo4j = null;
        super.onStop( aplctn );
    }

    private void registerEmergencyShutdownHook()
    {
        if ( embeddedNeo4j != null ) {
            embeddedNeo4j.shutdown();
        }
    }

}
