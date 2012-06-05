package org.qibud.eventstore;

import org.json.JSONObject;

/**
 * DomainEvent.
 * 
 * A DomainEvent occurs in a DomainEventsSequence.
 */
public interface DomainEvent
{

    /**
     * Name of the DomainEvent.
     */
    String name();

    /**
     * DomainEvent data.
     */
    JSONObject data();

}
