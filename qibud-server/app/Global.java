
import play.Application;
import play.GlobalSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import buds.Bud;
import buds.BudsFactory;
import buds.BudsRepository;
import storage.AttachmentsDB;
import storage.EntitiesDB;
import storage.GraphDB;

public class Global
        extends GlobalSettings
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.server" );

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );
        LOGGER.info( "QiBud Server starting ..." );

        EntitiesDB.getInstance().start();
        GraphDB.getInstance().start();
        AttachmentsDB.getInstance().start();

        Bud rootBud = BudsRepository.getInstance().findRootBud();
        if ( rootBud == null ) {
            rootBud = BudsFactory.getInstance().createRootBud();
        }

        LOGGER.info( "QiBud Server Started with Root Bud: {}", rootBud );
    }

    @Override
    public void onStop( Application aplctn )
    {
        GraphDB.getInstance().shutdown();
        AttachmentsDB.getInstance().shutdown();
        EntitiesDB.getInstance().shutdown();
        super.onStop( aplctn );
    }

}
