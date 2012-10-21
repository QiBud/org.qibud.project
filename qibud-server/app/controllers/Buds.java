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
import domain.budpacks.BudPacksService;
import domain.buds.Bud;
import domain.buds.BudsFactory;
import domain.buds.BudsRepository;
import forms.BudForm;
import infrastructure.attachmentsdb.AttachmentsDB;
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
import views.BudViewData;
import views.html.buds.all_buds;
import views.html.buds.create_bud;
import views.html.buds.edit_bud;
import views.html.buds.show_bud;

@With( { RootBudContext.class, AuthContext.class } )
public class Buds
        extends Controller
{

    static final Form<BudForm> budForm = form( BudForm.class );

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

    public static Result budCreateForm( String identity )
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud parent = budsRepository.findByIdentity( identity );
            if ( parent == null ) {
                return notFound();
            }
            return ok( create_bud.render( parent, budForm ) );
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
                uow.discard();
                uow = null;
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
            if ( uow != null ) {
                uow.complete();
            }
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
            return ok( show_bud.render( new BudViewData( bud, budPacksService.unusedRoles( bud ) ) ) );
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
            return ok( edit_bud.render( new BudViewData( bud, budPacksService.unusedRoles( bud ) ),
                                        form( BudForm.class ).fill( BudForm.filledWith( bud ) ) ) );
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
                uow.discard();
                uow = null;
                return notFound();
            }

            Form<BudForm> filledForm = budForm.bindFromRequest();

            if ( filledForm.hasErrors() ) {
                return badRequest( edit_bud.render( new BudViewData( bud, budPacksService.unusedRoles( bud ) ),
                                                    filledForm ) );
            } else {
                BudForm updated = filledForm.get();
                bud.title().set( updated.title );
                bud.content().set( updated.content );
                flash( "success", updated.title + " saved" );
                return redirect( routes.Buds.bud( bud.identity().get() ) );
            }
        } finally {
            if ( uow != null ) {
                uow.complete();
            }
        }
    }

    public static Result deleteBud( String identity )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                uow.discard();
                uow = null;
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
            if ( uow != null ) {
                uow.complete();
            }
        }
    }

    public static Result addBudRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {

            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                uow.discard();
                uow = null;
                return notFound();
            }

            bud.addRole( pack, role );

            flash( "success", "Role " + pack + "/" + role + " added" );
            return redirect( routes.Buds.bud( identity ) );

        } finally {
            if ( uow != null ) {
                uow.complete();
            }
        }
    }

    public static Result deleteBudRole( String identity, String pack, String role )
            throws UnitOfWorkCompletionException
    {
        UnitOfWork uow = module.newUnitOfWork();
        try {
            Bud bud = budsRepository.findByIdentity( identity );
            if ( bud == null ) {
                uow.discard();
                uow = null;
                return notFound();
            }

            bud.removeRole( pack, role );

            flash( "success", "Role " + pack + "/" + role + " removed" );
            return redirect( routes.Buds.bud( bud.identity().get() ) );
        } finally {
            if ( uow != null ) {
                uow.complete();
            }
        }
    }

}
