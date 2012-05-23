package controllers;

import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;

import buds.BudPack;
import buds.BudPacksRepository;
import views.html.budpacks.all_budpacks;
import views.html.budpacks.show_budpack;

public class BudPacks
        extends Controller
{

    public static Result packs()
    {
        List<BudPack> allBudPacks = BudPacksRepository.getInstance().findAll();
        return ok( all_budpacks.render( allBudPacks ) );
    }

    public static Result packUploadForm()
    {
        return TODO;
    }

    public static Result uploadPack()
    {
        return TODO;
    }

    public static Result pack( String pack )
    {
        BudPack budPack = BudPacksRepository.getInstance().findByName( pack );
        if ( budPack == null ) {
            return notFound();
        }
        return ok( show_budpack.render( budPack ) );
    }

    public static Result packConfiguration( String pack )
    {
        return TODO;
    }

    public static Result savePackConfiguration( String pack )
    {
        return TODO;
    }

    public static Result actionForm( String pack, String action )
    {
        return TODO;
    }

    public static Result invokeAction( String pack, String action )
    {
        return TODO;
    }

}
