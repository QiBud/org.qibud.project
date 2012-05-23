package buds;

import java.io.InputStream;

import play.Play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.AttachmentsDB;
import storage.GraphDB;

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
