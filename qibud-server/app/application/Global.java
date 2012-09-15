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

import com.mongodb.Mongo;
import java.lang.reflect.Method;
import org.codeartisans.playqi.PlayQi;
import org.qi4j.entitystore.mongodb.MongoMapEntityStoreService;
import org.qibud.eventstore.Usecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import static application.bootstrap.QiBudAssembler.LAYER_INFRASTRUCTURE;
import static application.bootstrap.QiBudAssembler.MODULE_PERSISTENCE;

public class Global
        extends GlobalSettings
{

    private static final Logger LOGGER = LoggerFactory.getLogger( "org.qibud.server" );

    @Override
    public void onStart( Application aplctn )
    {
        super.onStart( aplctn );
    }

    @Override
    public void onStop( Application aplctn )
    {
        if ( !Play.isProd() ) {
            // Delete data if dev or test mode
            MongoMapEntityStoreService es = PlayQi.service( LAYER_INFRASTRUCTURE, MODULE_PERSISTENCE, MongoMapEntityStoreService.class );
            Mongo mongo = es.mongoInstanceUsed();
            String dbName = es.dbInstanceUsed().getName();
            mongo.dropDatabase( dbName );
        }
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
