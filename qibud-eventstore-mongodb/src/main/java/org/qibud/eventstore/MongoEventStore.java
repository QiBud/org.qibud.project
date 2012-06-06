package org.qibud.eventstore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

import org.json.JSONException;
import org.json.JSONObject;
import org.qibud.mongodb.MongoDB;

/**
 * EventStore backed by MongoDB.
 */
public class MongoEventStore
        extends AbstractEventStore
{

    private static final String COUNTER_NAME = "mongo-event-store-counter";

    private final Mongo driver;

    private final DB database;

    private final String eventsCollection;

    private final String utilsCollection;

    public MongoEventStore( Mongo driver, String database, String eventsCollection, String utilsCollection )
    {
        this.driver = driver;
        this.database = driver.getDB( database );
        this.eventsCollection = eventsCollection;
        this.utilsCollection = utilsCollection;
        MongoDB.ensureCounter( this.database, this.utilsCollection, COUNTER_NAME );
    }

    @Override
    protected void doStoreEvents( DomainEventsSequence domainEventsSequence )
    {
        DBObject dbObject = ( DBObject ) JSON.parse( domainEventsSequence.toJSON().toString() );
        dbObject.put( "_id", MongoDB.getAndIncrementCounter( database, utilsCollection, COUNTER_NAME ) );
        database.getCollection( eventsCollection ).save( dbObject, WriteConcern.FSYNC_SAFE );
    }

    @Override
    public List<DomainEventsSequence> eventsSequences( int offset, int limit )
    {
        try {
            DBCursor cursor = database.getCollection( eventsCollection ).find().sort( new BasicDBObject( "_id", 1 ) ).skip( offset ).limit( limit );
            List<DomainEventsSequence> result = new ArrayList<DomainEventsSequence>();
            while ( cursor.hasNext() ) {
                DBObject next = cursor.next();
                JSONObject jsonObject = new JSONObject( JSON.serialize( next ) );
                DomainEventsSequence eventdSequence = DomainEventsSequenceImpl.fromJSON( jsonObject );
                result.add( eventdSequence );
            }
            return Collections.unmodifiableList( result );
        } catch ( JSONException ex ) {
            throw new EventStoreException( "Unable to read DomainEventsSequences from MongoDB.", ex );
        }
    }

    @Override
    public int count()
    {
        return ( int ) database.getCollection( eventsCollection ).count();
    }

}
