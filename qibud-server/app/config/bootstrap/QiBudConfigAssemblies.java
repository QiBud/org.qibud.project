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
package config.bootstrap;

import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.library.fileconfig.FileConfigurationService;

import static org.qi4j.api.common.Visibility.application;
import static org.qi4j.api.common.Visibility.module;

public final class QiBudConfigAssemblies
{

    /**
     * Qi4j application configuration resides in-memory as we use the Play!
     * configuration values to default Qi4j configuration properties at
     * assembly time.
     *
     * See other assemblers that take a config module assembly parameter.
     *
     * @return Assembler of the config layer.
     */
    public static Assembler config()
    {
        return new Assembler()
        {

            @Override
            public void assemble( ModuleAssembly ma )
                throws AssemblyException
            {
                ma.services( MemoryEntityStoreService.class ).
                    visibleIn( module ).
                    instantiateOnStartup();

                ma.services( FileConfigurationService.class ).
                    visibleIn( application ).
                    instantiateOnStartup();
            }

        };
    }

    private QiBudConfigAssemblies()
    {
    }

}
