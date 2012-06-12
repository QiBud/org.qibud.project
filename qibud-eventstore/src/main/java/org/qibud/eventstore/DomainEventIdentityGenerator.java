package org.qibud.eventstore;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/* package */ class DomainEventIdentityGenerator
{

    /* package */ static final DomainEventIdentityGenerator EVENTS_ID_GEN = new DomainEventIdentityGenerator();

    private final String uuid = UUID.randomUUID().toString() + "-";

    private final AtomicLong count = new AtomicLong( 0L );

    /* package */ DomainEventIdentityGenerator()
    {
    }

    /* package */ String newIdentity()
    {
        return uuid + count.getAndIncrement();
    }

}
