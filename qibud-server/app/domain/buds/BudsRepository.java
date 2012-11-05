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
package domain.buds;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;

import static org.qi4j.api.query.QueryExpressions.*;

@Mixins( BudsRepository.Mixin.class )
public interface BudsRepository
    extends ServiceComposite
{

    Bud findRootBud();

    Query<Bud> findAll();

    Bud findByIdentity( String identity );

    Query<Bud> findChildren( String identity );

    abstract class Mixin
        implements BudsRepository
    {

        @Structure
        private Module module;

        @Override
        public Bud findRootBud()
        {
            return module.currentUnitOfWork().get( Bud.class, Bud.ROOT_BUD_IDENTITY );
        }

        @Override
        public Query<Bud> findAll()
        {
            return module.currentUnitOfWork().newQuery( module.newQueryBuilder( Bud.class ) );
        }

        @Override
        public Bud findByIdentity( String identity )
        {
            return module.currentUnitOfWork().get( Bud.class, identity );
        }

        @Override
        public Query<Bud> findChildren( String identity )
        {
            QueryBuilder<Bud> builder = module.newQueryBuilder( Bud.class );
            Bud template = templateFor( Bud.class );
            builder = builder.where( eq( template.parent().get().identity(), identity ) );
            Query<Bud> query = module.currentUnitOfWork().newQuery( builder );
            return query;
        }

    }

}
