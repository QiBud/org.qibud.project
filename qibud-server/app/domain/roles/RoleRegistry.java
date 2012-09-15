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

import domain.aaa.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.value.ValueBuilder;

@Mixins( RoleRegistry.Mixin.class )
public interface RoleRegistry
        extends ServiceComposite, ServiceActivation
{

    Iterable<String> registeredRolesNames();

    Iterable<RoleDescriptor> registeredRoles();

    Role newRoleInstance( String roleName );

    abstract class Mixin
            implements RoleRegistry
    {

        @Structure
        private Module module;

        private Map<String, RoleDescriptor> roleMap;

        @Override
        public void activateService()
                throws Exception
        {
            roleMap = new HashMap<String, RoleDescriptor>();
            ValueBuilder<RoleDescriptor> builder = module.newValueBuilder( RoleDescriptor.class );
            RoleDescriptor roleDescriptor = builder.prototype();
            roleDescriptor.name().set( "user" );
            roleDescriptor.description().set( "QiBud User Role" );
            roleDescriptor.roleTypeName().set( User.class.getName() );
            roleMap.put( "user", builder.newInstance() );
        }

        @Override
        public void passivateService()
                throws Exception
        {
            roleMap = Collections.emptyMap();
        }

        @Override
        public Iterable<String> registeredRolesNames()
        {
            return roleMap.keySet();
        }

        @Override
        public Iterable<RoleDescriptor> registeredRoles()
        {
            return roleMap.values();
        }

        @Override
        public Role newRoleInstance( String roleName )
        {
            RoleDescriptor roleDescriptor = roleMap.get( roleName );
            UnitOfWork uow = module.currentUnitOfWork();
            EntityBuilder<? extends Role> builder = uow.newEntityBuilder( roleDescriptor.roleType() );
            Role role = builder.instance();
            role.roleName().set( roleDescriptor.name().get() );
            return builder.newInstance();
        }

    }

}
