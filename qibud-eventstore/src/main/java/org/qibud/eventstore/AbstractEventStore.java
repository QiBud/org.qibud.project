package org.qibud.eventstore;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.codeartisans.java.toolbox.exceptions.NullArgumentException;

public abstract class AbstractEventStore
        implements EventStore, EventSource, EventStream
{

    private final List<EventStreamListener> listeners = new CopyOnWriteArrayList<EventStreamListener>();

    @Override
    public final EventStreamRegistration registerEventStreamListener( final EventStreamListener listener )
    {
        NullArgumentException.ensureNotNull( "EventStreamListener", listener );
        listeners.add( listener );
        return new EventStreamRegistration()
        {

            @Override
            public void unregister()
            {
                listeners.remove( listener );
            }

        };
    }

    @Override
    public final void storeEvents( DomainEventsSequence domainEventsSequence )
    {
        doStoreEvents( domainEventsSequence );
        for ( EventStreamListener listener : listeners ) {
            listener.onDomainEventsSequence( domainEventsSequence );
        }
    }

    protected abstract void doStoreEvents( DomainEventsSequence domainEventsSequence );

}
