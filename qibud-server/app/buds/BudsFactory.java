package buds;

import models.BudEntity;
import storage.GraphDB;

public class BudsFactory
{

    private static BudsFactory instance;

    public static synchronized BudsFactory getInstance()
    {
        if ( instance == null ) {
            instance = new BudsFactory();
        }
        return instance;
    }

    public Bud createRootBud()
    {
        // Create ROOT BudEntity
        // TODO Move to BudFactory
        BudEntity rootBudEntity = new BudEntity();
        rootBudEntity.identity = Bud.ROOT_BUD_IDENTITY;
        rootBudEntity.title = "Root Bud";
        rootBudEntity.save();

        // Create ROOT BudNode
        GraphDB.getInstance().createBudNode( Bud.ROOT_BUD_IDENTITY );

        // Create ROOT BudAttachments?

        return new Bud( rootBudEntity );
    }

    private BudsFactory()
    {
    }

}
