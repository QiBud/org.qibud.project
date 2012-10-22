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
import domain.roles.AbstractRoleAction;
import domain.roles.BudAction;
import domain.roles.BudActions;
import domain.roles.BudRole;
import domain.roles.Role;
import domain.roles.RoleActionException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import play.libs.Json;

@BudRole( name = "person", description = "Person Role" )
@BudActions( Person.Say.class )
@Mixins( Person.PersonMixin.class )
public interface Person
        extends Role
{

    String name();

    @BudAction( name = "say" )
    class Say
            extends AbstractRoleAction<Person>
    {

        @Override
        public ObjectNode invokeAction( Bud bud, Person role, ObjectNode param )
                throws RoleActionException
        {
            String message = param.get( "message" ).asText();
            ObjectNode result = nodeFactory.objectNode();
            result.put( "message", role.name() + " say '" + message + "'" );
            return result;
        }

    }

    abstract class PersonMixin
            implements Person
    {

        @This
        private Role role;

        @Override
        public String name()
        {
            JsonNode state = role.jsonRoleState();
            return state.has( "name" ) ? state.get( "name" ).getTextValue() : "Unknown";
        }

        @Override
        public void onCreate( Bud bud )
        {
            // Use Bud title as Person name by default
            ObjectNode state = JsonNodeFactory.instance.objectNode();
            state.put( "name", bud.title().get() );
            role.roleState().set( Json.stringify( state ) );
        }

    }

}
