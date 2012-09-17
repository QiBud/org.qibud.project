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
package domain.budpacks.project;

import domain.buds.Bud;
import domain.roles.BudAction;
import domain.roles.BudActions;
import domain.roles.BudRole;
import domain.roles.Role;
import domain.roles.RoleAction;
import org.qi4j.api.common.Optional;
import org.qi4j.api.property.Property;

@BudRole( name = "person", description = "Person Role" )
@BudActions( Person.Say.class )
public interface Person
        extends Role
{

    @Optional
    Property<String> fullName();

    @BudAction( name = "say" )
    class Say
            implements RoleAction<Person, String, String, Throwable>
    {

        @Override
        public String invokeAction( Bud bud, Person role, String message )
                throws Throwable
        {
            return role.fullName().get() + " say " + message;
        }

    }

}
