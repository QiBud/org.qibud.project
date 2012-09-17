package infrastructure.graphdb;

import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.api.service.ServiceComposite;

@Mixins( GraphDBImpl.class )
public interface GraphDBService
        extends GraphDB, ServiceComposite, ServiceActivation
{
}
