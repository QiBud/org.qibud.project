package org.qibud.eventstore.test;

import org.qibud.eventstore.EventStore;
import org.qibud.eventstore.InMemoryEventStore;

public class InMemoryEventStoreTest
        extends AbstractEventStoreTest
{

    @Override
    protected EventStore newEventStore()
    {
        return new InMemoryEventStore();
    }

}
