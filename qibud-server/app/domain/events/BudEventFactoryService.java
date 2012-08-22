package domain.events;

import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;

@Mixins( BudEventFactoryImpl.class )
public interface BudEventFactoryService
        extends BudEventFactory, ServiceComposite
{
}
