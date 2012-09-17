package domain.budpacks.aaa;

import domain.roles.BudRole;
import domain.roles.Role;
import org.qi4j.api.common.Optional;
import org.qi4j.api.property.Property;

@BudRole( name = "user" )
public interface User
        extends Role
{

    @Optional
    Property<String> username();

    @Optional
    Property<String> password();

}
