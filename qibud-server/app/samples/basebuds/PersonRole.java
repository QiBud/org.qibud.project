package samples.basebuds;

import java.util.Collections;
import java.util.List;

import roles.Role;
import roles.RoleAction;
import roles.RoleActionNotFound;

public class PersonRole
        implements Role<PersonEntity>
{

    private static final String ROLENAME = "Person";

    private final PersonEntity entity;

    public PersonRole( PersonEntity entity )
    {
        this.entity = entity;
    }

    @Override
    public String roleName()
    {
        return ROLENAME;
    }

    @Override
    public boolean hasActionNamed( String name )
    {
        return false;
    }

    @Override
    public RoleAction actionNamed( String name )
    {
        throw new RoleActionNotFound( name + " does not exist" );
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
