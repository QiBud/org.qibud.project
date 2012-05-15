package buds;

import java.util.Collections;
import java.util.List;

import models.BudEntity;
import roles.Role;

public class Bud
{

    private final BudEntity entity;

    /* package */ Bud( BudEntity entity )
    {
        this.entity = entity;
    }

    public String identity()
    {
        return entity.identity;
    }

    public BudEntity entity()
    {
        return entity;
    }

    public BudNode graphNode()
    {
        return null;
    }

    public BudAttachments attachments()
    {
        return null;
    }

    public List<Role> roles()
    {
        return Collections.emptyList();
    }

}
