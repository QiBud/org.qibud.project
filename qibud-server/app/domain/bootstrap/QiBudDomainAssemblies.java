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
package domain.bootstrap;

import application.bootstrap.BudPackDescriptor;
import application.bootstrap.RoleActionDescriptor;
import application.bootstrap.RoleDescriptor;
import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import domain.roles.Role;
import domain.roles.RoleAction;
import domain.roles.RoleRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.qi4j.api.common.Visibility.application;

public final class QiBudDomainAssemblies
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.domain.buds" );

    public static Assembler buds( final Map<String, BudPackDescriptor> budPacks )
    {
        return new Assembler()
        {

            @Override
            public void assemble( ModuleAssembly ma )
                    throws AssemblyException
            {
                ma.entities( Bud.class ).
                        visibleIn( application );

                List<Class<? extends Role>> roleTypes = new ArrayList<Class<? extends Role>>();
                List<Class<? extends RoleAction>> roleActionTypes = new ArrayList<Class<? extends RoleAction>>();
                for ( BudPackDescriptor budPack : budPacks.values() ) {
                    for ( RoleDescriptor role : budPack.roles().values() ) {
                        roleTypes.add( role.roleType() );
                        for ( RoleActionDescriptor action : role.actions().values() ) {
                            roleActionTypes.add( action.roleActionType() );
                        }
                    }
                }

                if ( !roleTypes.isEmpty() ) {
                    Class<?>[] rolesArray = roleTypes.toArray( new Class<?>[ roleTypes.size() ] );
                    ma.entities( rolesArray );
                    LOGGER.info( "Assembled {} BudRoles: {}", rolesArray.length, Arrays.toString( rolesArray ) );
                }

                if ( !roleActionTypes.isEmpty() ) {
                    Class<?>[] actionsArray = roleActionTypes.toArray( new Class<?>[ roleActionTypes.size() ] );
                    ma.transients( actionsArray );
                    LOGGER.info( "Assembled {} BudActions: {}", actionsArray.length, Arrays.toString( actionsArray ) );
                }

                ma.services( BudsRepository.class,
                             BudsFactory.class,
                             RoleRegistry.class ).
                        visibleIn( application ).
                        instantiateOnStartup();

                ma.services( RoleRegistry.class ).setMetaInfo( budPacks );
            }

        };
    }

    private QiBudDomainAssemblies()
    {
    }

}
