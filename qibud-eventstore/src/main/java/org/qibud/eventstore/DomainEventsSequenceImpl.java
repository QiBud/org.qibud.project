package org.qibud.eventstore;

import java.util.ArrayList;
import java.util.List;

import org.codeartisans.java.toolbox.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* package */ final class DomainEventsSequenceImpl
        implements DomainEventsSequence
{

    private final Long timestamp;

    private final String usecase;

    private final String user;

    private final List<DomainEvent> events;

    /* package */ DomainEventsSequenceImpl( Long timestamp, String usecase, String user, List<DomainEvent> events )
    {
        this.timestamp = timestamp;
        this.usecase = usecase;
        this.user = user;
        this.events = events;
    }

    @Override
    public Long timestamp()
    {
        return timestamp;
    }

    @Override
    public String usecase()
    {
        return usecase;
    }

    @Override
    public String user()
    {
        return user;
    }

    @Override
    public List<DomainEvent> events()
    {
        return events;
    }

    @Override
    public JSONObject toJSON()
    {
        try {
            JSONObject eventsSequence = new JSONObject();
            eventsSequence.put( "timestamp", timestamp );
            eventsSequence.put( "usecase", usecase );
            eventsSequence.put( "user", Strings.isEmpty( user ) ? null : user );
            JSONArray eventsJson = new JSONArray();
            if ( events != null && !events.isEmpty() ) {
                for ( DomainEvent event : events ) {
                    JSONObject eventJson = new JSONObject();
                    eventJson.put( "name", event.name() );
                    eventJson.put( "data", event.data() );
                }
            }
            eventsSequence.put( "events", eventsJson );
            return eventsSequence;
        } catch ( JSONException ex ) {
            throw new EventStoreException( "Unable to serialize DomainEventsSequence to JSON.", ex );
        }
    }

    /* package */ static DomainEventsSequence fromJSON( JSONObject jsonObject )
            throws JSONException
    {
        long timestamp = jsonObject.getLong( "timestamp" );
        String usecase = jsonObject.getString( "usecase" );
        String user = jsonObject.optString( "user" );
        JSONArray eventsArray = jsonObject.getJSONArray( "events" );
        List<DomainEvent> events = new ArrayList<DomainEvent>();
        for ( int idx = 0; idx < eventsArray.length(); idx++ ) {
            JSONObject eventJson = eventsArray.getJSONObject( idx );
            String name = eventJson.getString( "name" );
            JSONObject data = eventJson.getJSONObject( "data" );
            events.add( new DomainEventImpl( name, data ) );
        }
        return new DomainEventsSequenceImpl( timestamp, usecase, Strings.isEmpty( user ) ? null : user, events );
    }

}
