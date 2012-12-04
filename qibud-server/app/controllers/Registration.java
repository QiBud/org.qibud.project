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

import domain.aaa.AccountFactory;
import domain.aaa.AccountRepository;
import domain.aaa.LocalAccount;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Registration Controller.
 */
public class Registration
    extends Controller
{

    public static class Register
    {

        @Constraints.Required
        public String username;
        @Constraints.Required
        public String password;
        @Constraints.Required
        public String confirm;
    }

    static final Form<Register> REG_FORM = form( Register.class );
    @Structure
    public static Module module;
    @Service
    public static AccountRepository accountRepository;
    @Service
    public static AccountFactory accountFactory;

    public static Result register_form()
    {
        return ok( views.html.reg.register.render( REG_FORM ) );
    }

    public static Result register()
        throws UnitOfWorkCompletionException
    {
        Form<Register> filledForm = REG_FORM.bindFromRequest();
        if( filledForm.hasErrors() )
        {
            flash( "warn", "Some fields were rejected" );
            return badRequest( views.html.reg.register.render( filledForm ) );
        }

        Register register = filledForm.get();
        if( !register.password.equals( register.confirm ) )
        {
            flash( "warn", "Some fields were rejected" );
            filledForm.reject( "password", "Passwords do not match!" );
            filledForm.reject( "confirm", "Passwords do not match!" );
            return badRequest( views.html.reg.register.render( filledForm ) );
        }

        UnitOfWork uow = module.newUnitOfWork();
        if( accountRepository.findLocalAccount( register.username ) != null )
        {
            flash( "warn", "Some fields were rejected" );
            filledForm.reject( "username", register.username + " is unavailable" );
            return badRequest( views.html.reg.register.render( filledForm ) );
        }
        try
        {
            LocalAccount account = accountFactory.createNewLocalAccount( register.username, register.password );
            String accountIdentity = account.identity().get();
            uow.complete();
            uow = null;

            Authentication.create_auth( session(), accountIdentity, register.username );

            flash( "success", "Logged in as " + register.username + "." );
            return redirect( routes.Accounts.account() );
        }
        finally
        {
            if( uow != null )
            {
                uow.discard();
            }
        }
    }

}
