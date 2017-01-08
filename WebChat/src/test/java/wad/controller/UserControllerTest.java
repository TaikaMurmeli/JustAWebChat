package wad.controller;

import java.util.ArrayList;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wad.domain.User;
import wad.repository.UserRepository;
import wad.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import wad.WebChat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
public class UserControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    private AuthenticationManager manager;

    private MockMvc mockMvc;
    private User bob;
    private User greg;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        bob = new User();
        bob.setName("Bob");
        bob.setUsername("bob");
        bob.setPassword("IzzAccept4ble");
        bob.setFriends(new ArrayList());
        bob.setChatrooms(new ArrayList());

        bob = userRepository.save(bob);

        greg = new User();
        greg.setName("Greg");
        greg.setUsername("greg");
        greg.setPassword("IzzAccept4ble");
        greg.setFriends(new ArrayList());
        greg.setChatrooms(new ArrayList());

        greg = userRepository.save(greg);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication user = manager.authenticate(new UsernamePasswordAuthenticationToken("bob", "IzzAccept4ble"));
        context.setAuthentication(user);

        SecurityContextHolder.setContext(context);
    }

    @Test
    public void testSignUp() throws Exception {
        String username = "darthVedur31";
        String name = "Bob";
        String password = "EbinPa55Wurd";
        mockMvc.perform(post("/signup")
                .param("name", name)
                .param("username", username)
                .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attributeExists("signupMessage"))
                .andExpect(flash().attribute("signupMessage", "Account succesfully created!"));
        User user = userRepository.findByUsername(username);

        assertEquals(name, user.getName());
        assertEquals(username, user.getUsername());
        assertNotEquals(password, user.getPassword());

        userRepository.delete(user);
    }

    @Test
    public void signUpFailsWithValidationErrors() throws Exception {
        String username = "123!!";
        String name = "...";
        String password = "wurd";
        mockMvc.perform(post("/signup")
                .param("name", name)
                .param("username", username)
                .param("password", password))
                .andExpect(status().is2xxSuccessful());
//                .andExpect(forwardedUrl("signup"));
                
        User user = userRepository.findByUsername(username);

        assertEquals(user, null);
    }
    
    @Test
    public void signUpFailsWithBadPassword() throws Exception {
        String username = "anders";
        String name = "Anders";
        String passwordNotComplexEnough = "passwurd";
        String passwordTooShort = "short";
        mockMvc.perform(post("/signup")
                .param("name", name)
                .param("username", username)
                .param("password", passwordNotComplexEnough))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attribute("passwordErrorMessage", "Invalid password!"));
        
        mockMvc.perform(post("/signup")
                .param("name", name)
                .param("username", username)
                .param("password", passwordTooShort))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attribute("passwordErrorMessage", "Invalid password!"));
                        
        User user = userRepository.findByUsername(username);

        assertEquals(user, null);
    }
    
    @Test
    public void signUpFailsWithDuplicateUsername() throws Exception {
        String username = "bob";
        String name = "Blerb";
        String password = "EbinPa55Wurd";
        mockMvc.perform(post("/signup")
                .param("name", name)
                .param("username", username)
                .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attributeExists("signupMessage"))
                .andExpect(flash().attribute("signupMessage", "Username already in use!"));

        User user = userRepository.findByUsername(username);

        assertEquals(user.getName(), "Bob");
    }

    @Test
    @Transactional
    public void testAddFriend() throws Exception {

        assertTrue(bob.getFriends().isEmpty());

        mockMvc.perform((post("/friend")).param("username", greg.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"))
                .andExpect(flash().attribute("friendingMessage", greg.getUsername() + " is now your friend!"));

        assertEquals(bob.getFriends().size(), 1);
        assertEquals(bob.getFriends().get(0), greg);

        //Adding friends is one-sided.
        assertTrue(greg.getFriends().isEmpty());
    }

    @Test
    @Transactional
    public void cannotAddSelfAsFriend() throws Exception {

        assertTrue(bob.getFriends().isEmpty());

        mockMvc.perform((post("/friend")).param("username", bob.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"))
                .andExpect(flash().attribute("friendingMessage", "You cannot add yourself as friend. Makes no sense..."));

        assertTrue(bob.getFriends().isEmpty());
    }

    @Test
    @Transactional
    public void testAddingNonExistentUserAsFriend() throws Exception {

        assertTrue(bob.getFriends().isEmpty());

        mockMvc.perform((post("/friend")).param("username", "notAUserName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"))
                .andExpect(flash().attribute("friendingMessage", "Given username doesn not exist."));

        assertTrue(bob.getFriends().isEmpty());
    }

    @After
    @Transactional
    public void tearDown() {
        bob.getFriends().remove(greg);
        greg.getFriends().remove(bob);
        userRepository.delete(bob);
        userRepository.delete(greg);
    }
}
