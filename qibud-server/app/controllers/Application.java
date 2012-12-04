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

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Web front controller.
 */
@WithRootBudContext
@WithAuthContext
public class Application
    extends Controller
{

    // Homepage
    public static Result index()
    {
        if( AuthContextAction.connected() )
        {
            // Redirected connected to their account home page
            return redirect( routes.Accounts.account() );
        }
        return ok( views.html.index.render() );
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
