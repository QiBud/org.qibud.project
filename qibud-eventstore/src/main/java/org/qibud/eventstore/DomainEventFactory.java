package org.qibud.eventstore;

import java.util.Map;

import org.codeartisans.java.toolbox.Strings;
import org.json.JSONObject;

/**
 * Factory for DomainEvent.
 */
public class DomainEventFactory
{

    /**
     * Create a new DomainEvent.
     */
    public final DomainEvent newDomainEvent( String name, Map<String, String> data )
    {
        return newDomainEvent( name, new JSONObject( data ) );
    }

    /**
     * Create a new DomainEvent.
     */
    public final DomainEvent newDomainEvent( String name, JSONObject data )
    {
        if ( Strings.isEmpty( name ) ) {
            throw new IllegalArgumentException( "name was null or empty" );
        }
        if ( data == null ) {
            throw new IllegalArgumentException( "data was null" );
        }
        return new DomainEventImpl( name, data );
    }

}
