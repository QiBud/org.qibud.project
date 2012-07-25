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
package buds;

import com.mongodb.gridfs.GridFSDBFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.neo4j.graphdb.Node;
import roles.Role;
import storage.AttachmentsDB;
import storage.GraphDB;

public class Bud
{

    /* package */ static final String ROOT_BUD_IDENTITY = "root";

    private final BudEntity entity;

    private BudNode node;

    private List<BudAttachment> attachments;

    /* package */ Bud( BudEntity entity )
    {
        if ( entity == null ) {
            throw new IllegalArgumentException( "Cannot instanciate a Bud without its BudEntity (given was null)" );
        }
        this.entity = entity;
    }

    public String identity()
    {
        return entity.identity;
    }

    public BudEntity entity()
    {
        return entity;
    }

    public BudNode graphNode()
    {
        synchronized( this ) {
            if ( node == null ) {
                Node underlyingNode = GraphDB.getInstance().getBudNode( entity.identity );
                node = new BudNode( underlyingNode );
            }
        }
        return node;
    }

    public List<BudAttachment> attachments()
    {
        synchronized( this ) {
            if ( attachments == null ) {
                List<GridFSDBFile> budDBFiles = AttachmentsDB.getInstance().getBudDBFiles( entity.identity );
                List<BudAttachment> budAttachments = new ArrayList<BudAttachment>();
                for ( GridFSDBFile eachDBFile : budDBFiles ) {
                    budAttachments.add( new BudAttachment( eachDBFile ) );
                }
                attachments = budAttachments;
            }
        }
        return attachments;
    }

    public List<Role> roles()
    {
        // TODO
        return Collections.emptyList();
    }

    public final boolean isRoot()
    {
        return ROOT_BUD_IDENTITY.equals( entity.identity );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Bud other = ( Bud ) obj;
        if ( this.entity != other.entity && ( this.entity == null || !this.entity.equals( other.entity ) ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 79 * hash + ( this.entity != null ? this.entity.hashCode() : 0 );
        return hash;
    }

    @Override
    public String toString()
    {
        return "Bud[" + entity.identity + "]";
    }

}
