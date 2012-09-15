package domain.aaa;

import domain.roles.RoleAction;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;

@Mixins( UserCheckPasswordAction.Mixin.class )
public interface UserCheckPasswordAction
        extends RoleAction<String, Boolean, RuntimeException>
{

    abstract class Mixin
            implements UserCheckPasswordAction
    {

        @This
        private UserCheckPasswordAction me;

        @Override
        public Boolean invokeAction( String candidate )
                throws RuntimeException
        {
            return true;
        }

    }

}
