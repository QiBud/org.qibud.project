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

import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import org.apache.shiro.authc.credential.PasswordService;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.library.shiro.domain.permissions.Role;
import org.qi4j.library.shiro.domain.permissions.RoleFactory;

@Mixins( AccountFactory.Mixin.class )
public interface AccountFactory
    extends ServiceActivation
{

    String SUPERUSER_USERNAME = "admin";

    LocalAccount createNewLocalAccount( String username, String password );

    class Mixin
        implements AccountFactory
    {

        private static final String SUPERUSER_DEFAULT_PASSWORD = "admin";
        @Structure
        private Module module;
        @Service
        private BudsRepository budRepository;
        @Service
        private BudsFactory budFactory;
        @Service
        private PasswordService passwordService;
        @Service
        private RoleFactory roleFactory;
        @Service
        private AccountRepository accountRepository;

        @Override
        public void activateService()
            throws Exception
        {
            // Create the super user account and the super role if they do not exist
            UnitOfWork uow = module.newUnitOfWork();
            LocalAccount superUserAccount = accountRepository.findLocalAccount( SUPERUSER_USERNAME );
            if( superUserAccount == null )
            {
                LocalAccount superUser = createNewLocalAccount( SUPERUSER_USERNAME, SUPERUSER_DEFAULT_PASSWORD );
                Role superRole = roleFactory.create( "admin", "*" );
                superRole.assignTo( superUser );
            }
            uow.complete();
        }

        @Override
        public void passivateService()
            throws Exception
        {
            // NOOP
        }

        @Override
        public LocalAccount createNewLocalAccount( String username, String password )
        {
            Bud rootBud = budRepository.findRootBud();
            String accountBudContent = "This is the bud for the " + username + " account.";
            Bud accountBud = budFactory.createNewBud( rootBud, username, accountBudContent );

            EntityBuilder<LocalAccount> builder = module.currentUnitOfWork().newEntityBuilder( LocalAccount.class );

            LocalAccount account = builder.instance();
            account.subjectIdentifier().set( username );
            account.password().set( passwordService.encryptPassword( password ) );
            account.bud().set( accountBud );

            return builder.newInstance();
        }

    }

}
