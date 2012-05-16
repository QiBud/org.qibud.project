package storage;

import java.io.InputStream;
import java.net.UnknownHostException;

import play.Play;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import org.apache.commons.lang.StringUtils;

import utils.QiBudException;

public class AttachmentsDB
{

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

    private Mongo mongo;

    private DB attachmentsDB;

    public synchronized void startAttachmentsDatabase()
    {
        if ( mongo != null ) {
            try {
                String host = Play.application().configuration().getString( CONFIG_HOST );
                Integer port = Play.application().configuration().getInt( CONFIG_PORT );
                String dbName = Play.application().configuration().getString( CONFIG_DB );
                if ( StringUtils.isEmpty( host ) ) {
                    throw new QiBudException( "MongoDB host is empty, check your configuration (" + CONFIG_HOST + ")" );
                }
                if ( port == null ) {
                    throw new QiBudException( "MongoDB port is empty, check your configuration (" + CONFIG_PORT + ")" );
                }
                if ( StringUtils.isEmpty( dbName ) ) {
                    throw new QiBudException( "MongoDB database name is empty, check your configuration (" + CONFIG_DB + ")" );
                }
                mongo = new Mongo( host, port );
                attachmentsDB = mongo.getDB( dbName );

            } catch ( UnknownHostException ex ) {
                throw new QiBudException( ex );
            } catch ( MongoException ex ) {
                throw new QiBudException( ex );
            }
        }
    }

    public synchronized void shutdownAttachmentsDatabase()
    {
        if ( mongo != null ) {
            mongo.close();
            mongo = null;
            attachmentsDB = null;
        }
    }

    public void storeAttachment( String budIdentity, String baseName, InputStream inputStream )
    {
        String filename = budIdentity + "/" + baseName;
        GridFS gridFS = new GridFS( attachmentsDB );
        GridFSInputFile gridFSInputFile = gridFS.createFile( inputStream );
        gridFSInputFile.setFilename( filename );
        gridFSInputFile.put( "bud_identity", budIdentity );
        // TODO extract and save metadata
        gridFSInputFile.save();
    }

    private AttachmentsDB()
    {
    }

}
