package domain.events;

import java.io.IOException;
import java.io.InputStream;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.structure.Module;
import org.qi4j.api.value.ValueBuilder;
import org.qibud.eventstore.DomainEvent;
import org.qibud.eventstore.DomainEventAttachment;
import org.qibud.eventstore.DomainEventAttachment.DataProvider;
import org.qibud.eventstore.DomainEventFactory;
import org.qibud.eventstore.DomainEventsSequenceBuilder;
import play.Play;

public class BudEventFactoryImpl
        implements BudEventFactory
{

    @Structure
    private Module module;

    @Service
    private DomainEventFactory domainEventFactory;

    @Override
    public DomainEventsSequenceBuilder createRootBud( DomainEventsSequenceBuilder eventsBuilder )
    {
        DomainEventAttachment attachmentEvent = domainEventFactory.newDomainEventAttachment( new DataProvider()
        {

            @Override
            public InputStream data()
                    throws IOException
            {
                return Play.application().resourceAsStream( "whats.a.bud.svg" );
            }

        } );

        ValueBuilder<RootBudCreatedEvent> createdBuilder = module.newValueBuilder( RootBudCreatedEvent.class );

        RootBudCreatedEvent created = createdBuilder.prototype();
        created.identity().set( "root" );
        created.title().set( "Root Bud" );
        created.content().set( "## This is the Root Bud\nFor now this Bud has no Role and this sample content only." );
        created = createdBuilder.newInstance();

        DomainEvent domainEvent = domainEventFactory.newDomainEvent( created );
        return eventsBuilder.withEvents( domainEvent ).withAttachments( attachmentEvent );
    }

}
