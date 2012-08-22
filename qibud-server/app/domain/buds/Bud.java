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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.neo4j.graphdb.Node;
import org.qi4j.api.common.UseDefaults;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import roles.Role;
import storage.AttachmentsDB;
import storage.GraphDB;

@Mixins( Bud.Mixin.class )
public interface Bud
        extends EntityComposite
{

    String ROOT_BUD_IDENTITY = "root";

    Property<String> title();

    Property<DateTime> postedAt();

    @UseDefaults
    Property<String> content();

    BudNode graphNode();

    List<BudAttachment> attachments();

    boolean isRoot();

    List<Role> roles();

    abstract class Mixin
            implements Bud
    {

        @This
        private Bud bud;

        private BudNode node;

        private List<BudAttachment> attachments;

        @Override
        public BudNode graphNode()
        {
            synchronized( this ) {
                if ( node == null ) {
                    Node underlyingNode = GraphDB.getInstance().getBudNode( bud.identity().get() );
                    node = new BudNode( underlyingNode );
                }
            }
            return node;
        }

        @Override
        public List<BudAttachment> attachments()
        {
            synchronized( this ) {
                if ( attachments == null ) {
                    List<GridFSDBFile> budDBFiles = AttachmentsDB.getInstance().getBudDBFiles( bud.identity().get() );
                    List<BudAttachment> budAttachments = new ArrayList<BudAttachment>();
                    for ( GridFSDBFile eachDBFile : budDBFiles ) {
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
        public List<Role> roles()
        {
            // TODO
            return Collections.emptyList();
        }

    }

}
