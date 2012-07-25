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
package storage;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.apache.commons.lang.StringUtils;
import org.qibud.eventstore.EventStore;
import org.qibud.eventstore.MongoEventStore;
import org.qibud.mongodb.MongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import utils.QiBudException;
import utils.Threads;

public class EventSourcingDB
{

    private static final Logger LOGGER = LoggerFactory.getLogger( EventSourcingDB.class );

    private static final String CONFIG_HOST = "qibud.eventstore.host";

    private static final String CONFIG_PORT = "qibud.eventstore.port";

    private static final String CONFIG_DB = "qibud.eventstore.db";

    private static EventSourcingDB instance;

    public static synchronized EventSourcingDB getInstance()
    {
        if ( instance == null ) {
            instance = new EventSourcingDB();
        }
        return instance;
    }

    private Mongo mongo;

    private MongoEventStore eventStore;

    private EventSourcingDB()
    {
    }

    public synchronized void start()
    {
        if ( mongo == null ) {
            String host = Play.application().configuration().getString( CONFIG_HOST );
            Integer port = Play.application().configuration().getInt( CONFIG_PORT );
            String dbName = Play.application().configuration().getString( CONFIG_DB );
            if ( StringUtils.isEmpty( host ) ) {
                throw new QiBudException( "EventSourcingDB host is empty, check your configuration (" + CONFIG_HOST + ")" );
            }
            if ( port == null ) {
                throw new QiBudException( "EventSourcingDB port is empty, check your configuration (" + CONFIG_PORT + ")" );
            }
            if ( StringUtils.isEmpty( dbName ) ) {
                throw new QiBudException( "EventSourcingDB database name is empty, check your configuration (" + CONFIG_DB + ")" );
            }

            registerShutdownHook( host, port, dbName, !Play.isProd() );

            mongo = MongoDB.connectToMongoDB( host, port );
            eventStore = new MongoEventStore( mongo, dbName, "events", "attachments", "counter" );

            LOGGER.info( "EventSourcingDB started" );
        }
    }

    public synchronized void shutdown()
    {
        if ( mongo != null ) {
            mongo.close();
            mongo = null;
            eventStore = null;
            LOGGER.info( "EventSourcingDB stopped" );
        }
    }

    public EventStore eventStore()
    {
        return eventStore;
    }

    private void registerShutdownHook( final String host, final Integer port, final String dbName, final boolean clear )
    {
        if ( !Threads.isThreadRegisteredAsShutdownHook( "eventstoredb-shutdown" ) ) {
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

            }, "eventstoredb-shutdown" ) );
        }
    }

    private void clear( String host, Integer port, String dbName )
    {
        Mongo mongo = MongoDB.connectToMongoDB( host, port );
        DB db = mongo.getDB( dbName );
        db.dropDatabase();
        mongo.close();
        LOGGER.warn( "EventSourcingDB cleared!" );
    }

}
