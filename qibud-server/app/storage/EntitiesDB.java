package storage;

import java.net.UnknownHostException;

import play.Play;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.QiBudException;
import utils.Threads;

/**
 * Reference Bud Entities holder.
 * 
 * Entities are defined using Mongo Jackson Mapper using the play2 module that
 * can be found here: https://github.com/vznet/play-mongo-jackson-mapper
 * 
 * This class don't do anything but loading the plugin configuration and wiping
 * the whole data in dev and test modes.
 * 
 * TODO Database connection outside a play application using the Play module is
 *      tricky. We need that to clear the data in dev and test modes. It's done
 *      by reading the plugin configuration and register a shutdown hook that
 *      manually connect to MongoDB to drop the data. For testing purpose it
 *      could become convenient to support MongoDB replicas sets by parsing the
 *      config property 'mongodb.servers' as the plugin does.
 * 
 * See:
 * https://github.com/vznet/play-mongo-jackson-mapper/blob/master/src/main/scala/play/modules/mongodb/jackson/MongoDB.scala
 */
public class EntitiesDB
{

    private static final Logger LOGGER = LoggerFactory.getLogger( EntitiesDB.class );

    private static final String CONFIG_SERVERS = "mongodb.servers";

    private static final String CONFIG_CREDENTIALS = "mongodb.credentials";

    private static final String CONFIG_DB = "mongodb.database";

    private static EntitiesDB instance;

    public static synchronized EntitiesDB getInstance()
    {
        if ( instance == null ) {
            instance = new EntitiesDB();
        }
        return instance;
    }

    private EntitiesDB()
    {
    }

    public void start()
    {
        String servers = Play.application().configuration().getString( CONFIG_SERVERS );
        String credentials = Play.application().configuration().getString( CONFIG_CREDENTIALS );
        String database = Play.application().configuration().getString( CONFIG_DB );
        if ( StringUtils.isEmpty( servers ) ) {
            throw new QiBudException( "EntitiesDB servers is empty, check your configuration (" + CONFIG_SERVERS + ")" );
        }
        if ( StringUtils.isEmpty( database ) ) {
            throw new QiBudException( "EntitiesDB database name is empty, check your configuration (" + CONFIG_SERVERS + ")" );
        }
        registerShutdownHook( servers, credentials, database, !Play.isProd() );
        LOGGER.info( "EntitiesDB started" );
    }

    public void shutdown()
    {
        LOGGER.info( "EntitiesDB stopped" );
    }

    private void registerShutdownHook( final String servers, final String credentials, final String dbName, final boolean clear )
    {
        if ( !Threads.isThreadRegisteredAsShutdownHook( "entitiesdb-shutdown" ) ) {
            Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
            {

                @Override
                public void run()
                {
                    shutdown();
                    if ( clear ) {
                        clear( servers, credentials, dbName );
                    }
                }

            }, "entitiesdb-shutdown" ) );
        }
    }

    private void clear( String servers, String credentials, String dbName )
    {
        try {
            Mongo mongoInstance = new Mongo( servers );
            DB entitiesDBInstance = mongoInstance.getDB( dbName );
            entitiesDBInstance.dropDatabase();
            mongoInstance.close();
            LOGGER.warn( "EntitiesDB cleared!" );
        } catch ( UnknownHostException ex ) {
            throw new QiBudException( ex );
        } catch ( MongoException ex ) {
            throw new QiBudException( ex );
        }

    }

}
