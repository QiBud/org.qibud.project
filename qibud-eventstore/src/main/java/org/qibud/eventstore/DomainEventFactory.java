package org.qibud.eventstore;

import java.util.Map;

import org.codeartisans.java.toolbox.exceptions.NullArgumentException;
import org.json.JSONObject;
import org.qibud.eventstore.DomainEventAttachment.DataProvider;

/**
 * Factory for DomainEvent.
 */
public class DomainEventFactory
{

    private final DomainEventIdentityGenerator localIdGen = new DomainEventIdentityGenerator();

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
        NullArgumentException.ensureNotEmpty( "name", name );
        NullArgumentException.ensureNotNull( "data", data );
        return new DomainEventImpl( localIdGen.newIdentity(), name, data );
    }

    /**
     * Create a new DomainEventAttachment.
     */
    public final DomainEventAttachment newDomainEventAttachment( DataProvider dataProvider )
    {
        NullArgumentException.ensureNotNull( "data provider", dataProvider );
        return new DomainEventAttachment( localIdGen.newIdentity(), dataProvider );
    }

}
