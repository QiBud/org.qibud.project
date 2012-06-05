package org.qibud.eventstore;

import org.json.JSONObject;

/* package */ final class DomainEventImpl
        implements DomainEvent
{

    private final String name;

    private final JSONObject data;

    /* package */ DomainEventImpl( String name, JSONObject data )
    {
        this.name = name;
        this.data = data;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public JSONObject data()
    {
        return data;
    }

}
