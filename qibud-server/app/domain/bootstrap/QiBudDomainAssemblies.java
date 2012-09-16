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

import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import domain.roles.BudRole;
import domain.roles.RoleAction;
import domain.roles.RoleActionDescriptor;
import domain.roles.RoleDescriptor;
import domain.roles.RoleRegistry;
import java.util.ArrayList;
import java.util.List;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import play.Play;
import utils.ClassFinder;

import static org.qi4j.api.common.Visibility.application;

public final class QiBudDomainAssemblies
{

    public static Assembler buds( final String[] budPackPackages )
    {
        return new Assembler()
        {

            @Override
            public void assemble( ModuleAssembly ma )
                    throws AssemblyException
            {
                ma.entities( Bud.class ).
                        visibleIn( application );

                List<Class<?>> roles = new ArrayList<Class<?>>();
                for ( String budPackPackage : budPackPackages ) {
                    Class<?>[] candidates = ClassFinder.getClasses( budPackPackage, Play.application().classloader() );
                    for ( Class<?> candidate : candidates ) {
                        if ( candidate.isAnnotationPresent( BudRole.class ) ) {
                            roles.add( candidate );
                        }
                    }
                }
                if ( !roles.isEmpty() ) {
                    ma.entities( Bud.class ).
                            withTypes( roles.toArray( new Class<?>[ roles.size() ] ) );
                }

                ma.values( RoleDescriptor.class,
                           RoleActionDescriptor.class,
                           RoleAction.class ).
                        visibleIn( application );

                ma.services( BudsRepository.class,
                             BudsFactory.class,
                             RoleRegistry.class ).
                        visibleIn( application ).
                        instantiateOnStartup();
            }

        };
    }

    private QiBudDomainAssemblies()
    {
    }

}
