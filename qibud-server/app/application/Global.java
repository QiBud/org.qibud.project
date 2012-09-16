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

import java.io.File;
import java.lang.reflect.Method;
import org.apache.commons.io.FileUtils;
import org.qibud.eventstore.Usecase;
import org.qibud.mongodb.MongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.GlobalSettings;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.Threads;

public class Global
        extends GlobalSettings
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.server" );

    private static final String SHUTDOWN_HOOK_THREAD_NAME = "QiBud.Shutdown";

    @Override
    public void onStart( Application app )
    {
        super.onStart( app );
        if ( !app.isProd() && !Threads.isThreadRegisteredAsShutdownHook( SHUTDOWN_HOOK_THREAD_NAME ) ) {

            // Entities
            final String entitiesHost = app.configuration().getString( "qibud.entities.host" );
            final int entitiesPort = app.configuration().getInt( "qibud.entities.port" );
            final String entitiesDatabase = app.configuration().getString( "qibud.entities.database" );

            // Attachments
            final String attachmentsHost = app.configuration().getString( "qibud.attachmentsdb.host" );
            final int attachmentsPort = app.configuration().getInt( "qibud.attachmentsdb.port" );
            final String attachmentsDatabase = app.configuration().getString( "qibud.attachmentsdb.db" );

            // Graph
            final File graphPath = new File( app.configuration().getString( "qibud.graphdb.path" ) );

            Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
            {

                @Override
                @SuppressWarnings( "CallToThreadDumpStack" )
                public void run()
                {
                    // Entities
                    try {
                        MongoDB.connectToMongoDB( entitiesHost, entitiesPort ).dropDatabase( entitiesDatabase );
                        System.out.println( "Entities Database Cleared!" );
                    } catch ( Exception ex ) {
                        System.err.println( "Unable to delete entities" );
                        ex.printStackTrace();
                    }

                    // Attachments
                    try {
                        MongoDB.connectToMongoDB( attachmentsHost, attachmentsPort ).dropDatabase( attachmentsDatabase );
                        System.out.println( "Attachments Database Cleared!" );
                    } catch ( Exception ex ) {
                        System.err.println( "Unable to delete attachments" );
                        ex.printStackTrace();
                    }

                    // Graph
                    try {
                        FileUtils.deleteDirectory( graphPath );
                        System.out.println( "Graph Database Cleared!" );
                    } catch ( Exception ex ) {
                        System.err.println( "Unable to delete graph database from: " + graphPath );
                        ex.printStackTrace();
                    }
                }

            }, SHUTDOWN_HOOK_THREAD_NAME ) );
        }
    }

    @Override
    public void onStop( Application aplctn )
    {
        super.onStop( aplctn );
    }

    @Override
    public Action onRequest( Http.Request request, Method actionMethod )
    {
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
