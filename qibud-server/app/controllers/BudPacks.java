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

import domain.budpacks.BudPack;
import domain.budpacks.BudPacksRepository;
import java.util.List;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.budpacks.all_budpacks;
import views.html.budpacks.show_budpack;

@With( RootBudContext.class )
public class BudPacks
        extends Controller
{

    public static Result packs()
    {
        List<BudPack> allBudPacks = BudPacksRepository.getInstance().findAll();
        return ok( all_budpacks.render( allBudPacks ) );
    }

    public static Result packUploadForm()
    {
        return TODO;
    }

    public static Result uploadPack()
    {
        return TODO;
    }

    public static Result pack( String pack )
    {
        BudPack budPack = BudPacksRepository.getInstance().findByName( pack );
        if ( budPack == null ) {
            return notFound();
        }
        return ok( show_budpack.render( budPack ) );
    }

    public static Result packConfiguration( String pack )
    {
        return TODO;
    }

    public static Result savePackConfiguration( String pack )
    {
        return TODO;
    }

    public static Result actionForm( String pack, String action )
    {
        return TODO;
    }

    public static Result invokeAction( String pack, String action )
    {
        return TODO;
    }

}
