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

import config.bootstrap.QiBudConfigAssemblies;
import domain.bootstrap.QiBudDomainAssemblies;
import infrastructure.bootstrap.QiBudInfraAssemblies;
import org.qi4j.api.structure.Application.Mode;
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

    public static final String LAYER_INFRASTRUCTURE = "infrastructure";

    public static final String MODULE_PERSISTENCE = "persistence";

    private final Mode mode;

    private final String mongoHostname;

    private final int mongoPort;

    private final String mongoUsername;

    private final String mongoPassword;

    private final String mongoDatabase;

    private final String mongoCollection;

    public QiBudAssembler( Mode mode, String mongoHostname, int mongoPort, String mongoUsername, String mongoPassword, String mongoDatabase, String mongoCollection )
    {
        this.mode = mode;
        this.mongoHostname = mongoHostname;
        this.mongoPort = mongoPort;
        this.mongoUsername = mongoUsername;
        this.mongoPassword = mongoPassword;
        this.mongoDatabase = mongoDatabase;
        this.mongoCollection = mongoCollection;
    }

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory aaf )
            throws AssemblyException
    {
        ApplicationAssembly appAss = aaf.newApplicationAssembly();
        appAss.setMode( mode );

        // Config
        LayerAssembly configLayer = appAss.layer( "config" );
        ModuleAssembly config = configLayer.module( "config" );
        QiBudConfigAssemblies.config().assemble( config );

        // Domain
        LayerAssembly domainLayer = appAss.layer( LAYER_DOMAIN );
        ModuleAssembly buds = domainLayer.module( MODULE_BUDS );
        QiBudDomainAssemblies.buds().assemble( buds );

        // Infrastructure
        LayerAssembly infraLayer = appAss.layer( LAYER_INFRASTRUCTURE );
        ModuleAssembly persistence = infraLayer.module( MODULE_PERSISTENCE );
        QiBudInfraAssemblies.persistence( config,
                                          mongoHostname, mongoPort,
                                          mongoUsername, mongoPassword,
                                          mongoDatabase, mongoCollection ).
                assemble( persistence );

        domainLayer.uses( infraLayer, configLayer );
        infraLayer.uses( configLayer );

        return appAss;
    }

}
