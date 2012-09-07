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
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;

@Mixins( Role.Mixin.class )
public interface Role
        extends EntityComposite
{

    Property<String> roleName();

    @UseDefaults
    Property<List<RoleAction>> actions();

    boolean hasActionNamed( String name );

    RoleAction actionNamed( String name );

    abstract class Mixin
            implements Role
    {

        @This
        private Role me;

        @Override
        public boolean hasActionNamed( String name )
        {
            for ( RoleAction action : me.actions().get() ) {
                if ( action.actionName().get().equals( name ) ) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public RoleAction actionNamed( String name )
        {
            for ( RoleAction action : me.actions().get() ) {
                if ( action.actionName().get().equals( name ) ) {
                    return action;
                }
            }
            throw new RoleActionNotFound( name );
        }

    }

}
