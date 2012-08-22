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
package domain.events;

import java.io.StringWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.qi4j.api.Qi4j;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.json.JSONSerializer;
import org.qi4j.api.json.JSONWriterSerializer;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import org.qi4j.api.type.ValueType;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.api.value.ValueDescriptor;
import org.qi4j.functional.Iterables;
import org.qibud.eventstore.DomainEventType;
import utils.QiBudException;

@Mixins( BudEvent.Mixin.class )
public interface BudEvent
        extends DomainEventType, ValueComposite
{

    Property<String> identity();

    class Mixin
            implements DomainEventType
    {

        @This
        private ValueComposite value;

        @Override
        public String eventType()
        {
            ValueDescriptor valueDescriptor = ( ValueDescriptor ) Qi4j.DESCRIPTOR_FUNCTION.map( value );
            ValueType valueType = valueDescriptor.valueType();
            String eventType = Iterables.first( valueType.types() ).getName();
            return eventType;
        }

        @Override
        public JSONObject eventData()
        {
            try {

                StringWriter writer = new StringWriter();
                JSONSerializer serializer = new JSONWriterSerializer( writer );
                serializer.serialize( value );
                return new JSONObject( writer.toString() );

            } catch ( JSONException ex ) {
                throw new QiBudException( "Unable to serialize domain event: " + ex.getMessage(), ex );
            }
        }

    }

}
