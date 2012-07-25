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

import org.json.JSONObject;

/* package */ final class DomainEventImpl
        implements DomainEvent
{

    private final String localIdentity;

    private final String name;

    private final JSONObject data;


    /* package */ DomainEventImpl( String localIdentity, String name, JSONObject data )
    {
        this.localIdentity = localIdentity;
        this.name = name;
        this.data = data;
    }

    @Override
    public String localIdentity()
    {
        return localIdentity;
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

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final DomainEventImpl other = ( DomainEventImpl ) obj;
        if ( ( this.localIdentity == null ) ? ( other.localIdentity != null ) : !this.localIdentity.equals( other.localIdentity ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + ( this.localIdentity != null ? this.localIdentity.hashCode() : 0 );
        return hash;
    }

}
