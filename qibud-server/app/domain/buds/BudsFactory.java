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
package domain.buds;

import domain.aaa.Account;
import infrastructure.attachmentsdb.AttachmentsDB;
import infrastructure.graphdb.GraphDB;
import java.io.InputStream;
import org.joda.time.DateTime;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

@Mixins( BudsFactory.Mixin.class )
public interface BudsFactory
    extends ServiceComposite, ServiceActivation
{

    Bud createNewBud( Bud creationBud, String title, String content );

    abstract class Mixin
        implements BudsFactory
    {

        private static final Logger LOGGER = LoggerFactory.getLogger( BudsFactory.class );
        @Structure
        private Module module;
        @Service
        private GraphDB graphDB;
        @Service
        private AttachmentsDB attachmentsDB;

        @Override
        public void activateService()
            throws Exception
        {
            UnitOfWork uow = module.newUnitOfWork();
            try
            {
                uow.get( Bud.class, Bud.ROOT_BUD_IDENTITY );
                uow.discard();
            }
            catch( NoSuchEntityException noRootBud )
            {
                LOGGER.debug( "Root Bud does not exists, first start?" );

                // Create ROOT BudEntity
                EntityBuilder<Bud> builder = uow.newEntityBuilder( Bud.class );
                Bud root = builder.instance();
                root.identity().set( Bud.ROOT_BUD_IDENTITY );
                root.visibility().set( BudVisibility.PUBLIC );
                root.title().set( "Root Bud" );
                root.postedAt().set( new DateTime() );
                root.content().set( "## This is the Root Bud\nFor now this Bud is public, has no Role and this sample content only." );
                root = builder.newInstance();

                // Create ROOT BudNode
                graphDB.createRootBudNode( root.identity().get() );

                // Create ROOT BudAttachment
                String filename = "whats.a.bud.svg";
                InputStream attachmentInputStream = Play.application().resourceAsStream( filename );
                attachmentsDB.storeAttachment( root.identity().get(), filename, attachmentInputStream );

                try
                {
                    uow.complete();
                    LOGGER.info( "Rood Bud created!" );
                }
                catch( UnitOfWorkCompletionException ex )
                {
                    // Manual Rollback
                    uow = module.newUnitOfWork();
                    try
                    {
                        Bud rootBud = uow.get( Bud.class, Bud.ROOT_BUD_IDENTITY );
                        if( rootBud != null )
                        {
                            try
                            {
                                attachmentsDB.deleteBudDBFiles( Bud.ROOT_BUD_IDENTITY );
                            }
                            catch( RuntimeException attachEx )
                            {
                                LOGGER.warn( "Unable to cleanup RootBud attachments after creation failure", attachEx );
                            }
                            try
                            {
                                graphDB.deleteBudNode( Bud.ROOT_BUD_IDENTITY );
                            }
                            catch( RuntimeException graphEx )
                            {
                                LOGGER.warn( "Unable to cleanup RootBud node after creation failure", graphEx );
                            }
                            try
                            {
                                uow.remove( rootBud );
                                uow.complete();
                                LOGGER.error( "Something went wrong when creating Root Bud, changes have been manually rollbacked." );
                            }
                            catch( UnitOfWorkCompletionException ex2 )
                            {
                                LOGGER.error( "Something went wrong when creating Root Bud AND when manually rollbacking changes!", ex2 );
                            }
                        }
                    }
                    catch( NoSuchEntityException ex2 )
                    {
                    }
                }
            }
        }

        @Override
        public void passivateService()
            throws Exception
        {
        }

        @Override
        public Bud createNewBud( Bud creationBud, String title, String content )
        {
            UnitOfWork uow = module.currentUnitOfWork();

            // Create Bud Entity
            EntityBuilder<Bud> builder = uow.newEntityBuilder( Bud.class );
            Bud bud = builder.instance();
            bud.parent().set( creationBud );
            bud.title().set( title );
            bud.postedAt().set( new DateTime() );
            bud.content().set( content );
            bud = builder.newInstance();

            // Create BudNode
            graphDB.createBudNode( creationBud.identity().get(), bud.identity().get() );

            return bud;
        }

    }

}
