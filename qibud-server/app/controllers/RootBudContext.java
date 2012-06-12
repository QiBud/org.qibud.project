package controllers;

import buds.Bud;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;


public class RootBudContext extends Action.Simple{
    
    @Override
    public Result call(Http.Context ctx) throws Throwable {
        ctx.args.put("bud:root:identity", Bud.ROOT_BUD_IDENTITY);
        return delegate.call(ctx);
    }
    
    public static String rootBudIdentity() {
        return (String)Http.Context.current().args.get("bud:root:identity");
    }
}
