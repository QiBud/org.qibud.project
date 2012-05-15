package controllers;

import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;

import buds.Bud;
import buds.BudsRepository;
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
