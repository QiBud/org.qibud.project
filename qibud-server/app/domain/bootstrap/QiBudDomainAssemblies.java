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

import domain.events.BudContentChangedEvent;
import domain.events.BudCreatedEvent;
import domain.events.BudEventFactoryService;
import domain.events.BudTitleChangedEvent;
import domain.events.RootBudCreatedEvent;
import org.qi4j.api.service.importer.InstanceImporter;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qibud.eventstore.DomainEventFactory;

import static org.qi4j.api.common.Visibility.application;

public class QiBudDomainAssemblies
{

    public static Assembler eventsAssembler()
    {
        return new Assembler()
        {

            @Override
            public void assemble( ModuleAssembly ma )
                    throws AssemblyException
            {
                // Events
                ma.values( RootBudCreatedEvent.class,
                           BudCreatedEvent.class,
                           BudTitleChangedEvent.class,
                           BudContentChangedEvent.class ).
                        visibleIn( application );

                // Services
                ma.services( BudEventFactoryService.class ).
                        visibleIn( application );
                ma.importedServices( DomainEventFactory.class ).
                        importedBy( InstanceImporter.class ).
                        setMetaInfo( new DomainEventFactory() );
            }

        };
    }

}
