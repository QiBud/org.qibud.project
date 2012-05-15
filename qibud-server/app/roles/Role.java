package roles;

import java.util.List;

import play.db.ebean.Model;

public interface Role<EntityType extends Model>
{

    String roleName();

    boolean hasActionNamed( String name );

    RoleAction actionNamed( String name );

    List<RoleAction> actions();

    EntityType roleEntity();

}
