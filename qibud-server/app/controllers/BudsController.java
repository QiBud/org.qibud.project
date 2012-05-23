package controllers;

import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;

import com.mongodb.gridfs.GridFSDBFile;

import buds.Bud;
import buds.BudsRepository;
import storage.AttachmentsDB;
import views.html.buds.all_buds;
import views.html.buds.show_bud;

public class BudsController
        extends Controller
{

    public static Result buds()
    {
        List<Bud> allBuds = BudsRepository.getInstance().findAll();
        return ok( all_buds.render( allBuds ) );
    }

    public static Result budCreateForm()
    {
        return TODO;
    }

    public static Result saveNewBud()
    {
        return TODO;
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
        if ( dbFile.getContentType() != null ) {
            return ok( dbFile.getInputStream() ).as( dbFile.getContentType() );
        }
        return ok( dbFile.getInputStream() );
    }

    public static Result budEditForm( String identity )
    {
        return TODO;
    }

    public static Result saveBud( String identity )
    {
        return TODO;
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
