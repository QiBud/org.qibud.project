package org.qibud.mongodb;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import org.codeartisans.java.toolbox.Couple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDB
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.mongodb" );

    public static Couple<Mongo, DB> connectToMongoDB( String host, Integer port, String dbName )
    {
        try {
            Mongo mongoInstance = new Mongo( host, port );
            DB attachmentsDBInstance = mongoInstance.getDB( dbName );
            return new Couple<Mongo, DB>( mongoInstance, attachmentsDBInstance );
        } catch ( UnknownHostException ex ) {
            throw new MongoException( ex.getMessage(), ex );
        }
    }

    public static void ensureIndex( DB db, String collectionName, boolean unique, String indexName, String... keys )
    {
        DBCollection collection = db.getCollection( collectionName );
        DBObject keysObject = new BasicDBObject( keys.length );
        for ( String key : keys ) {
            keysObject.put( key, key );
        }
        collection.ensureIndex( keysObject, indexName, unique );
    }

    public static void ensureCounter( DB db, String collectionName, String counterName )
    {
        DBCollection collection = db.getCollection( collectionName );
        DBObject domainEventCounter = new BasicDBObject();
        domainEventCounter.put( "_id", counterName );
        DBCursor cursor = collection.find( domainEventCounter );
        if ( cursor.count() < 1 ) {
            domainEventCounter.put( "count", 0L );
            collection.insert( domainEventCounter );
            LOGGER.info( "Inserted initial DomainEvent counter with a 0 value." );
        } else {
            LOGGER.info( "DomainEvent counter already exists (" + cursor.next().get( "count" ) + "), doing nothing." );
        }
    }

    public static long getAndIncrementCounter( DB db, String collectionName, String counterName )
    {
        DBCollection collection = db.getCollection( collectionName );
        DBObject query = new BasicDBObject();
        query.put( "_id", counterName );

        // this object represents the "update" or the SET blah=blah in SQL
        DBObject change = new BasicDBObject( "count", 1 );
        DBObject update = new BasicDBObject( "$inc", change ); // the $inc here is a mongodb command for increment

        // Atomically updates the sequence field and returns the value for you
        DBObject res = collection.findAndModify( query, new BasicDBObject(), new BasicDBObject(), false, update, true, true );
        String nextIdString = res.get( "count" ).toString();

        return Long.valueOf( nextIdString );
    }

    private MongoDB()
    {
    }

}
