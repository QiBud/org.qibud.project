package org.qibud.eventstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codeartisans.java.toolbox.Strings;

/**
 * Builder for DomainEventsSequence.
 * 
 * This builder is immutable, every method returns a new instance.
 */
public final class DomainEventsSequenceBuilder
{

    private final String usecase;

    private final String user;

    private final List<DomainEvent> events;

    /**
     * Create a fresh builder instance.
     */
    public DomainEventsSequenceBuilder()
    {
        this( null, null, null );
    }

    private DomainEventsSequenceBuilder( String usecase, String user, List<DomainEvent> events )
    {
        this.usecase = usecase;
        this.user = Strings.isEmpty( user ) ? null : user;
        this.events = events == null ? new ArrayList<DomainEvent>() : new ArrayList<DomainEvent>( events );
    }

    /**
     * @param usecase Mandatory usecase to set to the new builder instance.
     * @return A new builder instance with given usecase set.
     */
    public DomainEventsSequenceBuilder withUsecase( String usecase )
    {
        return new DomainEventsSequenceBuilder( usecase, user, events );
    }

    /**
     * @param user Optional user to set to the new builder instance.
     * @return A new builder instance with given user set.
     */
    public DomainEventsSequenceBuilder withUser( String user )
    {
        return new DomainEventsSequenceBuilder( usecase, user, events );
    }

    /**
     * @param events DomainEvents to add to the new builder instance.
     * @return A new builder instance with given DomainEvents added.
     */
    public DomainEventsSequenceBuilder withEvents( DomainEvent... events )
    {
        List<DomainEvent> newEventList = new ArrayList<DomainEvent>( this.events );
        newEventList.addAll( Arrays.asList( events ) );
        return new DomainEventsSequenceBuilder( usecase, user, newEventList );
    }

    /**
     * @return A new DomainEventsSequence instance builded according to this builder instance state.
     */
    public DomainEventsSequence build()
    {
        if ( Strings.isEmpty( usecase ) ) {
            throw new IllegalStateException( "usecase was null or empty" );
        }
        if ( events.isEmpty() ) {
            throw new IllegalStateException( "events was null or empty" );
        }
        return new DomainEventsSequenceImpl( System.currentTimeMillis(), usecase, user, events );
    }

}
