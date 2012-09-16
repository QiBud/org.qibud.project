package domain.roles;

import domain.buds.Bud;
import org.qi4j.api.common.MetaInfo;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.Property;

@BudRole( "foo" )
@Mixins( DynamicRole.Mixin.class )
public interface DynamicRole
{

    Property<String> fooState();

    @BudAction
    void doThis();

    @BudAction
    void doThisWithParams( @BudActionParam( "foo" ) String foo );

    @BudAction
    String doThisWithReturn();

    //@BudAction
    //void doThisWithValueParams( @BudActionParam( "foo" ) FooValue foo );

    class BudRoleMetaInfo
    {
        // ??? Populated at assembly time with data from annotations?
        // ??? For what?
    }

    abstract class Mixin
            implements DynamicRole
    {

        @This
        private MetaInfo meta;

        @This
        private Bud bud;

        @This
        private DynamicRole role;

        @Override
        public void doThis()
        {
            role.fooState().set( bud.content().get() );
        }

        @Override
        public void doThisWithParams( String foo )
        {
            bud.content().set( foo );
        }

    }

}
