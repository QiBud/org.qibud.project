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

import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;
import org.qi4j.api.value.ValueComposite;

@Mixins( Role.Mixin.class )
public interface Role
        extends ValueComposite
{

    Property<String> budPackName();

    Property<String> roleName();

    boolean is( String pack, String role );

    /**
     * @return budPackName() + "/" + roleName()
     */
    String codename();

    /**
     * @return budPackName() + "-" + roleName()
     */
    String idname();

    abstract class Mixin
            implements Role
    {

        @This
        private Role roleValue;

        @Override
        public boolean is( String pack, String role )
        {
            return roleValue.budPackName().get().equals( pack ) && roleValue.roleName().get().equals( role );
        }

        @Override
        public String codename()
        {
            return roleValue.budPackName().get() + "/" + roleValue.roleName().get();
        }

        @Override
        public String idname()
        {
            return roleValue.budPackName().get() + "-" + roleValue.roleName().get();
        }

    }

}
