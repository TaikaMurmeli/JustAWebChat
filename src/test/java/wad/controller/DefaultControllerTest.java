package wad.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wad.WebChat;
import wad.domain.User;
import wad.repository.UserRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static testUtil.TestObjectBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

        bob = userRepository.save(createUser("bob"));
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
    @WithMockUser("bob")
    public void testViewIndex() throws Exception {
        mockMvc.perform((get("/")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("chatrooms"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attribute("welcomeMessage",
                        "You are currently logged in as " + bob.getUsername() + "."))
                .andExpect(view().name("index"));
    }

    @After
    public void tearDown() {
        clearUserData(bob);
        userRepository.delete(bob);
    }
}
