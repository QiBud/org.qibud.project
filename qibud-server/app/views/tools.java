package views;

import play.api.templates.Html;

import org.apache.commons.lang.RandomStringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isEmpty;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class tools
{

    public static String make_id()
    {
        return RandomStringUtils.randomAlphanumeric( 6 );
    }

    public static Html markdown( String markdown )
    {
        if ( isEmpty( markdown ) ) {
            return new Html( EMPTY );
        }
        PegDownProcessor pegDownProcessor = new PegDownProcessor();
        String html = pegDownProcessor.markdownToHtml( markdown );
        return new Html( html );
    }

    private static final Logger LOGGER = LoggerFactory.getLogger( "views" );

    public static void log_trace( Object message )
    {
        LOGGER.trace( message == null ? "null" : message.toString() );
    }

    public static void log_debug( Object message )
    {
        LOGGER.debug( message == null ? "null" : message.toString() );
    }

    public static void log_warn( Object message )
    {
        LOGGER.warn( message == null ? "null" : message.toString() );
    }

    public static void log_info( Object message )
    {
        LOGGER.info( message == null ? "null" : message.toString() );
    }

    public static void log_error( Object message )
    {
        LOGGER.error( message == null ? "null" : message.toString() );
    }

}
