package roles;

import java.util.List;

import play.db.ebean.Model;

public interface Role<EntityType extends RoleEntity>
{

    String roleName();

    boolean hasActionNamed( String name );

    RoleAction actionNamed( String name );

    List<RoleAction> actions();

    EntityType roleEntity();

}
