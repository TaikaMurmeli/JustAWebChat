package wad.selenium;

// importit
import org.fluentlenium.adapter.FluentTest;
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
import org.springframework.beans.factory.annotation.Autowired;
import static testUtil.TestObjectBuilder.*;
import wad.domain.User;
import wad.repository.UserRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class SignupTest extends FluentTest {

    @Autowired
    UserRepository userRepository;

    @Value("${local.server.port}")
    private int serverPort;
    private WebDriver webDriver = new HtmlUnitDriver();
    private final String username = "jack";
    private final String password = "Izg00dBazzwurd";
    private final String email = "email@domain.com";

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
        fill(find("#email")).with(email);
        fill(find("#password")).with(password);
        submit(find("form").first());

        assertTrue(pageSource().contains("Account succesfully created!"));

        goTo(getUrl());

        fill(find("#username")).with(username);
        fill(find("#password")).with(password);
        submit(find("form").first());

        assertTrue(pageSource()
                .contains("You are currently logged in as " + username + "."));
        
        userRepository.delete(userRepository.findByUsername(username));
    }

    @Test
    public void testBadFormInputAllBad() {
        goTo(getUrl() + "/signup");

        fill(find("#name")).with("...");
        fill(find("#username")).with();
        fill(find("#email")).with("email");
        fill(find("#password")).with("wurd");
        submit(find("form").first());

        assertTrue(!pageSource().contains("Account succesfully created!"));
        assertTrue(pageSource().contains("can only contain characters and spaces."));
        assertTrue(pageSource().contains("may not be empty"));
        assertTrue(pageSource().contains("not a well-formed email address"));
        assertTrue(pageSource().contains("not a valid password"));
    }
    
    @Test
    public void testBadFormInputEmptyName() {
        goTo(getUrl() + "/signup");

        fill(find("#name")).with();
        fill(find("#username")).with("user123");
        fill(find("#email")).with(VALID_EMAIL);
        fill(find("#password")).with(VALID_PLAIN_TEXT_PASSWORD);
        submit(find("form").first());

        assertTrue(pageSource().contains("may not be empty"));
    }
    
    public void testBadFormInputEmptyUserName() {
        goTo(getUrl() + "/signup");

        fill(find("#name")).with("Bob");
        fill(find("#username")).with("");
        fill(find("#email")).with(VALID_EMAIL);
        fill(find("#password")).with(VALID_PLAIN_TEXT_PASSWORD);
        submit(find("form").first());

        assertTrue(pageSource().contains("may not be empty"));
    }

    public void testBadFormInputEmptyEmail() {
        goTo(getUrl() + "/signup");

        fill(find("#name")).with("Bob");
        fill(find("#username")).with("user123");
        fill(find("#email")).with();
        fill(find("#password")).with(VALID_PLAIN_TEXT_PASSWORD);
        submit(find("form").first());

        assertTrue(pageSource().contains("may not be empty"));
    }
    
    public void testBadFormInputEmptyPassword() {
        goTo(getUrl() + "/signup");

        fill(find("#name")).with();
        fill(find("#username")).with("user123");
        fill(find("#email")).with(VALID_EMAIL);
        fill(find("#password")).with();
        submit(find("form").first());

        assertTrue(pageSource().contains("may not be empty"));
    }
    @Test
    public void noDuplicateUsernames() {
        User bob = userRepository.save(createUser("bob"));
        goTo(getUrl() + "/signup");

        fill(find("#name")).with("Alfred");
        fill(find("#username")).with(bob.getUsername());
        fill(find("#email")).with("email@email.com");
        fill(find("#password")).with(VALID_PLAIN_TEXT_PASSWORD);
        submit(find("form").first());

        assertTrue(!pageSource().contains("Account succesfully created!"));
        assertTrue(pageSource().contains("username already in use"));
        
        clearUserData(bob);
        userRepository.delete(bob);
    }

}
