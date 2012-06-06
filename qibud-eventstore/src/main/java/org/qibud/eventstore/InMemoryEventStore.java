package org.qibud.eventstore;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryEventStore
        extends AbstractEventStore
{

    private List<DomainEventsSequence> events = new CopyOnWriteArrayList<DomainEventsSequence>();

    @Override
    protected void doStoreEvents( DomainEventsSequence domainEventsSequence )
    {
        events.add( domainEventsSequence );
    }

    @Override
    public List<DomainEventsSequence> eventsSequences( int offset, int limit )
    {
        int stop = offset + limit;
        if ( stop > events.size() ) {
            stop = events.size();
        }
        return Collections.unmodifiableList( events.subList( offset, stop ) );
    }

    @Override
    public int count()
    {
        return events.size();
    }

}
