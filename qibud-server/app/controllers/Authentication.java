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

import domain.aaa.AccountRepository;
import domain.aaa.LocalAccount;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;

@WithRootBudContext
@WithAuthContext
public class Authentication
    extends Controller
{

    public static final String ACCOUNT_IDENTITY_KEY = "account-identity";
    public static final String ACCOUNT_SUBJECT_KEY = "account-subject";

    public static class Login
    {

        @Constraints.Required
        public String username;
        @Constraints.Required
        public String password;
    }

    static final Form<Login> LOGIN_FORM = form( Login.class );
    @Structure
    public static Module module;
    @Service
    public static AccountRepository accountRepository;

    public static Result login_form()
    {
        return ok( views.html.auth.login.render( LOGIN_FORM ) );
    }

    public static Result login()
    {
        Form<Login> filledForm = LOGIN_FORM.bindFromRequest();
        if( filledForm.hasErrors() )
        {
            return badRequest( views.html.auth.login.render( filledForm ) );
        }

        try
        {
            Login login = filledForm.get();
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.login( new UsernamePasswordToken( login.username, login.password ) );

            flash( "success", "Logged in as " + login.username + "." );

            UnitOfWork uow = module.newUnitOfWork();
            LocalAccount account = accountRepository.findLocalAccount( login.username );
            session().put( ACCOUNT_IDENTITY_KEY, account.identity().get() );
            session().put( ACCOUNT_SUBJECT_KEY, login.username );
            uow.discard();

            return redirect( routes.Application.index() );
        }
        catch( AuthenticationException ex )
        {
            Logger.debug( "Authentication failed", ex );
            session().remove( ACCOUNT_IDENTITY_KEY );
            session().remove( ACCOUNT_SUBJECT_KEY );
            flash( "error", "Invalid username or password." );
            return badRequest( views.html.auth.login.render( filledForm ) );
        }
    }

    public static Result logout()
    {
        session().remove( ACCOUNT_IDENTITY_KEY );
        session().remove( ACCOUNT_SUBJECT_KEY );
        flash( "success", "You have been logged out." );
        return redirect( routes.Application.index() );
    }

}
