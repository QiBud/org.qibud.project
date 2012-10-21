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

import application.bootstrap.RoleActionDescriptor;
import application.bootstrap.RoleDescriptor;
import domain.budpacks.BudPacksService;
import domain.buds.Bud;
import domain.buds.BudsRepository;
import domain.roles.Role;
import domain.roles.RoleAction;
import domain.roles.RoleActionException;
import java.io.IOException;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With( RootBudContext.class )
public class BudRoles
        extends Controller
{

    @Structure
    public static Module module;

    @Service
    public static BudPacksService budPacksService;

    @Service
    public static BudsRepository budsRepository;

    public static Result budRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException, IOException, JSONException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {

            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound( "This Bud does not exists." );
            }

            Role roleValue = bud.role( pack, role );
            if ( roleValue == null ) {
                return notFound( "This Bud has no '" + pack + "/" + role + "' role" );
            }

            RoleDescriptor roleDescriptor = budPacksService.budPack( pack ).role( role );

            // Describe Role
            ObjectNode json = Json.newObject();
            json.put( "bud-identity", bud.identity().get() );
            json.put( "descriptor", Json.toJson( roleDescriptor ) );
            json.put( "state", roleValue.jsonRoleState() );

            return ok( json );

        } finally {
            uow.discard();
        }
    }

    @BodyParser.Of( BodyParser.Json.class )
    public static Result saveBudRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {

            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                uow.discard();
                uow = null;
                return notFound( "This Bud does not exists." );
            }

            Role roleEntity = bud.role( pack, role );
            if ( roleEntity == null ) {
                uow.discard();
                uow = null;
                return notFound( "This Bud has no '" + pack + "/" + role + "' role" );
            }

            // Update Role
            ObjectNode jsonState = ( ObjectNode ) request().body().asJson();
            roleEntity.roleState().set( Json.stringify( jsonState ) );

            return ok();

        } finally {
            if ( uow != null ) {
                uow.complete();
            }
        }
    }

    @BodyParser.Of( BodyParser.Json.class )
    public static Result invokeBudRoleAction( String identity, String pack, String role, String action )
            throws UnitOfWorkCompletionException, InstantiationException, IllegalAccessException, RoleActionException, IOException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {

            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                uow.discard();
                uow = null;
                return notFound( "This Bud does not exists." );
            }

            Role roleValue = bud.role( pack, role );
            if ( roleValue == null ) {
                uow.discard();
                uow = null;
                return notFound( "This Bud has no '" + pack + "/" + role + "' role" );
            }

            RoleActionDescriptor actionDescriptor = budPacksService.budPack( pack ).role( role ).action( action );
            if ( actionDescriptor == null ) {
                uow.discard();
                uow = null;
                return notFound( "This Bud Role Action does not exists" );
            }

            // Call Role Action
            ObjectNode actionParam = ( ObjectNode ) request().body().asJson();
            RoleAction roleAction = actionDescriptor.roleActionType().newInstance();
            ObjectNode actionResult = roleAction.invokeAction( bud, roleValue, actionParam );

            return ok( actionResult );

        } finally {
            if ( uow != null ) {
                uow.complete();
            }
        }
    }

}
