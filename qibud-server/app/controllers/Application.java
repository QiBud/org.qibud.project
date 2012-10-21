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
package controllers;

import domain.buds.BudsRepository;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qibud.eventstore.Usecase;
import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.index;

/**
 * Web front controller.
 */
@With( { RootBudContext.class, AuthContext.class } )
public class Application
        extends Controller
{

    @Structure
    public static Module module;

    @Service
    public static BudsRepository budsRepository;

    // Homepage
    @Usecase( "QiBud Server Index" )
    public static Result index()
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            return ok( index.render( budsRepository.findRootBud() ) );
        } finally {
            uow.discard();
        }
    }

    // Javascript Routing
    public static Result javascriptRoutes()
    {
        response().setContentType( "text/javascript" );
        return ok( Routes.javascriptRouter(
                "jsRoutes",
                // Routes for Buds
                controllers.routes.javascript.BudRoles.budRole(),
                controllers.routes.javascript.BudRoles.saveBudRole(),
                controllers.routes.javascript.BudRoles.invokeBudRoleAction() ) );
    }

}
