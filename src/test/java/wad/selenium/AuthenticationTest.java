package wad.selenium;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;
import static testUtil.TestObjectBuilder.createUser;
import wad.repository.UserRepository;
import static testUtil.TestObjectBuilder.VALID_PLAIN_TEXT_PASSWORD;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class AuthenticationTest extends FluentTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Value("${local.server.port}")
    private int serverPort;
    private WebDriver webDriver = new HtmlUnitDriver();

    private String getUrl() {
        return "http://localhost:" + serverPort;
    }

    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @Test
    public void viewingUnauthorizedPagesRedirectsToLogin() {
        goTo(getUrl());
        assertEquals("Login", title());

        goTo(getUrl() + "/index");
        assertEquals("Login", title());

        goTo(getUrl() + "/chatrooms/1");
        assertEquals("Login", title());

        goTo(getUrl() + "/chatrooms");
        assertEquals("Login", title());
    }

    @Test
    @WithMockUser
    public void authenticatedUserRedirectsToIndexWithImproperUrlPath() {
        userRepository.save(createUser("user"));

        goTo(getUrl());

        fill(find("#username")).with("user");
        fill(find("#password")).with(VALID_PLAIN_TEXT_PASSWORD);
        submit(find("form").first());

        goTo(getUrl());
        assertEquals("JustAWebChat", title());

        goTo(getUrl() + "/whatIsThis");
        assertEquals("JustAWebChat", title());

        goTo(getUrl() + "/info");
        assertEquals("Info", title());
    }
}
