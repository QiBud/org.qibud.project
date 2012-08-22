/*
 * Copyright (c) 2012, Paul Merlin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package views;

import org.apache.commons.lang.RandomStringUtils;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.templates.Html;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isEmpty;

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
