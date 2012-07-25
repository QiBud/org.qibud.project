/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 * Copyright (c) 2012, Samuel Loup. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
