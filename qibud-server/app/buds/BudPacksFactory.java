package buds;

import java.util.Collections;
import java.util.List;

import buds.BudPack;

public class BudPacksFactory
{

    private static BudPacksFactory instance;

    public static synchronized BudPacksFactory getInstance()
    {
        if ( instance == null ) {
            instance = new BudPacksFactory();
        }
        return instance;
    }

    public List<BudPack> findAll()
    {
        return Collections.emptyList();
    }

    public BudPack findByIdentity( String identity )
    {
        return null;
    }

    private BudPacksFactory()
    {
    }

}
