package samples.basebuds;

import play.modules.mongodb.jackson.MongoDB;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;

import roles.RoleEntity;

public class PersonEntity
        implements RoleEntity
{

    private static final JacksonDBCollection<PersonEntity, String> db = MongoDB.getCollection( "person-entities", PersonEntity.class, String.class );

    @Id
    public String identity;

}
