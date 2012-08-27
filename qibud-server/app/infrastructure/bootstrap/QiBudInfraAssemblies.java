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
package infrastructure.bootstrap;

import infrastructure.attachmentsdb.AttachmentsDBService;
import infrastructure.graphdb.GraphDBService;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.mongodb.MongoEntityStoreConfiguration;
import org.qi4j.entitystore.mongodb.MongoMapEntityStoreAssembler;
import org.qi4j.index.rdf.assembly.RdfMemoryStoreAssembler;

import static org.qi4j.api.common.Visibility.application;

public final class QiBudInfraAssemblies
{

    public static Assembler persistence( final ModuleAssembly configModule,
                                         final String mongoHostname, final int mongoPort,
                                         final String mongoUsername, final String mongoPassword,
                                         final String mongoDatabase, final String mongoCollection )
    {
        return new Assembler()
        {

            @Override
            public void assemble( ModuleAssembly ma )
                    throws AssemblyException
            {
                // Entities Storage
                new MongoMapEntityStoreAssembler().withVisibility( application ).
                        withConfigModule( configModule ).
                        withConfigVisibility( application ).
                        assemble( ma );
                MongoEntityStoreConfiguration mongoESConf = ma.forMixin( MongoEntityStoreConfiguration.class ).
                        declareDefaults();
                mongoESConf.hostname().set( mongoHostname );
                mongoESConf.port().set( mongoPort );
                mongoESConf.username().set( mongoUsername );
                mongoESConf.password().set( mongoPassword );
                mongoESConf.database().set( mongoDatabase );
                mongoESConf.collection().set( mongoCollection );

                // Entities Indexing & Query
                new RdfMemoryStoreAssembler().assemble( ma );

                // Attachments
                ma.services( AttachmentsDBService.class ).
                        visibleIn( application ).
                        instantiateOnStartup();

                // Graph
                ma.services( GraphDBService.class ).
                        visibleIn( application ).
                        instantiateOnStartup();
            }

        };
    }

    private QiBudInfraAssemblies()
    {
    }

}
