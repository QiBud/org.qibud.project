/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
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
package org.qibud.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDB
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.mongodb" );

    public static Mongo connectToMongoDB( String host, Integer port )
    {
        try {
            Mongo mongo = new Mongo( host, port );
            LOGGER.info( "Connected to MongoDB on {}:{}", host, port );
            return mongo;
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
            LOGGER.info( "Inserted initial counter with a 0 value." );
        } else {
            LOGGER.info( "Counter already exists ({}), doing nothing.", cursor.next().get( "count" ) );
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

    public static void deleteCounter( DB db, String collectionName, String counterName )
    {
        DBCollection collection = db.getCollection( collectionName );
        DBObject query = new BasicDBObject();
        query.put( "_id", counterName );
        collection.remove( query, WriteConcern.FSYNC_SAFE );
        LOGGER.info( "Removed counter '{}' from '{}' collection!", counterName, collectionName );
    }

    private MongoDB()
    {
    }

}
