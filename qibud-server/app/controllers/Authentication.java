package controllers;

import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With( { RootBudContext.class, AuthContext.class } )
public class Authentication
        extends Controller
{

    public static class Login
    {

        @Constraints.Required
        String username;

        String password;

    }

    static final Form<Login> LOGIN_FORM = form( Login.class );

    public static Result login_form()
    {
        return ok( views.html.auth.login.render( LOGIN_FORM ) );
    }

    public static Result login()
    {
        Form<Login> filledForm = LOGIN_FORM.bindFromRequest();
        if ( filledForm.hasErrors() ) {
            return badRequest( views.html.auth.login.render( filledForm ) );
        }
        boolean loggedIn = true; // TODO
        if ( !loggedIn ) {
            flash( "Invalid username or password." );
            return badRequest( views.html.auth.login.render( filledForm ) );
        }
        return redirect( routes.Application.index() );
    }

    public static Result logout()
    {
        return redirect( routes.Application.index() );
    }

}
