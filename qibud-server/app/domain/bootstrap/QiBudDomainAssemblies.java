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

import domain.aaa.User;
import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import domain.roles.RoleAction;
import domain.roles.RoleActionDescriptor;
import domain.roles.RoleDescriptor;
import domain.roles.RoleRegistry;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;

import static org.qi4j.api.common.Visibility.application;

public final class QiBudDomainAssemblies
{

    public static Assembler buds()
    {
        return new Assembler()
        {

            @Override
            public void assemble( ModuleAssembly ma )
                    throws AssemblyException
            {
                ma.entities( Bud.class,
                             User.class ).
                        visibleIn( application );

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
