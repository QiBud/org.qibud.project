package buds;

import java.util.Collections;
import java.util.List;

import buds.BudPack;

public class BudPacksRepository
{

    private static BudPacksRepository instance;

    public static synchronized BudPacksRepository getInstance()
    {
        if ( instance == null ) {
            instance = new BudPacksRepository();
        }
        return instance;
    }

    public List<BudPack> findAll()
    {
        return Collections.emptyList();
    }

    public BudPack findByName( String packName )
    {
        return null;
    }

    private BudPacksRepository()
    {
    }

}
