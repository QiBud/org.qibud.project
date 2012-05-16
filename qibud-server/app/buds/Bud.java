package buds;

import java.util.Collections;
import java.util.List;

import org.neo4j.graphdb.Node;

import models.BudEntity;
import roles.Role;

public class Bud
{

    /* package */ static final String ROOT_BUD_IDENTITY = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXY";

    private final BudEntity entity;

    private BudNode node;

    /* package */ Bud( BudEntity entity )
    {
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
                Node underlyingNode = null; // TODO
                node = new BudNode( underlyingNode );
            }
        }
        return node;
    }

    public List<BudAttachment> attachments()
    {
        return null;
    }

    public List<Role> roles()
    {
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

}
