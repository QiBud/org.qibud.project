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
package org.qibud.eventstore.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codeartisans.java.toolbox.io.IO;
import org.junit.Test;
import org.qibud.eventstore.DomainEvent;
import org.qibud.eventstore.DomainEventAttachment;
import org.qibud.eventstore.DomainEventFactory;
import org.qibud.eventstore.DomainEventsSequence;
import org.qibud.eventstore.DomainEventsSequenceBuilder;
import org.qibud.eventstore.EventStore;
import org.qibud.eventstore.EventStreamListener;

import static org.junit.Assert.*;

public abstract class AbstractEventStoreTest
{

    protected abstract EventStore newEventStore()
            throws Exception;

    @Test
    public void globalTestWithoutAttachments()
            throws Exception
    {
        EventStore eventStore = null;
        try {
            eventStore = newEventStore();
            DomainEventFactory eventFactory = new DomainEventFactory();

            System.out.println( "    >> EVENT STORE TEST :: GLOBAL -----------------------------------------------------" );
            Map<String, String> testData = new HashMap<String, String>();
            testData.put( "foo", "bar" );
            final List<DomainEventsSequence> listenedEvents = new ArrayList<DomainEventsSequence>();

            eventStore.registerEventStreamListener( new EventStreamListener()
            {

                @Override
                public void onDomainEventsSequence( DomainEventsSequence events )
                {
                    listenedEvents.add( events );
                }

            } );

            DomainEventsSequence events = new DomainEventsSequenceBuilder().withUsecase( "EventStore unit test" ).
                    withUser( "Build System" ).
                    withEvents( eventFactory.newDomainEvent( "First Event", testData ),
                                eventFactory.newDomainEvent( "Second Event", testData ) ).
                    build();

            eventStore.storeEvents( events );

            assertEquals( "EventStreamListener", 1, listenedEvents.size() );
            assertEquals( "ListenedEventsData", 1, listenedEvents.get( 0 ).events().get( 0 ).data().length() );
            assertEquals( "EventStoreCount", 1, eventStore.count() );
            assertEquals( "EventStoreFetch", 1, eventStore.eventsSequences( 0, Integer.MAX_VALUE ).size() );

            events = new DomainEventsSequenceBuilder().withUsecase( "EventStore unit test" ).
                    withUser( "Build System" ).
                    withEvents( eventFactory.newDomainEvent( "Third Event", testData ),
                                eventFactory.newDomainEvent( "Fourth Event", testData ) ).
                    build();

            eventStore.storeEvents( events );

            assertEquals( "EventStreamListener", 2, listenedEvents.size() );
            assertEquals( "EventStoreCount", 2, eventStore.count() );
            assertEquals( "EventStoreFetch", 2, eventStore.eventsSequences( 0, Integer.MAX_VALUE ).size() );

            StringWriter backupWriter = new StringWriter();
            eventStore.backup( backupWriter );
            String backup = backupWriter.toString();
            System.out.println( backup );
            eventStore.restore( new StringReader( backup ) );

            assertEquals( "EventStreamListener", 2, listenedEvents.size() );
            assertEquals( "EventStoreCount", 4, eventStore.count() );
            assertEquals( "EventStoreFetch", 4, eventStore.eventsSequences( 0, Integer.MAX_VALUE ).size() );

            eventStore.replay( 2, Integer.MAX_VALUE );
            assertEquals( "EventStreamListener", 4, listenedEvents.size() );

            backupWriter = new StringWriter();
            eventStore.backup( backupWriter );
            backup = backupWriter.toString();
            System.out.println( backup );

            eventStore.clear();
            eventStore.restore( new StringReader( backup ) );
            eventStore.replay( 0, Integer.MAX_VALUE );

            assertEquals( "EventStreamListener", 8, listenedEvents.size() );
            assertEquals( "EventStoreCount", 4, eventStore.count() );
            assertEquals( "EventStoreFetch", 4, eventStore.eventsSequences( 0, Integer.MAX_VALUE ).size() );

        } finally {
            if ( eventStore != null ) {
                eventStore.clear();
            }
        }
    }

    @Test
    public void testAttachments()
            throws Exception
    {
        EventStore eventStore = null;
        try {
            eventStore = newEventStore();
            DomainEventFactory eventFactory = new DomainEventFactory();

            System.out.println( "    >> EVENT STORE TEST :: ATTACHMENTS ------------------------------------------------" );
            Map<String, String> testData = new HashMap<String, String>();
            testData.put( "foo", "bar" );

            DomainEventAttachment attachment = eventFactory.newDomainEventAttachment( new DomainEventAttachment.DataProvider()
            {

                @Override
                public InputStream data()
                        throws IOException
                {
                    return new StringBufferInputStream( "TEST DATA" );
                }

            } );

            DomainEvent event = eventFactory.newDomainEvent( "My event", testData );
            event.data().put( "single-attachment", attachment.localIdentity() );

            DomainEventsSequence sequence = new DomainEventsSequenceBuilder().withUsecase( "EventStore attachments test" ).
                    withUser( "Build System" ).
                    withEvents( event ).
                    withAttachments( attachment ).build();

            eventStore.storeEvents( sequence );

            StringWriter backupWriter = new StringWriter();
            eventStore.backup( backupWriter );
            String backup = backupWriter.toString();
            System.out.println( backup );

            DomainEventsSequence fetchedSequence = eventStore.eventsSequence( 0 );
            assertFalse( "fetched sequence attachments empty", fetchedSequence.attachments().isEmpty() );
            assertEquals( "fetched seq att size", 1, fetchedSequence.attachments().size() );

            DomainEventAttachment fetchedAttachment = fetchedSequence.attachments().get( 0 );
            StringWriter writer = new StringWriter();
            InputStreamReader reader = new InputStreamReader( fetchedAttachment.data() );
            IO.copy( reader, writer );
            String readAttachment = writer.toString();
            assertEquals( "fetched att data is correct", "TEST DATA", readAttachment );
        } finally {
            if ( eventStore != null ) {
                eventStore.clear();
            }
        }

    }

}
