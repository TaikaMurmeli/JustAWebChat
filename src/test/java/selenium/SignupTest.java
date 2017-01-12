
package selenium;

// importit

import org.fluentlenium.adapter.FluentTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wad.WebChat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SignupTest extends FluentTest {

    @Value("${local.server.port}")
    private int serverPort;
    private WebDriver webDriver = new HtmlUnitDriver();
    private final String username = "jack";
    private final String password = "Izg00dBazzwurd";

    private String getUrl() {
        return "http://localhost:" + serverPort;
    }

    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @Test
    public void canSignUp() {
        goTo(getUrl());

        click(find("a").first());
        assertEquals("Signup", title());

        fill(find("#name")).with("Jack");
        fill(find("#username")).with(username);
        fill(find("#password")).with(password);
        submit(find("form").first());

        assertTrue(pageSource().contains("Account succesfully created!"));
        
        goTo(getUrl());
        
        fill(find("#username")).with(username);
        fill(find("#password")).with(password);
        submit(find("form").first());
        
        assertTrue(pageSource()
                .contains("You are currently logged in as " + username + "."));
        
        
    }
 
}