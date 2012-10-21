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

import application.bootstrap.BudPackDescriptor;
import application.bootstrap.RoleDescriptor;
import domain.budpacks.BudPacksService;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.codeartisans.java.toolbox.Strings;
import org.qi4j.api.injection.scope.Service;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.budpacks.all_budpacks;
import views.html.budpacks.show_budpack;

@With( { RootBudContext.class, AuthContext.class } )
public class BudPacks
        extends Controller
{

    @Service
    public static BudPacksService budPacksService;

    public static Result packs()
    {
        return ok( all_budpacks.render( budPacksService.budPacks() ) );
    }

    public static Result pack( String pack )
    {
        return ok( show_budpack.render( budPacksService.budPack( pack ) ) );
    }

    /**
     * Generate the BudPacks javascript from all available BudPacks scripts.
     * BudPacks initializers first and then all Bud Roles.
     */
    public static Result packsJavascript()
            throws IOException
    {
        StringBuilder jsBuilder = new StringBuilder();
        for ( BudPackDescriptor budPack : budPacksService.budPacks() ) {
            String resourceName = "/public/budpacks/" + budPack.name() + "/_" + budPack.name() + ".js";
            URL resource = Play.application().resource( resourceName );
            if ( resource != null ) {
                jsBuilder.append( Strings.toString( new InputStreamReader( resource.openStream(), "UTF-8" ) ) ).append( Strings.NEWLINE );
            }
        }
        for ( RoleDescriptor role : budPacksService.roles() ) {
            String resourceName = "/public/budpacks/" + role.budPackName() + "/" + role.name() + ".js";
            URL resource = Play.application().resource( resourceName );
            if ( resource != null ) {
                jsBuilder.append( Strings.toString( new InputStreamReader( resource.openStream(), "UTF-8" ) ) ).append( Strings.NEWLINE );
            }
        }
        jsBuilder.append( Strings.NEWLINE ).append( "console.log(\"BudPacks Javascript Loaded\");" ).append( Strings.NEWLINE );
        return ok( jsBuilder.toString() ).as( "application/javascript" );
    }

}
