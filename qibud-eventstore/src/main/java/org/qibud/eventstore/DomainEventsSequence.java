package org.qibud.eventstore;

import java.util.List;

import org.json.JSONObject;

/**
 * DomainEvents sequence.
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
     * DomainEvents in this sequence.
     */
    List<DomainEvent> events();

    /**
     * Serialize this DomainEvents sequence as a JSONObject.
     */
    JSONObject toJSON();

}
