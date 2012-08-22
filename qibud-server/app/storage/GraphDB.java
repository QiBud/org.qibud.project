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
package storage;

import buds.Bud;
import buds.BudEntity;
import buds.BudNode;
import domain.events.RootBudCreatedEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.Iterables;
import org.qibud.eventstore.DomainEvent;
import org.qibud.eventstore.DomainEventsSequence;
import org.qibud.eventstore.EventStream;
import org.qibud.eventstore.EventStreamListener;
import org.qibud.eventstore.EventStreamRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import utils.QiBudException;
import utils.Threads;

/**
 * Bud Graph Holder.
 * 
 * (refNode) --IS_BUD_REF--> (budRefNode) ----IS_BUD----->  (budNode)
 *                                        --IS_ROOT_BUD-->
 */
public class GraphDB
        implements EventStreamListener
{

    public static enum RelTypes
            implements RelationshipType
    {

        IS_BUD_REF,
        IS_BUD,
        IS_ROOT_BUD

    }

    private static final Logger LOGGER = LoggerFactory.getLogger( GraphDB.class );

    private static GraphDB instance;

    public static synchronized GraphDB getInstance()
    {
        if ( instance == null ) {
            instance = new GraphDB();
        }
        return instance;
    }

    private GraphDB()
    {
    }

    private GraphDatabaseService graphDatabase;

    public synchronized void start( EventStream eventStream )
    {
        if ( graphDatabase == null ) {

            String graphDatabasePath = Play.application().configuration().getString( "qibud.graphdb.path" );
            if ( StringUtils.isEmpty( graphDatabasePath ) ) {
                throw new QiBudException( "Neo4J Database Storage Path is empty, check your configuration" );
            }

            EventStreamRegistration registration = eventStream.registerEventStreamListener( this );
            registerShutdownHook( graphDatabasePath, registration, !Play.isProd() );

            graphDatabase = new GraphDatabaseFactory().newEmbeddedDatabase( graphDatabasePath );
            LOGGER.info( "GraphDB Started" );
        }

        // BudReferenceNode creation if needed
        Iterable<Relationship> relationships = graphDatabase.getReferenceNode().getRelationships( Direction.OUTGOING, RelTypes.IS_BUD_REF );
        if ( Iterables.count( relationships ) <= 0 ) {
            Transaction tx = graphDatabase.beginTx();
            try {
                Node budRefNode = graphDatabase.createNode();
                graphDatabase.getReferenceNode().createRelationshipTo( budRefNode, RelTypes.IS_BUD_REF );
                tx.success();
                LOGGER.info( "Bud Ref Node created" );
            } finally {
                tx.finish();
            }
        }
    }

    private void registerShutdownHook( final String graphDatabasePath, final EventStreamRegistration registration, final boolean clear )
    {
        if ( !Threads.isThreadRegisteredAsShutdownHook( "graphdb-shutdown" ) ) {
            Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
            {

                @Override
                public void run()
                {
                    shutdown();
                    if ( clear ) {
                        clear( graphDatabasePath, registration );
                    }
                }

            }, "graphdb-shutdown" ) );
        }
    }

    public synchronized void shutdown()
    {
        if ( graphDatabase != null ) {
            graphDatabase.shutdown();
            graphDatabase = null;
            LOGGER.info( "GraphD stopped" );
        }
    }

    private void clear( String graphDatabasePath, EventStreamRegistration registration )
    {
        registration.unregister();
        // Delete database
        File graphDatabaseDir = new File( graphDatabasePath );
        if ( graphDatabaseDir.exists() ) {
            try {
                FileUtils.deleteDirectory( graphDatabaseDir );
                LOGGER.warn( "GraphDB cleared!" );
            } catch ( IOException ex ) {
                throw new QiBudException( ex );
            }
        }
    }

    public Node getBudNode( String identity )
    {
        Iterable<Relationship> isBudsRelationships = getBudRefNode().getRelationships( Direction.OUTGOING, RelTypes.IS_BUD );
        for ( Relationship eachIsBudRel : isBudsRelationships ) {
            Node eachNode = eachIsBudRel.getEndNode();
            String eachIdentity = ( String ) eachNode.getProperty( BudNode.IDENTITY );
            if ( identity.equals( eachIdentity ) ) {
                return eachNode;
            }
        }
        return null;
    }

    public Node createBudNode( String identity )
    {
        return createBudNode( identity, 0L );
    }

    public Node createBudNode( String identity, Long qi )
    {
        Transaction tx = graphDatabase.beginTx();
        try {

            Node budNode = graphDatabase.createNode();
            budNode.setProperty( BudNode.IDENTITY, identity );
            budNode.setProperty( BudNode.QI, qi );

            getBudRefNode().createRelationshipTo( budNode, RelTypes.IS_BUD );

            tx.success();

            return budNode;

        } finally {
            tx.finish();
        }
    }

    public void setAsRootBud( String identity )
    {
        if ( Iterables.count( getBudRefNode().getRelationships( Direction.OUTGOING, RelTypes.IS_ROOT_BUD ) ) > 0 ) {
            throw new IllegalStateException( "The Root Bud already exists, check your code" );
        }
        Transaction tx = graphDatabase.beginTx();
        try {
            Node budNode = getBudNode( identity );
            getBudRefNode().createRelationshipTo( budNode, RelTypes.IS_ROOT_BUD );
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public void deleteBudNode( String identity )
    {
        Transaction tx = graphDatabase.beginTx();
        try {

            Node budNode = getBudNode( identity );
            for ( Relationship eachRel : budNode.getRelationships() ) {
                if ( eachRel.getType() == RelTypes.IS_ROOT_BUD ) {
                    throw new IllegalArgumentException( "You cannot delete the root bud" );
                }
                eachRel.delete();
            }
            budNode.delete();

            tx.success();

        } finally {
            tx.finish();
        }
    }

    @Override
    public void onDomainEventsSequence( DomainEventsSequence events )
    {
        LOGGER.info( "Applying '{}' usecase requested by the '{}' user.", events.usecase(), events.user() );
        for ( DomainEvent event : events.events() ) {
            LOGGER.info( "Applying event: {}", event );
            if ( RootBudCreatedEvent.class.getName().equals( event.type() ) ) {
                createBudNode( "root" ); // FIXME Hardcoded Root Bud Identity!!
                setAsRootBud( "root" ); // FIXME Hardcoded Root Bud Identity!!
            }
        }
    }

    private Node getBudRefNode()
    {
        return Iterables.first( graphDatabase.getReferenceNode().getRelationships( Direction.OUTGOING, RelTypes.IS_BUD_REF ) ).getEndNode();
    }

}
