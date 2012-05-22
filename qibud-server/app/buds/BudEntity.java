package buds;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import play.modules.mongodb.jackson.MongoDB;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;

public class BudEntity
        implements Serializable
{

    public static final long _serialVersionUID = 1L;

    private static final JacksonDBCollection<BudEntity, String> db = MongoDB.getCollection( "bud-entities", BudEntity.class, String.class );

    @Id
    public String identity;

    public String title;

    public Date postedAt;

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
