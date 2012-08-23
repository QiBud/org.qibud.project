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

import application.QiBud;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import domain.buds.Bud;
import forms.BudForm;
import org.neo4j.helpers.collection.Iterables;
import org.qi4j.api.query.Query;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import play.data.Form;
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

    public static Result buds()
    {
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Query<Bud> allBuds = QiBud.budsRepository().findAll();
            return ok( all_buds.render( Iterables.toList( allBuds ) ) );
        } finally {
            uow.discard();
        }
    }

    static final Form<BudForm> budEntityForm = form( BudForm.class );

    public static Result budCreateForm( String identity )
    {
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud parent = QiBud.budsRepository().findByIdentity( identity );
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
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud parent = QiBud.budsRepository().findByIdentity( identity );
            if ( parent == null ) {
                return notFound();
            }
            Form<BudForm> filledForm = budForm.bindFromRequest();

            if ( filledForm.hasErrors() ) {
                return badRequest( create_bud.render( parent, filledForm ) );
            } else {
                BudForm newBud = filledForm.get();
                Bud createdBud = QiBud.budsFactory().createNewBud( parent, newBud.title, newBud.content );

                return redirect( routes.Buds.bud( createdBud.identity().get() ) );
            }
        } finally {
            uow.complete();
        }
    }

    public static Result bud( String identity )
    {
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud bud = QiBud.budsRepository().findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            return ok( show_bud.render( bud ) );
        } finally {
            uow.discard();
        }
    }

    public static Result attachment( String identity, String attachment_id )
    {
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud bud = QiBud.budsRepository().findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            GridFSDBFile dbFile = QiBud.attachmentsDB().getDBFile( attachment_id );
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
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud bud = QiBud.budsRepository().findByIdentity( identity );
            if ( bud == null ) {
                return notFound();
            }
            GridFSDBFile dbFile = QiBud.attachmentsDB().getDBFile( attachment_id );
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
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud bud = QiBud.budsRepository().findByIdentity( identity );
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
        UnitOfWork uow = QiBud.budsDomainModule().newUnitOfWork();
        try {
            Bud bud = QiBud.budsRepository().findByIdentity( identity );
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
                return redirect( routes.Buds.bud( bud.identity().get() ) );
            }
        } finally {
            uow.complete();
        }
    }

    public static Result deleteBud( String identity )
    {
        return TODO;
    }

    public static Result budByRole( String identity, String pack, String role )
    {
        return TODO;
    }

    public static Result budEditFormByRole( String identity, String pack, String role )
    {
        return TODO;
    }

    public static Result saveBudByRole( String identity, String pack, String role )
    {
        return TODO;
    }

    public static Result deleteBudByRole( String identity, String pack, String role )
    {
        return TODO;
    }

    public static Result roleActionForm( String identity, String pack, String role, String action )
    {
        return TODO;
    }

    public static Result invokeRoleAction( String identity, String pack, String role, String action )
    {
        return TODO;
    }

}
