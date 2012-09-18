/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 * Copyright (c) 2012, Samuel Loup. All Rights Reserved.
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
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import domain.budpacks.BudPacksService;
import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import domain.roles.Role;
import forms.BudForm;
import infrastructure.attachmentsdb.AttachmentsDB;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.neo4j.helpers.collection.Iterables;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.query.Query;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import views.html.buds.all_buds;
import views.html.buds.create_bud;
import views.html.buds.edit_bud;
import views.html.buds.show_bud;

@With( RootBudContext.class )
public class Buds
        extends Controller
{

    final static Form<BudForm> budForm = form( BudForm.class );

    @Structure
    public static Module module;

    @Service
    public static BudPacksService budPacksService;

    @Service
    public static BudsRepository budsRepository;

    @Service
    public static BudsFactory budsFactory;

    @Service
    public static AttachmentsDB attachmentsDB;

    public static Result buds()
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Query<Bud> allBuds = budsRepository.findAll();
            return ok( all_buds.render( Iterables.toList( allBuds ) ) );
        } finally {
            uow.discard();
        }
    }

    static final Form<BudForm> budEntityForm = form( BudForm.class );

    public static Result budCreateForm( String identity )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud parent = budsRepository.findByIdentity( identity );
            if ( parent == null ) {
                return notFound();
            }
            return ok( create_bud.render( parent, form( BudForm.class ) ) );
        } finally {
            uow.discard();
        }
    }

    public static Result saveNewBud( String identity )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud parent = budsRepository.findByIdentity( identity );
            if ( parent == null ) {
                return notFound();
            }
            Form<BudForm> filledForm = budForm.bindFromRequest();

            if ( filledForm.hasErrors() ) {
                return badRequest( create_bud.render( parent, filledForm ) );
            } else {
                BudForm newBud = filledForm.get();
                Bud createdBud = budsFactory.createNewBud( parent, newBud.title, newBud.content );
                flash( "success", newBud.title + " created" );
                return redirect( routes.Buds.bud( createdBud.identity().get() ) );
            }
        } finally {
            uow.complete();
        }
    }

    public static Result bud( String identity )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            List<RoleDescriptor> availableRoles = new ArrayList<RoleDescriptor>( budPacksService.roles() );
            for ( Role budRole : bud.roles() ) {
                Iterator<RoleDescriptor> it = availableRoles.iterator();
                while ( it.hasNext() ) {
                    RoleDescriptor availableRole = it.next();
                    if ( availableRole.budPackName().equals( budRole.budPackName().get() )
                         && availableRole.name().equals( budRole.roleName().get() ) ) {
                        it.remove();
                    }
                }
            }
            return ok( show_bud.render( bud, Iterables.toList( bud.roles() ), availableRoles ) );
        } finally {
            uow.discard();
        }
    }

    public static Result attachment( String identity, String attachment_id )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            GridFSDBFile dbFile = attachmentsDB.getDBFile( attachment_id );
            if ( dbFile == null ) {
                return notFound();
            }
            if ( dbFile.getContentType() != null ) {
                return ok( dbFile.getInputStream() ).as( dbFile.getContentType() );
            }
            return ok( dbFile.getInputStream() );
        } finally {
            uow.discard();
        }
    }

    public static Result attachmentMetadata( String identity, String attachment_id )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            GridFSDBFile dbFile = attachmentsDB.getDBFile( attachment_id );
            if ( dbFile == null ) {
                return notFound();
            }
            DBObject metaData = dbFile.getMetaData();
            String json = metaData == null ? "{}" : metaData.toString();
            return ok( json ).as( "application/json" );
        } finally {
            uow.discard();
        }
    }

    public static Result budEditForm( String identity )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            return ok( edit_bud.render( bud, form( BudForm.class ).fill( BudForm.filledWith( bud ) ) ) );
        } finally {
            uow.discard();
        }
    }

    public static Result saveBud( String identity )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }

            Form<BudForm> filledForm = budForm.bindFromRequest();

            if ( filledForm.hasErrors() ) {
                return badRequest( edit_bud.render( bud, filledForm ) );
            } else {
                BudForm updated = filledForm.get();
                bud.title().set( updated.title );
                bud.content().set( updated.content );
                flash( "success", updated.title + " saved" );
                return redirect( routes.Buds.bud( bud.identity().get() ) );
            }
        } finally {
            uow.complete();
        }
    }

    public static Result deleteBud( String identity )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            Bud parent = bud.parent().get();
            Call redirect = parent == null
                            ? routes.Application.index()
                            : routes.Buds.bud( parent.identity().get() );
            // TODO Implement deleteBud();
            flash( "success", bud.title().get() + " deleted" );
            return redirect( redirect );
        } finally {
            uow.complete();
        }
    }

    public static Result addBudRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            Iterator<Role> it = bud.passivatedRoles().iterator();
            Role newRole = null;
            while ( it.hasNext() ) {
                Role candidate = it.next();
                if ( candidate.budPackName().get().equals( pack )
                     && candidate.roleName().get().equals( role ) ) {
                    it.remove();
                    newRole = candidate;
                    break;
                }
            }
            if ( newRole == null ) {
                newRole = budPacksService.newRoleInstance( pack, role );
            }
            bud.roles().add( newRole );
            flash( "success", "Role " + pack + "/" + role + " added" );
            return redirect( routes.Buds.bud( bud.identity().get() ) );
        } finally {
            uow.complete();
        }
    }

    public static Result deleteBudRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            Iterator<Role> it = bud.roles().iterator();
            while ( it.hasNext() ) {
                Role candidate = it.next();
                if ( candidate.budPackName().get().equals( pack )
                     && candidate.roleName().get().equals( role ) ) {
                    it.remove();
                    bud.passivatedRoles().add( candidate );
                    break;
                }
            }
            flash( "success", "Role " + pack + "/" + role + " removed" );
            return redirect( routes.Buds.bud( bud.identity().get() ) );
        } finally {
            uow.complete();
        }
    }

    public static Result budRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException, IOException, JSONException
    {
        // Return JSON state and actions description
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
            ObjectNode json = JsonNodeFactory.instance.objectNode();
            json.put( "identity", bud.identity().get() );
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
