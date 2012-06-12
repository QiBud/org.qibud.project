package org.qibud.eventstore;

import org.json.JSONObject;

/* package */ final class DomainEventImpl
        implements DomainEvent
{

    private final String localIdentity;

    private final String name;

    private final JSONObject data;


    /* package */ DomainEventImpl( String localIdentity, String name, JSONObject data )
    {
        this.localIdentity = localIdentity;
        this.name = name;
        this.data = data;
    }

    @Override
    public String localIdentity()
    {
        return localIdentity;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public JSONObject data()
    {
        return data;
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
        final DomainEventImpl other = ( DomainEventImpl ) obj;
        if ( ( this.localIdentity == null ) ? ( other.localIdentity != null ) : !this.localIdentity.equals( other.localIdentity ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + ( this.localIdentity != null ? this.localIdentity.hashCode() : 0 );
        return hash;
    }

}
