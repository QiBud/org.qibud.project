package buds;

import java.util.List;

import roles.RoleDescriptor;

public interface BudPack
{

    String name();

    String description();

    String version();

    List<RoleDescriptor> roles();

    // TODO Add Dependencies
}
