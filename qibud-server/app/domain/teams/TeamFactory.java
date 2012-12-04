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
package domain.teams;

import domain.aaa.Account;
import domain.buds.Bud;
import domain.buds.BudsFactory;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;

@Mixins( TeamFactory.Mixin.class )
public interface TeamFactory
{

    Team newTeam( String title, Account owner );

    class Mixin
        implements TeamFactory
    {

        @Structure
        private Module module;
        @Service
        private BudsFactory budFactory;

        @Override
        public Team newTeam( String title, Account owner )
        {
            String budContent = "This is the bud for the '" + title + "' team.";
            Bud teamBud = budFactory.createNewBud( owner.bud().get(), title, budContent );
            teamBud.owner().set( owner );
            UnitOfWork uow = module.currentUnitOfWork();
            EntityBuilder<Team> builder = uow.newEntityBuilder( Team.class );
            Team team = builder.instance();
            team.title().set( title );
            team.bud().set( teamBud );
            team = builder.newInstance();
            owner.ownedTeams().add( team );
            return team;
        }

    }

}
