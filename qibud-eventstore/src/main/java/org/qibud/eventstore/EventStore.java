package org.qibud.eventstore;

/**
 * EventStore used to store DomainEventsSequences.
 */
public interface EventStore
{

    /**
     * Store a DomainEventsSequence.
     */
    void storeEvents( DomainEventsSequence domainEventsSequence );

}
