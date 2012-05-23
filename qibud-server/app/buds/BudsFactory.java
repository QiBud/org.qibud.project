package buds;

import java.io.InputStream;
import java.util.Date;

import play.Play;

import org.bson.types.ObjectId;
import org.codeartisans.java.toolbox.exceptions.NullArgumentException;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.AttachmentsDB;
import storage.GraphDB;

/**
 * TODO Move this code to Bud.
 */
public class BudsFactory
{

    private static final Logger LOGGER = LoggerFactory.getLogger( BudsFactory.class );

    private static BudsFactory instance;

    public static synchronized BudsFactory getInstance()
    {
        if ( instance == null ) {
            instance = new BudsFactory();
        }
        return instance;
    }

    // FIXME This is a naïve implementation
    public Bud createNewBud( Bud creationBud, String title, String content )
    {
        NullArgumentException.ensureNotNull( "Creation Bud", creationBud );
        NullArgumentException.ensureNotEmpty( "Title", title );

        // Generate new identity
        String identity = new ObjectId().toString();

        try {
            // Create BudEntity
            BudEntity budEntity = new BudEntity();
            budEntity.identity = identity;
            budEntity.title = title;
            budEntity.content = content;
            BudEntity.save( budEntity );

            // Create BudNode
            GraphDB graphDatabase = GraphDB.getInstance();
            Node creationNode = graphDatabase.getBudNode( creationBud.identity() );
            graphDatabase.createBudNode( identity ); // TODO Bud create new Bud

            return new Bud( budEntity );
        } catch ( RuntimeException ex ) {
            // TODO Manual rollback
            throw ex;
        }
    }

    public Bud createRootBud()
    {
        BudEntity rootBudEntity = BudEntity.findById( Bud.ROOT_BUD_IDENTITY );
        if ( rootBudEntity != null ) {
            throw new IllegalStateException( "The Root Bud already exists, check your code" );
        }
        try {

            // Create ROOT BudEntity
            rootBudEntity = new BudEntity();
            rootBudEntity.identity = Bud.ROOT_BUD_IDENTITY;
            rootBudEntity.title = "Root Bud";
            rootBudEntity.postedAt = new Date();
            rootBudEntity.content = "## This is the Root Bud\nFor now this Bud has no Role and this sample content only.";
            BudEntity.save( rootBudEntity );

            // Create ROOT BudNode
            GraphDB graphDatabase = GraphDB.getInstance();
            graphDatabase.createBudNode( Bud.ROOT_BUD_IDENTITY );
            graphDatabase.setAsRootBud( Bud.ROOT_BUD_IDENTITY );

            // Create ROOT BudAttachment
            String filename = "whats.a.bud.svg";
            InputStream attachmentInputStream = Play.application().resourceAsStream( filename );
            AttachmentsDB.getInstance().storeAttachment( Bud.ROOT_BUD_IDENTITY, filename, attachmentInputStream );

            return new Bud( rootBudEntity );

        } catch ( RuntimeException ex ) {

            // Manual rollback
            BudEntity rootBud = BudEntity.findById( Bud.ROOT_BUD_IDENTITY );
            if ( rootBud != null ) {
                try {
                    AttachmentsDB.getInstance().deleteBudDBFiles( Bud.ROOT_BUD_IDENTITY );
                } catch ( RuntimeException attachEx ) {
                    LOGGER.warn( "Unable to cleanup RootBud attachments after creation failure", attachEx );
                }
                try {
                    GraphDB.getInstance().deleteBudNode( Bud.ROOT_BUD_IDENTITY );
                } catch ( RuntimeException graphEx ) {
                    LOGGER.warn( "Unable to cleanup RootBud node after creation failure", graphEx );
                }
                BudEntity.delete( rootBud );
                LOGGER.error( "Something went wrong when creating Root Bud, hanges have been manually rollbacked", ex );
            }

            throw ex;
        }
    }

    private BudsFactory()
    {
    }

}
