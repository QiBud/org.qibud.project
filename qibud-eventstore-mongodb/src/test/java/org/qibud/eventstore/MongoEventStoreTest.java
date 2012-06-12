package org.qibud.eventstore;

import org.junit.Ignore;

import com.mongodb.Mongo;

import org.qibud.eventstore.test.AbstractEventStoreTest;

@Ignore( "This test needs a MongoDB instance on 127.0.0.1:27017 which is the default." )
public class MongoEventStoreTest
        extends AbstractEventStoreTest
{

    @Override
    protected EventStore newEventStore()
            throws Exception
    {
        return new MongoEventStore( new Mongo( "127.0.0.1" ), "es-test", "events", "attachments", "counter" );
    }

}
