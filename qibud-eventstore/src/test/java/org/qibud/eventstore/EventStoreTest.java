package org.qibud.eventstore;

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
        assertEquals( "EventStoreFetch", 1, eventStore.eventsSequences( 0, 1 ).size() );
    }

}
