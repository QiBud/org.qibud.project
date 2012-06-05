package org.qibud.eventstore;

public class EventStoreException
        extends RuntimeException
{

    public EventStoreException( String string )
    {
        super( string );
    }

    public EventStoreException( Throwable thrwbl )
    {
        super( thrwbl.getMessage(), thrwbl );
    }

    public EventStoreException( String string, Throwable thrwbl )
    {
        super( string, thrwbl );
    }

}
