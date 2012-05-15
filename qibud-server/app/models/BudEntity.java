package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.ebean.Model;

@Entity
public class BudEntity
        extends Model
{

    public static final long _serialVersionUID = 1L;

    public static Finder<String, BudEntity> find = new Finder<String, BudEntity>( String.class, BudEntity.class );

    @Id
    @Column( name = "bud_identity" ) // Needed because IDENTITY is a SQL-99 reserved keyword
    public String identity;

    @Column( length = 512 )
    public String title;

    @Temporal( TemporalType.DATE )
    public Date postedAt;

    @Column( length = 30000 )
    public String content;

    public boolean hasAttachment;

}
