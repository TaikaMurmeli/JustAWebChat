package wad.controller;

import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wad.WebChat;
import wad.domain.User;
import wad.repository.UserRepository;
import wad.service.UserService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
public class DefaultControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    AuthenticationManager manager;

    private MockMvc mockMvc;
    private User bob;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        bob = new User();
        bob.setName("Bob");
        bob.setUsername("bob");
        bob.setPassword("password");
        bob.setFriends(new ArrayList());
        bob.setChatrooms(new ArrayList());
        bob.setMessages(new ArrayList());
        bob = userRepository.save(bob);

        bob = userRepository.save(bob);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication user = manager.authenticate(new UsernamePasswordAuthenticationToken("bob", "password"));
        context.setAuthentication(user);

        SecurityContextHolder.setContext(context);
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("login"));
    }

    @Test
    public void testSignup() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("signup"));
    }
    
    @Test
    public void testViewLogin() throws Exception {
        mockMvc.perform(get("/info"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("info"));
    }
    
    
    
    @Test
    public void testViewIndex() throws Exception {
        mockMvc.perform((get("/")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("chatrooms"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attributeExists("welcomeMessage"))
                .andExpect(view().name("index"));
    }

    @After
    public void tearDown() {
        userRepository.delete(bob);
    }
}
