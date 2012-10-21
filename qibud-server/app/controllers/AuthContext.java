package controllers;

import domain.buds.Bud;
import domain.buds.BudsRepository;
import org.codeartisans.playqi.PlayQi;
import play.Play;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

public class AuthContext
        extends Action.Simple
{

    public static final String USER_ID_SESSION_KEY = "user:current:identity";

    private static final String USER_BUD_KEY = "user:current:bud";

    @Override
    public Result call( Http.Context ctx )
            throws Throwable
    {
        String layer = Play.application().configuration().getString( "qi4j.inject.layer" );
        String module = Play.application().configuration().getString( "qi4j.inject.module" );
        BudsRepository budsRepository = PlayQi.service( layer, module, BudsRepository.class );
        String userIdentity = ctx.session().get( USER_ID_SESSION_KEY );
        if ( userIdentity != null && userIdentity.length() > 0 ) {
            Bud userBud = budsRepository.findByIdentity( userIdentity );
            if ( userBud == null ) {
                ctx.session().remove( USER_ID_SESSION_KEY );
                ctx.args.remove( USER_BUD_KEY );
            } else {
                ctx.args.put( USER_BUD_KEY, userBud );
            }
        } else {
            ctx.args.remove( USER_BUD_KEY );
        }
        return delegate.call( ctx );
    }

    public static Bud userBud()
    {
        return ( Bud ) Http.Context.current().args.get( USER_BUD_KEY );
    }

}
