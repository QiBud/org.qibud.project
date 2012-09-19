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
import java.io.StringWriter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.json.JSONWriterSerializer;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.entitystore.mongodb.MongoMapEntityStoreService;
import org.qi4j.spi.Qi4jSPI;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With( RootBudContext.class )
public class BudRoles
        extends Controller
{

    @Structure
    public static Qi4jSPI qi4j;

    @Structure
    public static Module module;

    @Service
    public static BudPacksService budPacksService;

    @Service
    public static BudsRepository budsRepository;

    @Service
    public static MongoMapEntityStoreService mongoEntityStore;

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

            // Build JSON Response

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = JsonNodeFactory.instance.objectNode();
            json.put( "bud-identity", bud.identity().get() );

            StringWriter descriptorWriter = new StringWriter();
            mapper.writeValue( descriptorWriter, roleDescriptor );
            json.put( "descriptor", mapper.readValue( descriptorWriter.toString(), JsonNode.class ) );

            StringWriter stateWriter = new StringWriter();
            new JSONWriterSerializer( stateWriter ).serialize( roleValue );
            json.put( "state", mapper.readTree( stateWriter.toString() ) );

            return ok( json );

        } finally {
            uow.discard();
        }
    }

    public static Result saveBudRole( String identity, String pack, String role )
    {
        // Save JSON state
        return TODO;
    }

    public static Result invokeBudRoleAction( String identity, String pack, String role, String action )
            throws UnitOfWorkCompletionException, InstantiationException, IllegalAccessException, RoleActionException, IOException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {

            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound( "This Bud does not exists." );
            }
            Role roleEntity = bud.role( pack, role );
            if ( roleEntity == null ) {
                return notFound( "This Bud has no '" + pack + "/" + role + "' role" );
            }
            RoleActionDescriptor actionDescriptor = budPacksService.budPack( pack ).role( role ).action( action );
            if ( actionDescriptor == null ) {
                return notFound( "This Bud Role Action does not exists" );
            }

            // Call action
            ObjectNode actionParam = ( ObjectNode ) new ObjectMapper().readTree( ( String ) ctx().args.get( "param" ) );
            RoleAction roleAction = actionDescriptor.roleActionType().newInstance();
            ObjectNode actionResult = roleAction.invokeAction( bud, roleEntity, actionParam );

            return ok( actionResult );

        } finally {
            uow.complete();
        }
    }

}
