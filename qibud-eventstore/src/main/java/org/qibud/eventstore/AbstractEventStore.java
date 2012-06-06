package org.qibud.eventstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.codeartisans.java.toolbox.Strings;
import org.codeartisans.java.toolbox.exceptions.NullArgumentException;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractEventStore
        implements EventStore, EventSource, EventStream, EventStoreBackupRestore
{

    private final List<EventStreamListener> listeners = new CopyOnWriteArrayList<EventStreamListener>();

    @Override
    public final EventStreamRegistration registerEventStreamListener( final EventStreamListener listener )
    {
        NullArgumentException.ensureNotNull( "EventStreamListener", listener );
        listeners.add( listener );
        return new EventStreamRegistration()
        {

            @Override
            public void unregister()
            {
                listeners.remove( listener );
            }

        };
    }

    @Override
    public final void storeEvents( DomainEventsSequence domainEventsSequence )
    {
        doStoreEvents( domainEventsSequence );
        dispatch( domainEventsSequence );
    }

    @Override
    public final int replay( int offset, int limit )
    {
        int replayed = 0;
        for ( DomainEventsSequence domainEventsSequence : eventsSequences( offset, limit ) ) {
            dispatch( domainEventsSequence );
            replayed++;
        }
        return replayed;
    }

    @Override
    public final int backup( Writer writer )
            throws IOException
    {
        int backuped = 0;
        for ( DomainEventsSequence domainEventsSequence : eventsSequences( 0, count() ) ) {
            writer.write( domainEventsSequence.toJSON().toString() );
            writer.write( Strings.NEWLINE );
            backuped++;
        }
        return backuped;
    }

    @Override
    public final int restore( Reader reader )
            throws IOException
    {
        try {
            BufferedReader bufferedReader = new BufferedReader( reader );
            String line = bufferedReader.readLine();
            int restored = 0;
            while ( line != null ) {
                JSONObject json = new JSONObject( line );
                DomainEventsSequence events = DomainEventsSequenceImpl.fromJSON( json );
                doStoreEvents( events ); // No dispatch!
                restored++;
                line = bufferedReader.readLine();
            }
            return restored;
        } catch ( JSONException ex ) {
            throw new IllegalArgumentException( "Data is not in a valid format.", ex );
        }
    }

    private void dispatch( DomainEventsSequence domainEventsSequence )
    {
        for ( EventStreamListener listener : listeners ) {
            listener.onDomainEventsSequence( domainEventsSequence );
        }
    }

    protected abstract void doStoreEvents( DomainEventsSequence domainEventsSequence );

}
