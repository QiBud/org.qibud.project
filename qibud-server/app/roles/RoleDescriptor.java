package roles;

import java.util.List;

import play.db.ebean.Model;

public interface RoleDescriptor
{

    String name();

    List<RoleActionDescriptor> actions();

    Class<? extends Model> roleEntity();

    // extensions de vues etc..
}
