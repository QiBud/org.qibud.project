package org.qibud.eventstore;

public interface EventStream
{

    EventStreamRegistration registerEventStreamListener( EventStreamListener listener );

}
