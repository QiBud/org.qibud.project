package org.qibud.eventstore;

public interface EventStreamListener
{

    void onDomainEventsSequence( DomainEventsSequence events );

}
