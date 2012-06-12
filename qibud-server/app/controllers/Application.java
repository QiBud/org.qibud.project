package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import org.qibud.Dummy;

import play.mvc.With;
import views.html.index;

/**
 * Web front controller.
 */
@With(RootBudContext.class)
public class Application
        extends Controller
{

    public static Result index()
    {
        Dummy dummy = new Dummy();
        return ok( index.render() );
    }

}
