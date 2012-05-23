package buds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO Move this code to Bud.
 */
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

    public Bud findRootBud()
    {
        return findByIdentity( Bud.ROOT_BUD_IDENTITY );
    }

    public List<Bud> findAll()
    {
        List<BudEntity> allBudEntities = BudEntity.findAll();
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
        BudEntity entity = BudEntity.findById( identity );
        if ( entity == null ) {
            return null;
        }
        return new Bud( entity );
    }

    private BudsRepository()
    {
    }

}
