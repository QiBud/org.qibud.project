package storage;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import play.Play;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import org.apache.commons.lang.StringUtils;
import org.codeartisans.java.toolbox.Couple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import buds.BudAttachment;
import utils.QiBudException;

/**
 * Hold binary content attached to Buds.
 * 
 * Uploaded files are added as MongoDB GridFS files.
 * 
 * TODO Async task that extract attachment metadata and store it as a Mongo document alongside the GridFS file.
 * TODO Controller that output attachment metadata as a json resource
 */
public class AttachmentsDB
{

    private static final Logger LOGGER = LoggerFactory.getLogger( AttachmentsDB.class );

    private static final String CONFIG_HOST = "qibud.attachmentsdb.host";

    private static final String CONFIG_PORT = "qibud.attachmentsdb.port";

    private static final String CONFIG_DB = "qibud.attachmentsdb.db";

    private static AttachmentsDB instance;

    public static synchronized AttachmentsDB getInstance()
    {
        if ( instance == null ) {
            instance = new AttachmentsDB();
        }
        return instance;
    }

    private AttachmentsDB()
    {
    }

    private Mongo mongo;

    private DB attachmentsDB;

    public synchronized void start()
    {
        if ( mongo == null ) {
            String host = Play.application().configuration().getString( CONFIG_HOST );
            Integer port = Play.application().configuration().getInt( CONFIG_PORT );
            String dbName = Play.application().configuration().getString( CONFIG_DB );
            if ( StringUtils.isEmpty( host ) ) {
                throw new QiBudException( "AttachmentsDB host is empty, check your configuration (" + CONFIG_HOST + ")" );
            }
            if ( port == null ) {
                throw new QiBudException( "AttachmentsDB port is empty, check your configuration (" + CONFIG_PORT + ")" );
            }
            if ( StringUtils.isEmpty( dbName ) ) {
                throw new QiBudException( "AttachmentsDB database name is empty, check your configuration (" + CONFIG_DB + ")" );
            }

            registerShutdownHook( host, port, dbName, !Play.isProd() );

            Couple<Mongo, DB> mongoCouple = connectToMongoDB( host, port, dbName );
            mongo = mongoCouple.left();
            attachmentsDB = mongoCouple.right();

            LOGGER.info( "AttachmentsDB started" );
        }
    }

    public synchronized void shutdown()
    {
        if ( mongo != null ) {
            mongo.close();
            mongo = null;
            attachmentsDB = null;
            LOGGER.info( "AttachmentsDB stopped" );
        }
    }

    private void registerShutdownHook( final String host, final Integer port, final String dbName, final boolean clear )
    {
        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {

            @Override
            public void run()
            {
                shutdown();
                if ( clear ) {
                    clear( host, port, dbName );
                }
            }

        } ) );
    }

    private void clear( String host, Integer port, String dbName )
    {
        Couple<Mongo, DB> mongoCouple = connectToMongoDB( host, port, dbName );
        mongoCouple.right().dropDatabase();
        mongoCouple.left().close();
        LOGGER.warn( "AttachmentsDB cleared!" );
    }

    public void storeAttachment( String budIdentity, String baseName, InputStream inputStream )
    {
        if ( StringUtils.isEmpty( budIdentity ) ) {
            throw new IllegalArgumentException( "Bud identity is null" );
        }
        if ( StringUtils.isEmpty( baseName ) ) {
            throw new IllegalArgumentException( "Base name is null" );
        }
        if ( inputStream == null ) {
            throw new IllegalArgumentException( "InputStream is null" );
        }
        String filename = budIdentity + "/" + baseName;
        GridFS gridFS = new GridFS( attachmentsDB );
        GridFSInputFile gridFSInputFile = gridFS.createFile( inputStream );
        gridFSInputFile.setFilename( filename );
        gridFSInputFile.put( BudAttachment.IDENTITY, budIdentity );
        // TODO extract and save metadata
        gridFSInputFile.save();
    }

    public List<GridFSDBFile> getBudDBFiles( String identity )
    {
        DBObject query = new BasicDBObject( BudAttachment.IDENTITY, identity );
        GridFS gridFS = new GridFS( attachmentsDB );
        return gridFS.find( query );
    }

    public void deleteBudDBFiles( String identity )
    {
        DBObject query = new BasicDBObject( BudAttachment.IDENTITY, identity );
        GridFS gridFS = new GridFS( attachmentsDB );
        List<GridFSDBFile> budDBFiles = gridFS.find( query );
        for ( GridFSDBFile eachDBFile : budDBFiles ) {
            gridFS.remove( eachDBFile.getFilename() );
        }
    }

    private Couple<Mongo, DB> connectToMongoDB( String host, Integer port, String dbName )
    {
        try {
            Mongo mongoInstance = new Mongo( host, port );
            DB attachmentsDBInstance = mongoInstance.getDB( dbName );
            return new Couple<Mongo, DB>( mongoInstance, attachmentsDBInstance );
        } catch ( UnknownHostException ex ) {
            throw new QiBudException( ex );
        } catch ( MongoException ex ) {
            throw new QiBudException( ex );
        }
    }

}
