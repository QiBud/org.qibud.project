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
package infrastructure.graphdb;

import domain.buds.BudNode;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.helpers.collection.Iterables;
import org.qi4j.api.service.ServiceActivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import utils.QiBudException;

public class GraphDBImpl
    implements GraphDB, ServiceActivation
{

    public static enum RelTypes
        implements RelationshipType
    {

        IS_BUD_REF,
        IS_BUD,
        IS_ROOT_BUD,
        IS_CHILD_BUD
    }

    private static final Logger LOGGER = LoggerFactory.getLogger( GraphDB.class );
    private String graphDatabasePath;
    private GraphDatabaseService graphDatabase;

    @Override
    public void activateService()
        throws Exception
    {
        graphDatabasePath = Play.application().configuration().getString( "qibud.graphdb.path" );
        if( StringUtils.isEmpty( graphDatabasePath ) )
        {
            throw new QiBudException( "Neo4J Database Storage Path is empty, check your configuration" );
        }

        graphDatabase = new GraphDatabaseFactory().newEmbeddedDatabase( graphDatabasePath );
        LOGGER.info( "GraphDB Started" );

        // BudReferenceNode creation if needed
        Iterable<Relationship> relationships = graphDatabase.getReferenceNode().getRelationships( Direction.OUTGOING, RelTypes.IS_BUD_REF );
        if( Iterables.count( relationships ) <= 0 )
        {
            Transaction tx = graphDatabase.beginTx();
            try
            {
                Node budRefNode = graphDatabase.createNode();
                graphDatabase.getReferenceNode().createRelationshipTo( budRefNode, RelTypes.IS_BUD_REF );
                tx.success();
                LOGGER.info( "Bud Ref Node created" );
            }
            finally
            {
                tx.finish();
            }
        }
    }

    @Override
    public void passivateService()
        throws Exception
    {
        graphDatabase.shutdown();
        graphDatabase = null;
        LOGGER.info( "GraphDB stopped" );
        graphDatabasePath = null;
    }

    @Override
    public Node getBudNode( String identity )
    {
        Iterable<Relationship> isBudsRelationships = getBudRefNode().getRelationships( Direction.OUTGOING, RelTypes.IS_BUD );
        for( Relationship eachIsBudRel : isBudsRelationships )
        {
            Node eachNode = eachIsBudRel.getEndNode();
            String eachIdentity = (String) eachNode.getProperty( BudNode.IDENTITY );
            if( identity.equals( eachIdentity ) )
            {
                return eachNode;
            }
        }
        return null;
    }

    @Override
    public Node createRootBudNode( String identity )
    {
        if( Iterables.count( getBudRefNode().getRelationships( Direction.OUTGOING, RelTypes.IS_ROOT_BUD ) ) > 0 )
        {
            throw new IllegalStateException( "The Root Bud already exists, check your code" );
        }
        Transaction tx = graphDatabase.beginTx();
        try
        {
            Node budNode = graphDatabase.createNode();
            budNode.setProperty( BudNode.IDENTITY, identity );
            budNode.setProperty( BudNode.QI, 0L );

            getBudRefNode().createRelationshipTo( budNode, RelTypes.IS_BUD );
            getBudRefNode().createRelationshipTo( budNode, RelTypes.IS_ROOT_BUD );

            tx.success();

            return budNode;
        }
        finally
        {
            tx.finish();
        }
    }

    @Override
    public Node createBudNode( String parentIdentity, String identity )
    {
        return createBudNode( parentIdentity, identity, 0L );
    }

    @Override
    public Node createBudNode( String parentIdentity, String identity, Long qi )
    {
        Transaction tx = graphDatabase.beginTx();
        try
        {
            Node parentNode = getBudNode( parentIdentity );
            if( parentNode == null )
            {
                throw new IllegalArgumentException( "Parent Bud Node '" + parentIdentity + "' do not exists in GraphDB." );
            }
            Node budNode = graphDatabase.createNode();
            budNode.setProperty( BudNode.IDENTITY, identity );
            budNode.setProperty( BudNode.QI, qi );

            getBudRefNode().createRelationshipTo( budNode, RelTypes.IS_BUD );
            parentNode.createRelationshipTo( budNode, RelTypes.IS_CHILD_BUD );

            tx.success();

            return budNode;
        }
        finally
        {
            tx.finish();
        }
    }

    @Override
    public void deleteBudNode( String identity )
    {
        Transaction tx = graphDatabase.beginTx();
        try
        {
            Node budNode = getBudNode( identity );
            for( Relationship eachRel : budNode.getRelationships() )
            {
                if( eachRel.getType() == RelTypes.IS_ROOT_BUD )
                {
                    throw new IllegalArgumentException( "You cannot delete the root bud" );
                }
                eachRel.delete();
            }
            budNode.delete();

            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    private Node getBudRefNode()
    {
        Node referenceNode = graphDatabase.getReferenceNode();
        Iterable<Relationship> budRefs = referenceNode.getRelationships( Direction.OUTGOING, RelTypes.IS_BUD_REF );
        Relationship budRef = Iterables.first( budRefs );
        return budRef.getEndNode();
    }

}
