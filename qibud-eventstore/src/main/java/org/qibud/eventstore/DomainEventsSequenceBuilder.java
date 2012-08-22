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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codeartisans.java.toolbox.Strings;

/**
 * Builder for DomainEventsSequences.
 * 
 * This builder is immutable, every method returns a new instance.
 */
public final class DomainEventsSequenceBuilder
{

    private final String usecase;

    private final String user;

    private final List<DomainEvent> events;

    private final List<DomainEventAttachment> attachments;

    /**
     * Create a fresh builder instance.
     */
    public DomainEventsSequenceBuilder()
    {
        this( null, null, null, null );
    }

    private DomainEventsSequenceBuilder( String usecase, String user, List<DomainEvent> events, List<DomainEventAttachment> attachments )
    {
        this.usecase = usecase;
        this.user = user == null ? "" : user;
        this.events = events == null ? new ArrayList<DomainEvent>() : new ArrayList<DomainEvent>( events );
        this.attachments = attachments == null ? new ArrayList<DomainEventAttachment>() : new ArrayList<DomainEventAttachment>( attachments );
    }

    /**
     * @param usecase Mandatory usecase to set to the new builder instance.
     * @return A new builder instance with given usecase set.
     */
    public DomainEventsSequenceBuilder withUsecase( String usecase )
    {
        return new DomainEventsSequenceBuilder( usecase, user, events, attachments );
    }

    /**
     * @param user Optional user to set to the new builder instance.
     * @return A new builder instance with given user set.
     */
    public DomainEventsSequenceBuilder withUser( String user )
    {
        return new DomainEventsSequenceBuilder( usecase, user, events, attachments );
    }

    /**
     * @param events DomainEvents to add to the new builder instance.
     * @return A new builder instance with given DomainEvents added.
     */
    public DomainEventsSequenceBuilder withEvents( DomainEvent... events )
    {
        List<DomainEvent> newEventList = new ArrayList<DomainEvent>( this.events );
        newEventList.addAll( Arrays.asList( events ) );
        return new DomainEventsSequenceBuilder( usecase, user, newEventList, attachments );
    }

    /**
     * @param eventsStates DomainEventStates to add to the new builder instance.
     * @return A new builder instance with given DomainEvents added.
     */
    public DomainEventsSequenceBuilder withEventsStates( DomainEventType... eventsStates )
    {
        List<DomainEvent> newEventList = new ArrayList<DomainEvent>( this.events );
        DomainEventFactory domainEventFactory = new DomainEventFactory();
        for ( DomainEventType eventState : eventsStates ) {
            newEventList.add( domainEventFactory.newDomainEvent( eventState ) );
        }
        return new DomainEventsSequenceBuilder( usecase, user, newEventList, attachments );
    }

    public DomainEventsSequenceBuilder withEventsStates( Iterable<? extends DomainEventType> eventsStates )
    {
        List<DomainEvent> newEventList = new ArrayList<DomainEvent>( this.events );
        DomainEventFactory domainEventFactory = new DomainEventFactory();
        for ( DomainEventType eventState : eventsStates ) {
            newEventList.add( domainEventFactory.newDomainEvent( eventState ) );
        }
        return new DomainEventsSequenceBuilder( usecase, user, newEventList, attachments );
    }

    /**
     * @param events DomainEventAttachments to add to the new builder instance.
     * @return A new builder instance with given DomainEventAttachments added.
     */
    public DomainEventsSequenceBuilder withAttachments( DomainEventAttachment... attachments )
    {
        List<DomainEventAttachment> newAttachmentList = new ArrayList<DomainEventAttachment>( this.attachments );
        newAttachmentList.addAll( Arrays.asList( attachments ) );
        return new DomainEventsSequenceBuilder( usecase, user, events, newAttachmentList );
    }

    /**
     * @return A new DomainEventsSequence instance builded according to this builder instance state.
     */
    public DomainEventsSequence build()
    {
        if ( Strings.isEmpty( usecase ) ) {
            throw new IllegalStateException( "usecase was null or empty" );
        }
        return new DomainEventsSequenceImpl( System.currentTimeMillis(), usecase, user, events, attachments );
    }

}
