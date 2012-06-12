package org.qibud.eventstore;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EventStoreTest
{

    @Test
    public void test()
            throws IOException
    {
        Map<String, String> testData = new HashMap<String, String>();
        testData.put( "foo", "bar" );
        final List<DomainEventsSequence> listenedEvents = new ArrayList<DomainEventsSequence>();

        InMemoryEventStore eventStore = new InMemoryEventStore();
        DomainEventFactory eventFactory = new DomainEventFactory();

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

    }

}
