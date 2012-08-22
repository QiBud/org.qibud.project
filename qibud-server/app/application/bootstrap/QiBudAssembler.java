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
package application.bootstrap;

import domain.bootstrap.QiBudDomainAssemblies;
import infrastructure.bootstrap.QiBudInfraAssemblies;
import org.qi4j.bootstrap.ApplicationAssembler;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.LayerAssembly;
import org.qi4j.bootstrap.ModuleAssembly;

public class QiBudAssembler
        implements ApplicationAssembler
{

    public static final String LAYER_DOMAIN = "domain";

    public static final String MODULE_BUDS = "buds";

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory aaf )
            throws AssemblyException
    {
        ApplicationAssembly appAss = aaf.newApplicationAssembly();

        // Domain
        LayerAssembly domainLayer = appAss.layer( "domain" );
        ModuleAssembly buds = domainLayer.module( "buds" );
        QiBudDomainAssemblies.buds().assemble( buds );

        // Infrastructure
        LayerAssembly infraLayer = appAss.layer( "infra" );
        ModuleAssembly persistence = infraLayer.module( "persistence" );
        QiBudInfraAssemblies.persistence().assemble( persistence );

        domainLayer.uses( infraLayer );

        return appAss;
    }

}
