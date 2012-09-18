package views;

import application.bootstrap.RoleDescriptor;
import domain.buds.Bud;
import domain.roles.Role;
import java.util.Collection;
import java.util.Collections;
import org.qi4j.functional.Iterables;

/**
 * Template helper created by controllers and used to group Bud data frequently used by views.
 */
public class BudViewData
{

    public final Bud bud;

    public final Collection<Role> roles;

    public final Collection<RoleDescriptor> unused_roles;

    public BudViewData( Bud bud, Collection<RoleDescriptor> unused_roles )
    {
        this.bud = bud;
        this.roles = Collections.unmodifiableCollection( Iterables.toList( bud.roles() ) );
        this.unused_roles = Collections.unmodifiableCollection( unused_roles );
    }

}
