package buds;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import play.modules.mongodb.jackson.MongoDB;

import net.vz.mongodb.jackson.JacksonDBCollection;

/**
 * About paging with MongoDB
 * https://groups.google.com/forum/?fromgroups#!topic/mongo-jackson-mapper/aEUuUPOowEw
 */
public class BudEntity
        implements Serializable
{

    public static final long _serialVersionUID = 1L;

    @Id
    public String identity;

    public String title;

    public Date postedAt;

    public String content;

    public static BudEntity findById( String id )
    {
        return db().findOneById( id );
    }

    public static void delete( BudEntity budEntity )
    {
        db().removeById( budEntity.identity );
    }

    public static void save( BudEntity budEntity )
    {
        db().save( budEntity );
        db().find().toArray();
    }

    public static List<BudEntity> findAll()
    {
        return db().find().toArray();
    }

    private static JacksonDBCollection<BudEntity, String> db()
    {
        return MongoDB.getCollection( "buds", BudEntity.class, String.class );
    }

}
