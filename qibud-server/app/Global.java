
import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import org.qibud.eventstore.Usecase;
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

    @Override
    public Action onRequest( Http.Request request, Method actionMethod )
    {
        Usecase annotation = actionMethod.getAnnotation( Usecase.class );
        final String usecase;
        if ( annotation == null ) {
            usecase = request.method() + ":" + request.uri();
        } else {
            usecase = annotation.value();
        }
        return new Action.Simple()
        {

            @Override
            public Result call( Http.Context ctx )
                    throws Throwable
            {
                LOGGER.debug( "Before request with Usecase '{}'", usecase );
                try {
                    Result result = delegate.call( ctx );
                    LOGGER.debug( "After request with Usecase '{}'", usecase );
                    return result;
                } catch ( Throwable ex ) {
                    LOGGER.debug( "Error after request with Usecase '{}'", usecase, ex );
                    throw ex;
                } finally {
                    LOGGER.debug( "Finally after request with Usecase '{}'", usecase );
                }
            }

        };
    }

}
