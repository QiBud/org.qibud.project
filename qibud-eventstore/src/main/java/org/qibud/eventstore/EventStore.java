package org.qibud.eventstore;

/**
 * EventStore used to store DomainEventsSequences.
 */
public interface EventStore
        extends EventSource, EventStream, EventStoreBackupRestore
{

    /**
     * Store a DomainEventsSequence.
     */
    void storeEvents( DomainEventsSequence domainEventsSequence );

    /**
     * Empty the EventStore, use with caution.
     */
    void clear();

}
