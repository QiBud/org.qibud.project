/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 * Copyright (c) 2012, Samuel Loup. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package infrastructure.attachmentsdb;

import akka.util.Duration;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import domain.buds.BudAttachment;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.bson.types.ObjectId;
import org.qi4j.api.service.ServiceActivation;
import org.qibud.mongodb.MongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import play.libs.Akka;
import utils.QiBudException;

public class AttachmentsDBImpl
        implements AttachmentsDB, ServiceActivation
{

    private static final Logger LOGGER = LoggerFactory.getLogger( AttachmentsDB.class );

    private static final String CONFIG_HOST = "qibud.attachmentsdb.host";

    private static final String CONFIG_PORT = "qibud.attachmentsdb.port";

    private static final String CONFIG_DB = "qibud.attachmentsdb.db";

    private String host;

    private Integer port;

    private String dbName;

    private Mongo mongo;

    private DB attachmentsDB;

    @Override
    public void activateService()
            throws Exception
    {
        host = Play.application().configuration().getString( CONFIG_HOST );
        port = Play.application().configuration().getInt( CONFIG_PORT );
        dbName = Play.application().configuration().getString( CONFIG_DB );
        if ( StringUtils.isEmpty( host ) ) {
            throw new QiBudException( "AttachmentsDB host is empty, check your configuration (" + CONFIG_HOST + ")" );
        }
        if ( port == null ) {
            throw new QiBudException( "AttachmentsDB port is empty, check your configuration (" + CONFIG_PORT + ")" );
        }
        if ( StringUtils.isEmpty( dbName ) ) {
            throw new QiBudException( "AttachmentsDB database name is empty, check your configuration (" + CONFIG_DB + ")" );
        }

        mongo = MongoDB.connectToMongoDB( host, port );
        attachmentsDB = mongo.getDB( dbName );

        LOGGER.info( "AttachmentsDB started" );
    }

    @Override
    public void passivateService()
            throws Exception
    {
        mongo.close();
        mongo = null;
        attachmentsDB = null;
        LOGGER.info( "AttachmentsDB stopped" );
        if ( !Play.isProd() ) {
            Mongo mongo = MongoDB.connectToMongoDB( host, port );
            DB db = mongo.getDB( dbName );
            db.dropDatabase();
            mongo.close();
            LOGGER.warn( "AttachmentsDB cleared!" );
        }
        host = null;
        port = null;
        dbName = null;
    }

    @Override
    public void storeAttachment( String budIdentity, String baseName, InputStream inputStream )
    {
        final String filename = budIdentity + "/" + baseName;

        // Save minimal data
        GridFS gridFS = new GridFS( attachmentsDB );
        GridFSInputFile gridFSInputFile = gridFS.createFile( inputStream );
        gridFSInputFile.setFilename( filename );
        gridFSInputFile.put( BudAttachment.IDENTITY, budIdentity );
        gridFSInputFile.put( BudAttachment.ORIGINAL_FILENAME, baseName );
        gridFSInputFile.save();

        // Schedule metadata extraction
        Akka.system().scheduler().scheduleOnce( Duration.create( 10, TimeUnit.SECONDS ), new Runnable()
        {

            @Override
            public void run()
            {
                gatherMedatada( filename );
            }

        } );
    }

    private void gatherMedatada( String filename )
    {
        GridFSDBFile dbFile = new GridFS( attachmentsDB ).findOne( filename );
        if ( dbFile == null ) {
            LOGGER.warn( "Cannot gather metadata, attachment does not exists (filename: " + filename + ")" );
            return;
        }
        try {

            Tika tika = new Tika();

            // Detecting content-type
            InputStream inputStream = dbFile.getInputStream();
            String contentType = tika.detect( inputStream );
            dbFile.put( "contentType", contentType );
            inputStream.close();

            // Gather metadata
            inputStream = dbFile.getInputStream();
            Metadata tikameta = new Metadata();
            tika.parse( inputStream, tikameta ); // TODO Use extracted content once QiBud as a full text search engine.
            inputStream.close();

            DBObject mongometa = new BasicDBObject( tikameta.size() );
            for ( String eachMetaKey : tikameta.names() ) {
                mongometa.put( eachMetaKey, tikameta.get( eachMetaKey ) );
            }
            dbFile.setMetaData( mongometa );

            dbFile.save();
            LOGGER.info( "Gathered metadada for " + filename );

        } catch ( IOException ex ) {
            LOGGER.error( "Unable to gather metadata (filename: " + filename + "): " + ex.getMessage(), ex );
        }
    }

    @Override
    public GridFSDBFile getDBFile( String attachmentId )
    {
        return new GridFS( attachmentsDB ).findOne( new ObjectId( attachmentId ) );
    }

    @Override
    public List<GridFSDBFile> getBudDBFiles( String identity )
    {
        DBObject query = new BasicDBObject( BudAttachment.IDENTITY, identity );
        GridFS gridFS = new GridFS( attachmentsDB );
        return gridFS.find( query );
    }

    @Override
    public void deleteBudDBFiles( String identity )
    {
        DBObject query = new BasicDBObject( BudAttachment.IDENTITY, identity );
        GridFS gridFS = new GridFS( attachmentsDB );
        List<GridFSDBFile> budDBFiles = gridFS.find( query );
        for ( GridFSDBFile eachDBFile : budDBFiles ) {
            gridFS.remove( eachDBFile.getFilename() );
        }
    }

}
