package buds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mongodb.gridfs.GridFSDBFile;

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
        synchronized ( this ) {
            if ( node == null ) {
                Node underlyingNode = GraphDB.getInstance().getBudNode( entity.identity );
                node = new BudNode( underlyingNode );
            }
        }
        return node;
    }

    public List<BudAttachment> attachments()
    {
        synchronized ( this ) {
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
