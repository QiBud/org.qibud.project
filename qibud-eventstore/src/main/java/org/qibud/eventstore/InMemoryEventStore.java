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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.qibud.eventstore.DomainEventAttachment.DataLocator;

public class InMemoryEventStore
        extends AbstractEventStore
{

    private List<DomainEventsSequence> events = new CopyOnWriteArrayList<DomainEventsSequence>();

    @Override
    protected void doStoreEvents( DomainEventsSequence domainEventsSequence )
    {
        events.add( domainEventsSequence );
    }

    @Override
    public List<DomainEventsSequence> eventsSequences( int offset, int limit )
    {
        long temp = ( long ) offset + ( long ) limit;
        int stop;
        if ( temp > Integer.MAX_VALUE || temp > events.size() ) {
            stop = events.size();
        } else {
            stop = ( int ) temp;
        }
        return Collections.unmodifiableList( events.subList( offset, stop > Integer.MAX_VALUE ? Integer.MAX_VALUE : new Long( stop ).intValue() ) );
    }

    @Override
    public int count()
    {
        return events.size();
    }

    @Override
    public void clear()
    {
        events.clear();
    }

    @Override
    protected DataLocator attachmentDataLocator()
    {
        return new DataLocator()
        {

            @Override
            public InputStream data( String attachmentLocalIdentity )
                    throws IOException
            {
                for ( DomainEventsSequence seq : events ) {
                    for ( DomainEventAttachment att : seq.attachments() ) {
                        if ( att.localIdentity().equals( attachmentLocalIdentity ) ) {
                            return att.data();
                        }
                    }
                }
                return null;
            }

        };
    }

}
