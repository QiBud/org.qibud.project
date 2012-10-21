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
package domain.budpacks;

import application.bootstrap.BudPackDescriptor;
import application.bootstrap.RoleDescriptor;
import domain.buds.Bud;
import domain.roles.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.Uses;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.service.ServiceDescriptor;
import org.qi4j.api.structure.Module;
import play.libs.Json;

@Mixins( BudPacksService.Mixin.class )
public interface BudPacksService
        extends ServiceComposite, ServiceActivation
{

    Collection<BudPackDescriptor> budPacks();

    BudPackDescriptor budPack( String name );

    Collection<RoleDescriptor> roles();

    Collection<RoleDescriptor> unusedRoles( Bud bud );

    Role newRoleInstance( Bud bud, String budPackName, String roleName );

    abstract class Mixin
            implements BudPacksService
    {

        @Structure
        private Module module;

        @Uses
        private ServiceDescriptor descriptor;

        private Map<String, BudPackDescriptor> budPacks;

        private Map<String, RoleDescriptor> roles;

        @Override
        public void activateService()
                throws Exception
        {
            budPacks = descriptor.metaInfo( Map.class );
            roles = new HashMap<String, RoleDescriptor>();
            for ( BudPackDescriptor budPack : budPacks.values() ) {
                roles.putAll( budPack.roles() );
            }
        }

        @Override
        public void passivateService()
                throws Exception
        {
            budPacks = null;
            roles = null;
        }

        @Override
        public Collection<BudPackDescriptor> budPacks()
        {
            return budPacks.values();
        }

        @Override
        public Collection<RoleDescriptor> roles()
        {
            return roles.values();
        }

        @Override
        public BudPackDescriptor budPack( String name )
        {
            return budPacks.get( name );
        }

        @Override
        public Collection<RoleDescriptor> unusedRoles( Bud bud )
        {
            List<RoleDescriptor> unused = new ArrayList<RoleDescriptor>( roles.values() );
            for ( Role budRole : bud.roles() ) {
                Iterator<RoleDescriptor> it = unused.iterator();
                while ( it.hasNext() ) {
                    RoleDescriptor availableRole = it.next();
                    if ( availableRole.budPackName().equals( budRole.budPackName().get() )
                         && availableRole.name().equals( budRole.roleName().get() ) ) {
                        it.remove();
                    }
                }
            }
            return unused;
        }

        @Override
        public Role newRoleInstance( Bud bud, String budPackName, String roleName )
        {
            RoleDescriptor roleDescriptor = budPacks.get( budPackName ).roles().get( roleName );
            EntityBuilder<? extends Role> roleBuilder = module.currentUnitOfWork().newEntityBuilder( roleDescriptor.roleType() );
            Role role = roleBuilder.instance();
            role.budPackName().set( roleDescriptor.budPackName() );
            role.roleName().set( roleDescriptor.name() );
            role.roleState().set( Json.stringify( Json.newObject() ) );
            role = roleBuilder.newInstance();
            role.onCreate( bud );
            return role;
        }

    }

}
