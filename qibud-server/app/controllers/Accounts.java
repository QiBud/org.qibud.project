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

import domain.aaa.Account;
import domain.aaa.AccountRepository;
import domain.budpacks.BudPacksService;
import domain.buds.Bud;
import domain.buds.BudsRepository;
import org.neo4j.helpers.collection.Iterables;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.query.Query;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import play.mvc.Controller;
import play.mvc.Result;
import views.BudViewData;

@WithRootBudContext
@WithAuthContext
public class Accounts
    extends Controller
{

    @Structure
    public static Module module;
    @Service
    public static AccountRepository accountRepository;
    @Service
    public static BudsRepository budsRepository;
    @Service
    public static BudPacksService budPacksService;

    public static Result account()
    {
        if( !AuthContextAction.connected() )
        {
            Authentication.clear_auth( session() );
            flash( "warn", "You are not authenticated." );
            return redirect( routes.Authentication.login() );
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Account account = accountRepository.findAccountByIdentity( AuthContextAction.connectedAccountIdentity() );
            if( account == null )
            {
                Authentication.clear_auth( session() );
                flash( "warn", "You are not authenticated." );
                return redirect( routes.Authentication.login() );
            }
            Bud bud = account.bud().get();
            return ok( views.html.buds.show_bud.render( new BudViewData( bud,
                                                                         budPacksService.unusedRoles( bud ),
                                                                         budsRepository.findChildren( bud ) ) ) );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result account_buds()
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Account owner = accountRepository.findAccountByIdentity( AuthContextAction.connectedAccountIdentity() );
            Query<Bud> myBuds = budsRepository.findOwnedBuds( owner );
            return ok( views.html.buds.account_buds.render( Iterables.toList( myBuds ) ) );
        }
        finally
        {
            uow.discard();
        }
    }

}
