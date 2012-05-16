
import play.Application;
import play.GlobalSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import buds.Bud;
import buds.BudsFactory;
import buds.BudsRepository;
import storage.AttachmentsDB;
import storage.GraphDB;

public class Global
        extends GlobalSettings
{

    private static final Logger LOGGER = LoggerFactory.getLogger( Global.class );

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );

        BudsRepository budRepository = BudsRepository.getInstance();
        BudsFactory budFactory = BudsFactory.getInstance();
        GraphDB graphDB = GraphDB.getInstance();
        AttachmentsDB attachmentsDB = AttachmentsDB.getInstance();

        graphDB.startGraphDatabase();
        attachmentsDB.startAttachmentsDatabase();

        Bud rootBud = budRepository.findRootBud();
        if ( rootBud == null ) {
            rootBud = budFactory.createRootBud();
        }

        LOGGER.info( "QiBud Server Started with Root Bud: {}", rootBud );

        registerEmergencyShutdownHook();
    }

    @Override
    public void onStop( Application aplctn )
    {
        GraphDB.getInstance().shutdownEmbeddedDatabase();
        AttachmentsDB.getInstance().shutdownAttachmentsDatabase();
        super.onStop( aplctn );
    }

    private void registerEmergencyShutdownHook()
    {
        GraphDB.getInstance().shutdownEmbeddedDatabase();
        AttachmentsDB.getInstance().shutdownAttachmentsDatabase();
    }

}
