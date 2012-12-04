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

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import domain.aaa.Account;
import domain.aaa.AccountRepository;
import domain.budpacks.BudPacksService;
import domain.buds.Bud;
import domain.buds.BudVisibility;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import domain.roles.Role;
import infrastructure.attachmentsdb.AttachmentsDB;
import infrastructure.graphdb.GraphDB;
import java.util.Iterator;
import org.neo4j.helpers.collection.Iterables;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.query.Query;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import views.BudViewData;
import views.html.buds.create_bud;
import views.html.buds.edit_bud;
import views.html.buds.show_bud;

@WithRootBudContext
@WithAuthContext
public class Buds
    extends Controller
{

    public static class BudForm
    {

        @Constraints.Required
        public String title;
        public String content;

        public static BudForm filledWith( Bud bud )
        {
            BudForm budForm = new BudForm();
            budForm.title = bud.title().get();
            budForm.content = bud.content().get();
            return budForm;
        }

    }

    static final Form<BudForm> BUD_FORM = form( BudForm.class );
    @Structure
    public static Module module;
    @Service
    public static AccountRepository accountRepository;
    @Service
    public static BudPacksService budPacksService;
    @Service
    public static BudsRepository budsRepository;
    @Service
    public static BudsFactory budsFactory;
    @Service
    public static AttachmentsDB attachmentsDB;
    @Service
    public static GraphDB graphDB;

    public static Result buds()
    {
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Query<Bud> publicBuds = budsRepository.findPublic();
            return ok( views.html.buds.public_buds.render( Iterables.toList( publicBuds ) ) );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result budCreateForm( String identity )
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud parent = budsRepository.findByIdentity( identity );
            if( parent == null )
            {
                return notFound();
            }
            return ok( create_bud.render( parent, BUD_FORM ) );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result saveNewBud( String identity )
        throws UnitOfWorkCompletionException
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud parent = budsRepository.findByIdentity( identity );
            if( parent == null )
            {
                return notFound();
            }
            Form<BudForm> filledForm = BUD_FORM.bindFromRequest();
            if( filledForm.hasErrors() )
            {
                return badRequest( create_bud.render( parent, filledForm ) );
            }

            BudForm newBud = filledForm.get();
            Account owner = accountRepository.findAccountByIdentity( AuthContextAction.connectedAccountIdentity() );
            Bud createdBud = budsFactory.createNewBud( parent, newBud.title, newBud.content );
            createdBud.owner().set( owner );

            flash( "success", newBud.title + " created" );
            Result result = redirect( routes.Buds.bud( createdBud.identity().get() ) );

            uow.complete();
            uow = null;

            return result;
        }
        finally
        {
            if( uow != null )
            {
                uow.discard();
            }
        }
    }

    public static Result bud( String identity )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }
            if( bud.visibility().get() != BudVisibility.PUBLIC )
            {
                if( bud.owner().get() == null )
                {
                    return unauthorized( "Unauthorized" );
                }
                if( !bud.owner().get().identity().get().equals( AuthContextAction.connectedAccountIdentity() ) )
                {
                    // TODO Ensure shared with connected Account!
                    return unauthorized( "Unauthorized" );
                }
            }
            return ok( show_bud.render( new BudViewData( bud,
                                                         budPacksService.unusedRoles( bud ),
                                                         budsRepository.findChildren( bud ) ) ) );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result attachment( String identity, String attachment_id )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }
            GridFSDBFile dbFile = attachmentsDB.getDBFile( attachment_id );
            if( dbFile == null )
            {
                return notFound();
            }
            if( dbFile.getContentType() != null )
            {
                return ok( dbFile.getInputStream() ).as( dbFile.getContentType() );
            }
            return ok( dbFile.getInputStream() );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result attachmentMetadata( String identity, String attachment_id )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }
            GridFSDBFile dbFile = attachmentsDB.getDBFile( attachment_id );
            if( dbFile == null )
            {
                return notFound();
            }
            DBObject metaData = dbFile.getMetaData();
            String json = metaData == null ? "{}" : metaData.toString();
            return ok( json ).as( "application/json" );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result budEditForm( String identity )
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }
            return ok( edit_bud.render( new BudViewData( bud,
                                                         budPacksService.unusedRoles( bud ),
                                                         budsRepository.findChildren( bud ) ),
                                        form( BudForm.class ).fill( BudForm.filledWith( bud ) ) ) );
        }
        finally
        {
            uow.discard();
        }
    }

    public static Result saveBud( String identity )
        throws UnitOfWorkCompletionException
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }

            Form<BudForm> filledForm = BUD_FORM.bindFromRequest();

            if( filledForm.hasErrors() )
            {
                return badRequest( edit_bud.render( new BudViewData( bud,
                                                                     budPacksService.unusedRoles( bud ),
                                                                     budsRepository.findChildren( bud ) ),
                                                    filledForm ) );
            }
            else
            {
                BudForm updated = filledForm.get();
                bud.title().set( updated.title );
                bud.content().set( updated.content );
                for( Role role : bud.roles() )
                {
                    role.onBudChange( bud );
                }
                flash( "success", updated.title + " saved" );

                Result result = redirect( routes.Buds.bud( bud.identity().get() ) );
                uow.complete();
                uow = null;
                return result;
            }
        }
        finally
        {
            if( uow != null )
            {
                uow.discard();
            }
        }
    }

    public static Result deleteBud( String identity )
        throws UnitOfWorkCompletionException
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }
            if( budsRepository.findChildren( bud.identity().get() ).count() > 0 )
            {
                flash( "warn", bud.title().get() + " has children and cannot be deleted!" );
                return redirect( routes.Buds.bud( bud.identity().get() ) );
            }
            Bud parent = bud.parent().get();
            Call redirect = parent == null
                            ? routes.Application.index()
                            : routes.Buds.bud( parent.identity().get() );
            flash( "success", bud.title().get() + " deleted" );

            // Disable all roles
            Iterator<Role> rolesIt = bud.roles().iterator();
            while( rolesIt.hasNext() )
            {
                rolesIt.next().onDisable( bud );
            }

            // Effectively delete bud
            attachmentsDB.deleteBudDBFiles( bud.identity().get() );
            graphDB.deleteBudNode( bud.identity().get() );
            uow.remove( bud );

            Result result = redirect( redirect );
            uow.complete();
            uow = null;
            return result;
        }
        finally
        {
            if( uow != null )
            {
                uow.discard();
            }
        }
    }

    public static Result addBudRole( String identity, String pack, String role )
        throws UnitOfWorkCompletionException
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }

            bud.addRole( pack, role );

            flash( "success", "Role " + pack + "/" + role + " added" );
            Result result = redirect( routes.Buds.bud( identity ) );
            uow.complete();
            uow = null;
            return result;
        }
        finally
        {
            if( uow != null )
            {
                uow.discard();
            }
        }
    }

    public static Result deleteBudRole( String identity, String pack, String role )
        throws UnitOfWorkCompletionException
    {
        if( !AuthContextAction.connected() )
        {
            return unauthorized();
        }
        UnitOfWork uow = module.newUnitOfWork();
        try
        {
            Bud bud = budsRepository.findByIdentity( identity );
            if( bud == null )
            {
                return notFound();
            }

            bud.removeRole( pack, role );

            flash( "success", "Role " + pack + "/" + role + " removed" );
            Result result = redirect( routes.Buds.bud( bud.identity().get() ) );
            uow.complete();
            uow = null;
            return result;
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
