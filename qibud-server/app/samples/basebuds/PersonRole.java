package samples.basebuds;

import java.util.Collections;
import java.util.List;

import roles.Role;
import roles.RoleAction;

public class PersonRole
        implements Role<PersonEntity>
{

    @Override
    public String roleName()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public boolean hasActionNamed( String name )
    {
        return false;
    }

    @Override
    public RoleAction actionNamed( String name )
    {
        return null;
    }

    @Override
    public List<RoleAction> actions()
    {
        return Collections.emptyList();
    }

    @Override
    public PersonEntity roleEntity()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

}
