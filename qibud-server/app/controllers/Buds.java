package controllers;

import java.util.List;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

import buds.Bud;
import buds.BudEntity;
import buds.BudsFactory;
import buds.BudsRepository;
import forms.BudForm;
import play.mvc.With;
import storage.AttachmentsDB;
import views.html.buds.all_buds;
import views.html.buds.create_bud;
import views.html.buds.show_bud;
import views.html.buds.edit_bud;

@With(RootBudContext.class)
public class Buds
        extends Controller
{

    final static Form<BudForm> budForm = form(BudForm.class);
    
    public static Result buds()
    {
        List<Bud> allBuds = BudsRepository.getInstance().findAll();
        return ok( all_buds.render( allBuds ) );
    }

    static final Form<BudForm> budEntityForm = form( BudForm.class );

    public static Result budCreateForm( String identity )
    {
        Bud parent = BudsRepository.getInstance().findByIdentity( identity );
        if ( parent == null ) {
            return notFound();
        }
        return ok( create_bud.render(parent, form(BudForm.class)) );     
    }

    public static Result saveNewBud(String identity)
    {
        Bud parent = BudsRepository.getInstance().findByIdentity( identity );
        Form<BudForm> filledForm = budForm.bindFromRequest();
        
        if(filledForm.hasErrors()) {
            return badRequest(create_bud.render(parent,filledForm));
        } else {
            BudForm newBud = filledForm.get();
            Bud createdBud = BudsFactory.getInstance().createNewBud(parent, newBud.title, newBud.content);
            
            return redirect( routes.Buds.bud( createdBud.identity() ) );
        }
    }

    public static Result bud( String identity )
    {
        Bud bud = BudsRepository.getInstance().findByIdentity( identity );
        if ( bud == null ) {
            return notFound();
        }
        return ok( show_bud.render( bud ) );
    }

    public static Result attachment( String identity, String attachment_id )
    {
        Bud bud = BudsRepository.getInstance().findByIdentity( identity );
        if ( bud == null ) {
            return notFound();
        }
        GridFSDBFile dbFile = AttachmentsDB.getInstance().getDBFile( attachment_id );
        if ( dbFile == null ) {
            return notFound();
        }
        if ( dbFile.getContentType() != null ) {
            return ok( dbFile.getInputStream() ).as( dbFile.getContentType() );
        }
        return ok( dbFile.getInputStream() );
    }

    public static Result attachmentMetadata( String identity, String attachment_id )
    {
        Bud bud = BudsRepository.getInstance().findByIdentity( identity );
        if ( bud == null ) {
            return notFound();
        }
        GridFSDBFile dbFile = AttachmentsDB.getInstance().getDBFile( attachment_id );
        if ( dbFile == null ) {
            return notFound();
        }
        DBObject metaData = dbFile.getMetaData();
        String json = metaData == null ? "{}" : metaData.toString();
        return ok( json ).as( "application/json" );
    }

    public static Result budEditForm( String identity )
    {
        // Sam, see https://github.com/playframework/Play20/blob/master/samples/java/forms/app/controllers/Contacts.java
        Bud bud = BudsRepository.getInstance().findByIdentity( identity );
        
        
        if ( bud == null ) {
            return notFound();
        }
        
        
        return ok( edit_bud.render( bud, form(BudForm.class).fill(BudForm.filledWith(bud))) );
    }

    public static Result saveBud(String identity)
    {
        Bud bud = BudsRepository.getInstance().findByIdentity( identity );

        if ( bud == null ) {
            return notFound();
        }
        
        Form<BudForm> filledForm = budForm.bindFromRequest();
        
        if(filledForm.hasErrors()) {
            return badRequest(edit_bud.render(bud,filledForm));
        } else {
            BudForm updated = filledForm.get();
            bud.entity().title = updated.title;
            bud.entity().content = updated.content;
            BudEntity.save(bud.entity());
            return redirect( routes.Buds.bud( bud.identity() ) );
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
