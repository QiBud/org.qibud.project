package buds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.BudEntity;

public class BudsRepository
{

    private static BudsRepository instance;

    public static synchronized BudsRepository getInstance()
    {
        if ( instance == null ) {
            instance = new BudsRepository();
        }
        return instance;
    }

    public List<Bud> findAll()
    {
        List<BudEntity> allBudEntities = BudEntity.find.all();
        if ( allBudEntities == null || allBudEntities.isEmpty() ) {
            return Collections.emptyList();
        }
        List<Bud> allBuds = new ArrayList<Bud>();
        for ( BudEntity eachEntity : allBudEntities ) {
            allBuds.add( new Bud( eachEntity ) );
        }
        return allBuds;
    }

    public Bud findByIdentity( String identity )
    {
        BudEntity entity = BudEntity.find.byId( identity );
        if ( entity == null ) {
            return null;
        }
        return new Bud( entity );
    }

    private BudsRepository()
    {
    }

}
