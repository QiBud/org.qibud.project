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
        long temp = ( long ) offset + ( long ) limit;
        int stop;
        if ( temp > Integer.MAX_VALUE || temp > events.size() ) {
            stop = events.size();
        } else {
            stop = ( int ) temp;
        }
        return Collections.unmodifiableList( events.subList( offset, stop > Integer.MAX_VALUE ? Integer.MAX_VALUE : new Long( stop ).intValue() ) );
    }

    @Override
    public int count()
    {
        return events.size();
    }

    void clear()
    {
        events.clear();
    }

}
