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

import buds.Bud;
import buds.BudsFactory;
import buds.BudsRepository;
import java.lang.reflect.Method;
import org.qibud.eventstore.DomainEventsSequence;
import org.qibud.eventstore.DomainEventsSequenceBuilder;
import org.qibud.eventstore.Usecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import storage.AttachmentsDB;
import storage.EntitiesDB;
import storage.EventSourcingDB;
import storage.GraphDB;

public class Global
        extends GlobalSettings
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.server" );

    private boolean started = false;

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );
        ensureStarted();
    }

    private synchronized void ensureStarted()
    {
        if ( !started ) {

            LOGGER.info( "QiBud Server starting ..." );

            EventSourcingDB eventSourcing = EventSourcingDB.getInstance();
            EntitiesDB entities = EntitiesDB.getInstance();
            AttachmentsDB attachments = AttachmentsDB.getInstance();
            GraphDB graph = GraphDB.getInstance();

            eventSourcing.start();
            entities.start( eventSourcing.eventStore() );
            attachments.start();
            graph.start();

            Bud rootBud = BudsRepository.getInstance().findRootBud();
            if ( rootBud == null ) {
                rootBud = BudsFactory.getInstance().createRootBud();
            }

            started = true;
            LOGGER.info( "QiBud Server Started with Root Bud: {}", rootBud );

        }
    }

    @Override
    public void onStop( Application aplctn )
    {
        if ( started ) {
            GraphDB.getInstance().shutdown();
            AttachmentsDB.getInstance().shutdown();
            EntitiesDB.getInstance().shutdown();
            EventSourcingDB.getInstance().shutdown();
        }
        super.onStop( aplctn );
    }

    public static final String DOMAIN_EVENTS_SEQ_BUILDER_ARG = "domain-events-sequence-builder";

    @Override
    public Action onRequest( Http.Request request, Method actionMethod )
    {
        ensureStarted();

        // Gather Usecase from controller methods annotation or create a generic one
        Usecase annotation = actionMethod.getAnnotation( Usecase.class );
        final String usecase;
        if ( annotation == null ) {
            usecase = request.method() + ":" + request.uri();
        } else {
            usecase = annotation.value();
        }

        // Wrap controller action to handle domain events
        return new Action.Simple()
        {

            @Override
            public Result call( Http.Context ctx )
                    throws Throwable
            {
                LOGGER.debug( "Before request with Usecase '{}'", usecase );

                DomainEventsSequenceBuilder eventsSequenceBuilder = new DomainEventsSequenceBuilder().withUsecase( usecase ).withUser( "QiBud System" );
                ctx.args.put( DOMAIN_EVENTS_SEQ_BUILDER_ARG, eventsSequenceBuilder );

                try {

                    Result result = delegate.call( ctx );
                    LOGGER.debug( "After request with Usecase '{}'", usecase );

                    DomainEventsSequence eventsSequence = eventsSequenceBuilder.build();
                    if ( !eventsSequence.events().isEmpty() ) {
                        EventSourcingDB.getInstance().eventStore().storeEvents( eventsSequence );
                    }

                    return result;

                } catch ( Throwable ex ) {

                    LOGGER.debug( "Error after request with Usecase '{}'", usecase, ex );
                    throw ex;

                } finally {

                    LOGGER.debug( "Finally after request with Usecase '{}'", usecase );

                }
            }

        };
    }

}
