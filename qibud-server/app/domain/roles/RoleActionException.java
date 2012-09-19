package domain.roles;

import utils.QiBudException;

public class RoleActionException
        extends QiBudException
{

    public RoleActionException( String string )
    {
        super( string );
    }

    public RoleActionException( Throwable thrwbl )
    {
        super( thrwbl );
    }

    public RoleActionException( String string, Throwable thrwbl )
    {
        super( string, thrwbl );
    }

}
