package org.qibud.eventstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codeartisans.java.toolbox.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qibud.eventstore.DomainEventAttachment.DataLocator;
import org.qibud.eventstore.DomainEventAttachment.DataProvider;
import org.qibud.eventstore.DomainEventAttachment.DataProviderLocator;

/* package */ final class DomainEventsSequenceImpl
        implements DomainEventsSequence
{

    private final Long timestamp;

    private final String usecase;

    private final String user;

    private final List<DomainEvent> events;

    private final List<DomainEventAttachment> attachments;

    /* package */ DomainEventsSequenceImpl( Long timestamp, String usecase, String user, List<DomainEvent> events, List<DomainEventAttachment> attachments )
    {
        this.timestamp = timestamp;
        this.usecase = usecase;
        this.user = user;
        this.events = events;
        this.attachments = attachments;
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
        return Collections.unmodifiableList( events );
    }

    @Override
    public List<DomainEventAttachment> attachments()
    {
        return Collections.unmodifiableList( attachments );
    }

    @Override
    public DomainEventsSequence events( DomainEvent event, DomainEventAttachment... attachments )
    {
        this.events.add( event );
        this.attachments.addAll( Arrays.asList( attachments ) );
        return this;
    }

    @Override
    public JSONObject toJSON()
    {
        try {
            // Base data
            JSONObject eventsSequence = new JSONObject();
            eventsSequence.put( "timestamp", timestamp );
            eventsSequence.put( "usecase", usecase );
            eventsSequence.put( "user", Strings.isEmpty( user ) ? null : user );

            // Events
            JSONArray eventsJson = new JSONArray();
            if ( events != null && !events.isEmpty() ) {
                for ( DomainEvent event : events ) {
                    JSONObject eventJson = new JSONObject();
                    eventJson.put( "local_identity", event.localIdentity() );
                    eventJson.put( "name", event.name() );
                    eventJson.put( "data", event.data() );
                    eventsJson.put( eventJson );
                }
            }

            // Attachments
            JSONArray attachmentsArray = new JSONArray();
            for ( DomainEventAttachment attachment : attachments ) {
                attachmentsArray.put( attachment.localIdentity() );
            }

            // Russian dolls
            eventsSequence.put( "events", eventsJson );
            eventsSequence.put( "attachments", attachmentsArray );

            return eventsSequence;
        } catch ( JSONException ex ) {
            throw new EventStoreException( "Unable to serialize DomainEventsSequence to JSON.", ex );
        }
    }

    /* package */ static DomainEventsSequence fromJSON( JSONObject json, DataLocator dataLocator )
            throws JSONException
    {
        // Base data
        long timestamp = json.getLong( "timestamp" );
        String usecase = json.getString( "usecase" );
        String user = json.optString( "user" );

        // Events
        JSONArray eventsArray = json.getJSONArray( "events" );
        List<DomainEvent> events = new ArrayList<DomainEvent>();
        for ( int index = 0; index < eventsArray.length(); index++ ) {
            JSONObject eventJson = eventsArray.getJSONObject( index );
            String localIdentity = eventJson.getString( "local_identity" );
            String name = eventJson.getString( "name" );
            JSONObject data = eventJson.getJSONObject( "data" );
            events.add( new DomainEventImpl( localIdentity, name, data ) );
        }

        // Attachments
        JSONArray attachmentsArray = json.getJSONArray( "attachments" );
        List<DomainEventAttachment> attachments = new ArrayList<DomainEventAttachment>();
        for ( int index = 0; index < attachmentsArray.length(); index++ ) {
            String attachmentLocalIdentity = attachmentsArray.getString( index );
            DataProvider dataProvider = new DataProviderLocator( attachmentLocalIdentity, dataLocator );
            DomainEventAttachment attachment = new DomainEventAttachment( attachmentLocalIdentity, dataProvider );
            attachments.add( attachment );
        }

        return new DomainEventsSequenceImpl( timestamp, usecase, Strings.isEmpty( user ) ? null : user, events, attachments );
    }

}
