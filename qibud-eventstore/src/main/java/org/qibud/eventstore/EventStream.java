package org.qibud.eventstore;

public interface EventStream
{

    EventStreamRegistration registerEventStreamListener( EventStreamListener listener );

    /**
     * Re-dispatch DomainEventsSequence to registered EventStreamListeners.
     */
    int replay( int offset, int limit );

}
