package org.qibud.eventstore;

import java.util.List;

/**
 * EventSource used to retrieve DomainEventsSequences.
 */
public interface EventSource
{

    /**
     * @param offset Offset of the DomainEventsSequences to retrieve.
     * @param limit Maximum number of DomainEventsSequences to retrieve.
     * @return A List of DomainEventsSequences.
     */
    List<DomainEventsSequence> eventsSequences( int offset, int limit );

    /**
     * @return How many DomainEventsSequences are stored in this EventSource.
     */
    long count();

}
