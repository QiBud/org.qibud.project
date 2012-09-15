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
package domain.roles;

import java.util.List;
import org.qi4j.api.common.UseDefaults;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import org.qi4j.api.value.ValueComposite;
import utils.QiBudException;

@Mixins( RoleDescriptor.Mixin.class )
public interface RoleDescriptor
        extends ValueComposite
{

    Property<String> name();

    Property<String> roleTypeName();

    Class<? extends Role> roleType();

    @UseDefaults
    Property<String> description();

    @UseDefaults
    Property<List<RoleActionDescriptor>> actions();

    abstract class Mixin
            implements RoleDescriptor
    {

        @This
        private RoleDescriptor state;

        @Override
        public Class<? extends Role> roleType()
        {
            try {
                return ( Class<? extends Role> ) Class.forName( state.roleTypeName().get() );
            } catch ( ClassNotFoundException ex ) {
                throw new QiBudException( "Unable to find registered Role class: " + ex.getMessage(), ex );
            }
        }

    }

}
