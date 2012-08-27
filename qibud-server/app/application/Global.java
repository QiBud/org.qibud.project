/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 * Copyright (c) 2012, Samuel Loup. All Rights Reserved.
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
package application;

import application.bootstrap.QiBudAssembler;
import com.mongodb.Mongo;
import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import java.lang.reflect.Method;
import org.qi4j.api.structure.Application.Mode;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.NoSuchEntityException;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.Energy4Java;
import org.qi4j.entitystore.mongodb.MongoMapEntityStoreService;
import org.qibud.eventstore.Usecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Play;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.QiBudException;

import static application.bootstrap.QiBudAssembler.*;

public class Global
        extends GlobalSettings
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.server" );

    private Energy4Java qi4j;

    private org.qi4j.api.structure.Application qi4jApp;

    private boolean started = false;

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );
        ensureStarted( aplctn );
    }

    private synchronized void ensureStarted( Application aplctn )
    {
        if ( !started ) {
            try {

                LOGGER.info( "QiBud Server starting ..." );

                Mode mode = Play.isProd() ? Mode.production : Play.isDev() ? Mode.development : Mode.test;

                // Load Play! configuration
                Configuration playConf = aplctn.configuration();
                String entitiesHost = playConf.getString( "qibud.entities.host" );
                int entitiesPort = playConf.getInt( "qibud.entities.port" );
                String entitiesUsername = playConf.getString( "qibud.entities.username" );
                String entitiesPassword = playConf.getString( "qibud.entities.password" );
                String entitiesDatabase = playConf.getString( "qibud.entities.database" );
                String entitiesCollection = playConf.getString( "qibud.entities.collection" );

                // Activate Qi4j Application
                qi4j = new Energy4Java();
                qi4jApp = qi4j.newApplication( new QiBudAssembler( mode,
                                                                   entitiesHost, entitiesPort,
                                                                   entitiesUsername, entitiesPassword,
                                                                   entitiesDatabase, entitiesCollection ) );
                qi4jApp.activate();
                LOGGER.info( "Qi4j Application Activated" );

                // Create Root Bud if needed
                Module budsModule = qi4jApp.findModule( LAYER_DOMAIN, MODULE_BUDS );
                BudsRepository budsRepository = budsModule.findService( BudsRepository.class ).get();
                BudsFactory budsFactory = budsModule.findService( BudsFactory.class ).get();

                UnitOfWork uow = budsModule.newUnitOfWork();
                Bud rootBud;
                try {
                    rootBud = budsRepository.findRootBud();
                    uow.discard();
                } catch ( NoSuchEntityException noRootBud ) {
                    rootBud = budsFactory.createRootBud();
                    uow.complete();
                }

                QiBud.setQi4jApplication( qi4jApp ); // FIXME Find a way to "un-staticalize" this

                started = true;
                LOGGER.info( "QiBud Server Started with Root Bud: {}", rootBud );

            } catch ( AssemblyException ex ) {
                throw new QiBudException( "Unable to assemble Qi4j application: " + ex.getMessage(), ex );
            } catch ( UnitOfWorkCompletionException ex ) {
                throw new QiBudException( "Unable to create Root Bud: " + ex.getMessage(), ex );
            } catch ( Exception ex ) {
                throw new QiBudException( "Unable to activate Qi4j application: " + ex.getMessage(), ex );
            } finally {
                if ( !started ) {
                    try {
                        qi4jApp.passivate();
                        QiBud.setQi4jApplication( null );
                        LOGGER.info( "Qi4j Application Passivated" );
                    } catch ( Exception ex ) {
                        LOGGER.error( "Unable to passivate Qi4j application: " + ex.getMessage(), ex );
                    }
                }
            }
        }
    }

    @Override
    public void onStop( Application aplctn )
    {
        if ( started ) {
            if ( !Play.isProd() ) {
                // Delete data if dev or test mode
                Module infraModule = QiBud.application().findModule( LAYER_INFRASTRUCTURE, MODULE_PERSISTENCE );
                MongoMapEntityStoreService es = infraModule.findService( MongoMapEntityStoreService.class ).get();
                Mongo mongo = es.mongoInstanceUsed();
                String dbName = es.dbInstanceUsed().getName();
                mongo.dropDatabase( dbName );
            }
            try {
                qi4jApp.passivate();
                QiBud.setQi4jApplication( null );
                LOGGER.info( "Qi4j Application Passivated" );
            } catch ( Exception ex ) {
                LOGGER.error( "Unable to passivate Qi4j application: " + ex.getMessage(), ex );
            }
        }
        super.onStop( aplctn );
    }

    @Override
    public Action onRequest( Http.Request request, Method actionMethod )
    {
        if ( !Play.isProd() ) {
            // Restart the application
            ensureStarted( Play.application() );
        }

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

                try {

                    Result result = delegate.call( ctx );
                    LOGGER.debug( "After request with Usecase '{}'", usecase );
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
