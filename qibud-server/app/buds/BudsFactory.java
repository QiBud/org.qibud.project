package buds;

import java.io.InputStream;

import models.BudEntity;
import storage.AttachmentsDB;
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
        BudEntity rootBudEntity = new BudEntity();
        rootBudEntity.identity = Bud.ROOT_BUD_IDENTITY;
        rootBudEntity.title = "Root Bud";
        rootBudEntity.save();

        // Create ROOT BudNode
        GraphDB.getInstance().createBudNode( Bud.ROOT_BUD_IDENTITY );
        // Create ROOT BudAttachments
        String filename = "whats.a.bud.svg";
        InputStream attachmentInputStream = getClass().getResourceAsStream( filename );
        AttachmentsDB.getInstance().storeAttachment( Bud.ROOT_BUD_IDENTITY, filename, attachmentInputStream );

        return new Bud( rootBudEntity );
    }

    private BudsFactory()
    {
    }

}
