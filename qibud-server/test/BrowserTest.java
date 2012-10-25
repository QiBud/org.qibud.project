import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import play.libs.WS;
import org.junit.Test;
import play.test.*;
import play.libs.F.*;



import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;
import static org.fluentlenium.core.filter.FilterConstructor.*;



public class BrowserTest {
   
   
    
   @Test
    public void runInBrowser() {
        running(testServer(3337, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
               assertThat(WS.url("http://localhost:3337").get().get().getStatus()).isEqualTo(OK);
               browser.goTo("http://localhost:3337/bud/root");
               assertThat(browser.$("h1").first().getText()).isEqualTo("Root Bud");
               //goTo new bud
               browser.$("#new").click();              
               System.out.println(browser.url());
               browser.await().atMost(20, TimeUnit.SECONDS);
               //create a new bud and submit the form
               browser.fill("input").with("Browser Test");
               browser.executeScript("find_epic_textarea().val('Browser Test Content');");
               browser.submit("input[id=save]");
               System.out.println(browser.url());
               assertThat(browser.$("h1").first().getText()).isEqualTo("Browser Test");
               
               
               
            }
        });
    }

}
