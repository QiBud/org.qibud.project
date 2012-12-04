package domain.teams;

import domain.buds.Bud;
import org.qi4j.api.association.Association;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;

@Mixins( Team.Mixin.class )
public interface Team
    extends EntityComposite
{

    Property<String> title();

    Association<Bud> bud();

    void changeTitle( String title );

    abstract class Mixin
        implements Team
    {

        @This
        private Team team;

        @Override
        public void changeTitle( String title )
        {
            team.title().set( title );
            team.bud().get().title().set( title );
        }

    }

}
