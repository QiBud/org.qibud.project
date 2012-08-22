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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.modules.mongodb.jackson.MongoDB;

public class BudEntity
        implements Serializable
{

    public static final long _serialVersionUID = 1L;

    private static final JacksonDBCollection<BudEntity, String> db = MongoDB.getCollection( "bud-entities", BudEntity.class, String.class );

    @Id
    public String identity;

    @Constraints.Required
    @Constraints.MinLength( 3 )
    @Constraints.MaxLength( 1024 )
    @Formats.NonEmpty
    public String title;

    @Constraints.Required
    @Formats.DateTime( pattern = "YYYY-MM-dd" )
    public Date postedAt;

    @Constraints.MaxLength( 10000 )
    public String content;

    public static BudEntity findById( String id )
    {
        return db.findOneById( id );
    }

    public static void delete( BudEntity budEntity )
    {
        db.removeById( budEntity.identity );
    }

    public static void save( BudEntity budEntity )
    {
        db.save( budEntity );
    }

    public static List<BudEntity> findAll()
    {
        return db.find().toArray();
    }

    public static List<BudEntity> findPage( int page )
    {
        if ( page < 1 ) {
            page = 1;
        }
        return db.find().skip( ( page - 1 ) * 32 ).limit( 32 ).toArray();
    }

}
