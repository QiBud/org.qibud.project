package controllers;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

/**
 * Compose EventStore handling.
 * 
 * Compose your Actions with this Aspect and record 
 */
public class EventStoreHttpAspect
        extends Action.Simple
{

    @Override
    public Result call( Context ctx )
            throws Throwable
    {
        
        return delegate.call( ctx );
    }

}
