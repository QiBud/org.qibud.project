package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import org.qibud.Dummy;
import org.qibud.eventstore.Usecase;

import views.html.index;

/**
 * Web front controller.
 */
public class Application
        extends Controller
{

    @Usecase( "QiBud Server Index" )
    public static Result index()
    {
        Dummy dummy = new Dummy();
        return ok( index.render() );
    }

}
