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
import domain.buds.BudsRepository;
import domain.teams.Team;
import domain.teams.TeamFactory;
import domain.teams.TeamRepository;
import java.util.List;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.BudViewData;

import static org.qi4j.functional.Iterables.*;

public class Teams
    extends Controller
{

    public static class TeamForm
    {

        @Constraints.Required
        public String title;
    }

    static final Form<TeamForm> TEAM_FORM = form( TeamForm.class );
    @Structure
    public static Module module;
    @Service
    public static AccountRepository accountRepository;
    @Service
    public static TeamRepository teamRepository;
    @Service
    public static TeamFactory teamFactory;
    @Service
    public static BudsRepository budsRepository;
    @Service
    public static BudPacksService budPacksService;

    public static Result teams()
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Account account = accountRepository.findAccountByIdentity( AuthContextAction.connectedAccountIdentity() );

            List<Team> ownedTeams = toList( account.ownedTeams() );
            List<Team> memberTeams = toList( teamRepository.findByMember( account ) );

            return ok( views.html.teams.teams.render( ownedTeams, memberTeams, TEAM_FORM ) );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result team_create()
        throws UnitOfWorkCompletionException
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Account account = accountRepository.findAccountByIdentity( AuthContextAction.connectedAccountIdentity() );
            Form<TeamForm> filledForm = TEAM_FORM.bindFromRequest();
            if( filledForm.hasErrors() )
            {
                List<Team> ownedTeams = toList( account.ownedTeams() );
                List<Team> memberTeams = toList( teamRepository.findByMember( account ) );
                return badRequest( views.html.teams.teams.render( ownedTeams, memberTeams, filledForm ) );
            }
            TeamForm create = filledForm.get();
            teamFactory.newTeam( create.title, account );
            uow.complete();
            uow = null;
            flash( "success", "Team '" + create.title + "' created." );
            return redirect( routes.Teams.teams() );
        }
        finally
        {
            if( uow != null )
            {
                uow.discard();
            }
        }
    }

    public static Result team( String identity )
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Team team = teamRepository.findByIdentity( identity );
            if( team == null )
            {
                return notFound();
            }
            Account account = accountRepository.findAccountByIdentity( AuthContextAction.connectedAccountIdentity() );
            List<Team> ownedTeams = toList( account.ownedTeams() );
            List<Team> memberTeams = toList( teamRepository.findByMember( account ) );
            if( !ownedTeams.contains( team ) && memberTeams.contains( team ) )
            {
                return unauthorized();
            }

            return ok( views.html.teams.team.render( team,
                                                     new BudViewData( team.bud().get(),
                                                                      budPacksService.unusedRoles( team.bud().get() ),
                                                                      budsRepository.findChildren( team.bud().get() ) ) ) );
        }
        finally
        {
            uow.discard();
        }
    }

}
