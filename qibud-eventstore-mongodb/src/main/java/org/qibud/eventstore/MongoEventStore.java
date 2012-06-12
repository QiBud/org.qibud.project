package org.qibud.eventstore;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;

import org.json.JSONException;
import org.json.JSONObject;
import org.qibud.eventstore.DomainEventAttachment.DataLocator;
import org.qibud.mongodb.MongoDB;

/**
 * EventStore backed by MongoDB.
 */
public class MongoEventStore
        extends AbstractEventStore
{

    private static final String COUNTER_NAME = "mongo-event-store-counter";

    private final String eventsCollection;

    private final String attachmentsCollectionsPrefix;

    private final String counterCollection;

    private final Mongo driver;

    private final DB database;

    private final GridFS gridfs;

    public MongoEventStore( Mongo driver, String database, String eventsCollection, String attachmentsCollectionsPrefix, String counterCollection )
    {
        this.eventsCollection = eventsCollection;
        this.attachmentsCollectionsPrefix = attachmentsCollectionsPrefix;
        this.counterCollection = counterCollection;

        this.driver = driver;
        this.database = driver.getDB( database );
        this.gridfs = new GridFS( this.database, this.attachmentsCollectionsPrefix );
        MongoDB.ensureCounter( this.database, this.counterCollection, COUNTER_NAME );
    }

    @Override
    protected void doStoreEvents( DomainEventsSequence sequence )
    {
        database.requestStart();

        // Prepare changes
        for ( DomainEventAttachment attachment : sequence.attachments() ) {
            try {
                GridFSInputFile createFile = gridfs.createFile( attachment.data(), attachment.localIdentity() );
                createFile.save();
            } catch ( IOException ex ) {
                throw new EventStoreException( "Unable to store event attachments: " + ex.getMessage(), ex );
            }
        }
        DBObject dbObject = ( DBObject ) JSON.parse( sequence.toJSON().toString() );
        dbObject.put( "_id", MongoDB.getAndIncrementCounter( database, counterCollection, COUNTER_NAME ) );

        // Send changes to the underlying database
        database.getCollection( eventsCollection ).save( dbObject, WriteConcern.FSYNC_SAFE );

        database.requestDone();
    }

    @Override
    public List<DomainEventsSequence> eventsSequences( int offset, int limit )
    {
        try {
            DBCursor cursor = database.getCollection( eventsCollection ).find().sort( new BasicDBObject( "_id", 1 ) ).skip( offset ).limit( limit );
            List<DomainEventsSequence> sequences = new ArrayList<DomainEventsSequence>();
            while ( cursor.hasNext() ) {
                DBObject next = cursor.next();
                JSONObject json = new JSONObject( JSON.serialize( next ) );
                DomainEventsSequence sequence = DomainEventsSequenceImpl.fromJSON( json, attachmentDataLocator() );
                sequences.add( sequence );
            }
            return Collections.unmodifiableList( sequences );
        } catch ( JSONException ex ) {
            throw new EventStoreException( "Unable to read DomainEventsSequences from MongoDB.", ex );
        }
    }

    @Override
    public int count()
    {
        return ( int ) database.getCollection( eventsCollection ).count();
    }

    @Override
    protected DataLocator attachmentDataLocator()
    {
        return new DataLocator()
        {

            @Override
            public InputStream data( String attachmentLocalIdentity )
                    throws IOException
            {
                return gridfs.findOne( attachmentLocalIdentity ).getInputStream();
            }

        };
    }

    @Override
    public void clear()
    {
        String attachmentFilesCollection = attachmentsCollectionsPrefix + ".files";
        String attachmentChunksCollection = attachmentsCollectionsPrefix + ".chunks";
        database.getCollection( eventsCollection ).drop();
        database.getCollection( counterCollection ).drop();
        database.getCollection( attachmentFilesCollection ).drop();
        database.getCollection( attachmentChunksCollection ).drop();
    }

}
