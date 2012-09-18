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

import application.bootstrap.RoleDescriptor;
import domain.budpacks.BudPacksService;
import domain.buds.Bud;
import domain.buds.BudsRepository;
import domain.roles.Role;
import java.io.IOException;
import java.io.StringWriter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.qi4j.api.association.AssociationStateDescriptor;
import org.qi4j.api.common.QualifiedName;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.property.PropertyDescriptor;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.spi.Qi4jSPI;
import org.qi4j.spi.entity.EntityState;
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

    public static Result budRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException, IOException, JSONException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            Role roleEntity = bud.role( pack, role );
            if ( roleEntity == null ) {
                return notFound();
            }
            // TODO Return JSON state and actions description
            RoleDescriptor roleDescriptor = budPacksService.budPack( pack ).role( role );

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode json = JsonNodeFactory.instance.objectNode();
            json.put( "bud-identity", bud.identity().get() );

            StringWriter descriptorWriter = new StringWriter();
            mapper.writeValue( descriptorWriter, roleDescriptor );
            json.put( "descriptor", mapper.readValue( descriptorWriter.toString(), JsonNode.class ) );

            ObjectNode jsonState = JsonNodeFactory.instance.objectNode();
            AssociationStateDescriptor entityStateDescriptor = qi4j.getEntityDescriptor( roleEntity ).state();
            EntityState entityState = qi4j.getEntityState( roleEntity );
            for ( PropertyDescriptor propertyDescriptor : entityStateDescriptor.properties() ) {
                QualifiedName qualifiedName = propertyDescriptor.qualifiedName();
                if ( "identity".equals( qualifiedName.name() ) ) {
                    continue; // Skip Role identity
                }
                Object value = entityState.getProperty( qualifiedName );
                String serialized = value == null ? "" : value.toString();
                jsonState.put( qualifiedName.name(), serialized );
            }

            jsonState.put( "associations", JsonNodeFactory.instance.arrayNode() );

            jsonState.put( "many-associations", JsonNodeFactory.instance.objectNode() );

            json.put( "state", jsonState );

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
    {
        return TODO;
    }

}