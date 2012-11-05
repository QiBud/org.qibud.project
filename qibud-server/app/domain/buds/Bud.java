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
package domain.buds;

import com.mongodb.gridfs.GridFSDBFile;
import domain.budpacks.BudPacksService;
import domain.roles.Role;
import infrastructure.attachmentsdb.AttachmentsDB;
import infrastructure.graphdb.GraphDB;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joda.time.DateTime;
import org.neo4j.graphdb.Node;
import org.qi4j.api.association.Association;
import org.qi4j.api.association.ManyAssociation;
import org.qi4j.api.common.Optional;
import org.qi4j.api.common.UseDefaults;
import org.qi4j.api.entity.Aggregated;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Immutable;
import org.qi4j.api.property.Property;

@Mixins( Bud.Mixin.class )
public interface Bud
    extends EntityComposite
{

    String ROOT_BUD_IDENTITY = "root";

    @Optional
    Association<Bud> parent();

    Property<String> title();

    @Immutable
    Property<DateTime> postedAt();

    @UseDefaults
    Property<String> content();

    BudNode graphNode();

    List<BudAttachment> attachments();

    boolean isRoot();

    @UseDefaults
    @Aggregated
    ManyAssociation<Role> roles();

    @UseDefaults
    @Aggregated
    ManyAssociation<Role> passivatedRoles();

    Role role( String pack, String role );

    void addRole( String pack, String role );

    void removeRole( String pack, String role );

    abstract class Mixin
        implements Bud
    {

        @Service
        private GraphDB graphDB;
        @Service
        private AttachmentsDB attachmentsDB;
        @Service
        private BudPacksService budPacksService;
        @This
        private Bud bud;
        private BudNode node;
        private List<BudAttachment> attachments;

        @Override
        public BudNode graphNode()
        {
            synchronized( this )
            {
                if( node == null )
                {
                    Node underlyingNode = graphDB.getBudNode( bud.identity().get() );
                    node = new BudNode( underlyingNode );
                }
            }
            return node;
        }

        @Override
        public List<BudAttachment> attachments()
        {
            synchronized( this )
            {
                if( attachments == null )
                {
                    List<GridFSDBFile> budDBFiles = attachmentsDB.getBudDBFiles( bud.identity().get() );
                    List<BudAttachment> budAttachments = new ArrayList<BudAttachment>();
                    for( GridFSDBFile eachDBFile : budDBFiles )
                    {
                        budAttachments.add( new BudAttachment( eachDBFile ) );
                    }
                    attachments = budAttachments;
                }
            }
            return attachments;
        }

        @Override
        public boolean isRoot()
        {
            return ROOT_BUD_IDENTITY.equals( bud.identity().get() );
        }

        @Override
        public Role role( String budPackName, String roleName )
        {
            for( Role role : bud.roles() )
            {
                if( role.budPackName().get().equals( budPackName )
                    && role.roleName().get().equals( roleName ) )
                {
                    return role;
                }
            }
            return null;
        }

        @Override
        public void addRole( String pack, String role )
        {
            Role newRole = null;
            Iterator<Role> passivatedIterator = bud.passivatedRoles().iterator();
            while( passivatedIterator.hasNext() )
            {
                Role unusedRole = passivatedIterator.next();
                if( unusedRole.is( pack, role ) )
                {
                    passivatedIterator.remove();
                    newRole = unusedRole;
                    break;
                }
            }

            if( newRole == null )
            {
                newRole = budPacksService.newRoleInstance( bud, pack, role );
            }

            bud.roles().add( newRole );
            newRole.onEnable( bud );
        }

        @Override
        public void removeRole( String pack, String role )
        {
            Iterator<Role> rolesIterator = bud.roles().iterator();
            while( rolesIterator.hasNext() )
            {
                Role candidate = rolesIterator.next();
                if( candidate.budPackName().get().equals( pack )
                    && candidate.roleName().get().equals( role ) )
                {
                    candidate.onDisable( bud );
                    rolesIterator.remove();
                    bud.passivatedRoles().add( candidate );
                    break;
                }
            }
        }

    }

}
