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
package domain.aaa;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.NoSuchEntityException;

import static org.qi4j.api.query.QueryExpressions.*;

@Mixins( AccountRepository.Mixin.class )
public interface AccountRepository
{

    Account findAccountByIdentity( String identity );

    LocalAccount findLocalAccount( String username );

    class Mixin
        implements AccountRepository
    {

        @Structure
        private Module module;

        @Override
        public Account findAccountByIdentity( String identity )
        {
            try
            {
                return module.currentUnitOfWork().get( Account.class, identity );
            }
            catch( NoSuchEntityException ex )
            {
                return null;
            }
        }

        @Override
        public LocalAccount findLocalAccount( String username )
        {
            QueryBuilder<LocalAccount> builder = module.newQueryBuilder( LocalAccount.class );
            builder = builder.where( eq( templateFor( LocalAccount.class ).subjectIdentifier(), username ) );
            return module.currentUnitOfWork().newQuery( builder ).find();
        }

    }

}
