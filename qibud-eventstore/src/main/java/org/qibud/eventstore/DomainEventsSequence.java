package org.qibud.eventstore;

import java.util.List;

import org.json.JSONObject;

/**
 * A sequence of DomainEvents.
 * 
 * This is the only Aggregate in the EventStore BoundedContext.
 * 
 * It's identity is managed by the underlying EventStore and not exposed.
 * Agregated entities are DomainEvent and DomainEventAttachment.
 * Their identities are local to the DomainEventsSequence Aggregate.
 * DomainEvent references DomainEventAttachemnts using their local identity.
 */
public interface DomainEventsSequence
{

    /**
     * Timestamp of the sequence.
     */
    Long timestamp();

    /**
     * Usecase of the sequence.
     */
    String usecase();

    /**
     * Optional user string;
     */
    String user();

    /**
     * DomainEvents in this sequence as an immutable List.
     */
    List<DomainEvent> events();

    /**
     * Attachments of this DomainEventsSequence as an immutable List.
     */
    List<DomainEventAttachment> attachments();

    /**
     * Appends a DomainEvent to the sequence.
     */
    DomainEventsSequence events( DomainEvent event, DomainEventAttachment... attachments );

    /**
     * Serialize this DomainEvents sequence as a JSONObject.
     */
    JSONObject toJSON();

}
