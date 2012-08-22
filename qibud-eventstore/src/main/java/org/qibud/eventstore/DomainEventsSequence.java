/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
