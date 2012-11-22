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
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.codeartisans.java.toolbox.Strings;
import org.codeartisans.playqi.PlayQi;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.library.shiro.ini.IniSecurityManagerService;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class AuthContextAction
    extends Action<WithAuthContext>
{

    @Override
    public Result call( Http.Context ctx )
        throws Throwable
    {
        IniSecurityManagerService securityManagerService = PlayQi.service( IniSecurityManagerService.class );
        ThreadContext.bind( securityManagerService.getSecurityManager() );

        String accountIdentity = ctx.session().get( Authentication.ACCOUNT_IDENTITY_KEY );
        String principal = ctx.session().get( Authentication.ACCOUNT_SUBJECT_KEY );
        if( !Strings.isEmpty( accountIdentity ) && !Strings.isEmpty( principal ) )
        {
            UnitOfWork uow = PlayQi.controllersModule().newUnitOfWork();
            AccountRepository accountRepository = PlayQi.service( AccountRepository.class );
            Account account = accountRepository.findAccountByIdentity( accountIdentity );
            if( account == null )
            {
                // Unknown account, automatic logout
                Authentication.clear_auth( ctx.session() );
            }
            else
            {
                // Associate Account with Shiro current thread
                Subject subject = new Subject.Builder().
                    principals( new SimplePrincipalCollection( principal, "AlreadyLoggedIn" ) ).
                    authenticated( true ).
                    buildSubject();
                ThreadContext.bind( subject );
            }
            uow.discard();
        }
        else if( !Strings.isEmpty( accountIdentity ) || !Strings.isEmpty( principal ) )
        {
            Authentication.clear_auth( ctx.session() );
        }
        try
        {
            Result result = delegate.call( ctx );
            return result;
        }
        finally
        {
            ThreadContext.unbindSubject();
            ThreadContext.unbindSecurityManager();
        }
    }

    public static boolean connected()
    {
        return !Strings.isEmpty( Http.Context.current().session().get( Authentication.ACCOUNT_IDENTITY_KEY ) );
    }

    public static String connectedSubject()
    {
        return Http.Context.current().session().get( Authentication.ACCOUNT_SUBJECT_KEY );
    }

    public static String connectedAccountIdentity()
    {
        return Http.Context.current().session().get( Authentication.ACCOUNT_IDENTITY_KEY );
    }

}
