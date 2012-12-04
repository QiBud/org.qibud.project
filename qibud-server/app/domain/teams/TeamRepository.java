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
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.NoSuchEntityException;

import static org.qi4j.api.query.QueryExpressions.*;

@Mixins( TeamRepository.Mixin.class )
public interface TeamRepository
{

    Team findByIdentity( String identity );

    Query<Team> findByMember( Account member );

    class Mixin
        implements TeamRepository
    {

        @Structure
        private Module module;

        @Override
        public Team findByIdentity( String identity )
        {
            try
            {
                return module.currentUnitOfWork().get( Team.class, identity );
            }
            catch( NoSuchEntityException ex )
            {
                return null;
            }
        }

        @Override
        public Query<Team> findByMember( Account member )
        {
            QueryBuilder<Team> builder = module.newQueryBuilder( Team.class );
            Team team = templateFor( Team.class );
            TeamMembership membership = templateFor( TeamMembership.class );
            builder = builder.where( and( eq( membership.account(), member ),
                                          eq( membership.team(), team ) ) );
            return module.currentUnitOfWork().newQuery( builder );
        }

    }

}
