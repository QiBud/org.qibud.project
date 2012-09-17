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

import domain.budpacks.BudPacksService;
import org.qi4j.api.injection.scope.Service;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.budpacks.all_budpacks;
import views.html.budpacks.show_budpack;

@With( RootBudContext.class )
public class BudPacks
        extends Controller
{

    @Service
    public static BudPacksService roleRegistry;

    public static Result packs()
    {
        return ok( all_budpacks.render( roleRegistry.budPacks() ) );
    }

    public static Result pack( String pack )
    {
        return ok( show_budpack.render( roleRegistry.budPack( pack ) ) );
    }

}
