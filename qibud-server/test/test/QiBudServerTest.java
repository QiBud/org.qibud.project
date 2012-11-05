/*
 * Copyright (c) 2012 Paul Merlin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.api.libs.ws.WS;
import play.libs.F.Callback;
import play.mvc.Result;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@Ignore
public class QiBudServerTest
{

    @Test
    public void testInApplication()
    {
        running( fakeApplication(), new Runnable()
        {

            @Override
            public void run()
            {
                System.out.println( "TEST TEST TEST" );
            }

        } );
    }

    @Test
    @Ignore( "There is no HTTP Context available from here." )
    public void testTemplate()
    {
        /*
         Content html = views.html.index.render();
         assertThat( contentType( html ) ).isEqualTo( "text/html" );
         assertThat( contentAsString( html ) ).contains( "QiBud" );
         */
    }

    @Test
    @Ignore( "There is no started application" )
    public void testController()
    {
        Result result = callAction( controllers.routes.ref.Application.index() );
        assertThat( status( result ) ).isEqualTo( OK );
        assertThat( contentType( result ) ).isEqualTo( "text/html" );
        assertThat( charset( result ) ).isEqualTo( "utf-8" );
        assertThat( contentAsString( result ) ).contains( "QiBud" );
    }

    @Test
    public void badRoute()
    {
        Result result = routeAndCall( fakeRequest( GET, "/xx/not/here" ) );
        assertThat( result ).isNull();
    }

    @Test
    @Ignore( "Server is not started!" )
    public void testInHttpServer()
    {
        running( testServer( 3333 ), new Runnable()
        {

            @Override
            public void run()
            {
                assertThat( WS.url( "http://localhost:3333" ).get().await().get().status() ).isEqualTo( OK );
            }

        } );
    }

    private static final Callback<TestBrowser> SIMPLE_FUNCTIONAL_TEST = new Callback<TestBrowser>()
    {

        @Override
        public void invoke( TestBrowser browser )
                throws Throwable
        {
            browser.goTo( "http://localhost:3333" );
            assertThat( browser.$( "h1.page_title" ).getTexts().get( 0 ) ).isEqualTo( "QiBud" );
            if ( false ) {
                browser.$( "a" ).click();
                assertThat( browser.url() ).isEqualTo( "http://localhost:3333/Coco" );
                assertThat( browser.$( "#title", 0 ).getText() ).isEqualTo( "Hello Coco" );
            }
        }

    };

    @Test
    @Ignore( "Server is not started!" )
    public void runInHtmlUnit()
    {
        running( testServer( 3333 ), HtmlUnitDriver.class, SIMPLE_FUNCTIONAL_TEST );
    }

    @Test
    @Ignore( "do not work" )
    // TODO runInFirefox
    public void runInFirefox()
    {
        running( testServer( 3333 ), FirefoxDriver.class, SIMPLE_FUNCTIONAL_TEST );
    }

    @Test
    @Ignore( "do not work" )
    // TODO runInChrome
    public void runInChrome()
    {
        running( testServer( 3333 ), ChromeDriver.class, SIMPLE_FUNCTIONAL_TEST );
    }

}
