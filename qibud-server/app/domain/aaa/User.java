package domain.aaa;

import domain.roles.Role;
import org.qi4j.api.common.Optional;
import org.qi4j.api.property.Property;

public interface User
        extends Role
{

    @Optional
    Property<String> username();

    @Optional
    Property<String> password();

}
