package wad.controller;

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
import wad.WebChat;
import org.springframework.security.test.context.support.WithMockUser;
import static testUtil.TestObjectBuilder.*;
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

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        bob = userRepository.save(createUser("bob"));
    }

    @Test
    public void testSignUp() throws Exception {
        String username = "darthVedur31";

        mockMvc.perform(post("/signup")
                .param("name", "Bob")
                .param("username", username)
                .param("password", VALID_PLAIN_TEXT_PASSWORD)
                .param("email", VALID_EMAIL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attribute("successfulSignupMessage",
                        "Account succesfully created!"));
        User user = userRepository.findByUsername(username);

        assertEquals("Bob", user.getName());
        assertEquals(username, user.getUsername());
        //test for not saving plain text password
        assertNotEquals(VALID_PLAIN_TEXT_PASSWORD, user.getPassword());

        userRepository.delete(user);
    }

    @Test
    public void signUpFailsWithValidationErrors() throws Exception {
        String username = "...";

        mockMvc.perform(post("/signup")
                .param("name", "123!!")
                .param("username", username)
                .param("password", "")
                .param("email", ""))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeHasFieldErrors(
                        "user", "name", "username", "email"));

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
                .param("password", passwordNotComplexEnough)
                .param("email", VALID_EMAIL))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("passwordErrorMessage",
                        "not a valid password"));

        mockMvc.perform(post("/signup")
                .param("name", name)
                .param("username", username)
                .param("password", passwordTooShort)
                .param("email", VALID_EMAIL))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("passwordErrorMessage",
                        "not a valid password"));

        User user = userRepository.findByUsername(username);

        assertEquals(user, null);
    }

    @Test
    public void signUpFailsWithDuplicateUsername() throws Exception {
        mockMvc.perform(post("/signup")
                .param("name", "Blerb")
                .param("username", bob.getUsername())
                .param("password", VALID_PLAIN_TEXT_PASSWORD)
                .param("email", VALID_EMAIL))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("usernameUsedErrorMessage",
                        "username already in use"));

        User user = userRepository.findByUsername(bob.getUsername());
        assertEquals(user, bob);
    }

    @Test
    @Transactional
    @WithMockUser("bob")
    public void testAddFriend() throws Exception {

        User greg = userRepository.save(createUser("greg"));

        assertTrue(bob.getFriends().isEmpty());

        mockMvc.perform((post("/friend")).param("username", greg.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("friendingMessage", greg.getUsername()
                        + " is now your friend!"));

        assertEquals(bob.getFriends().size(), 1);
        assertEquals(bob.getFriends().get(0), greg);

        //Adding friends is one-sided.
        assertTrue(greg.getFriends().isEmpty());

        clearUserData(greg);
        userRepository.delete(greg);

    }

    @Test
    @Transactional
    @WithMockUser("bob")
    public void cannotAddSelfAsFriend() throws Exception {

        assertTrue(bob.getFriends().isEmpty());

        mockMvc.perform((post("/friend")).param("username", bob.getUsername()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("friendingMessage",
                        "You cannot add yourself as friend. Makes no sense..."));

        assertTrue(bob.getFriends().isEmpty());
    }

    @Test
    @Transactional
    @WithMockUser("bob")
    public void testAddingNonExistentUserAsFriend() throws Exception {

        assertTrue(bob.getFriends().isEmpty());

        mockMvc.perform((post("/friend")).param("username", "notAUserName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("friendingMessage",
                        "Given username does not exist."));

        assertTrue(bob.getFriends().isEmpty());
    }
    
    @Test
    @Transactional
    @WithMockUser("bob")
    public void testRemovingFriend() throws Exception {
        User greg = userRepository.save(createUser("greg"));
        bob.addFriend(greg);
        
        mockMvc.perform((post("/remove-friend/" + greg.getUsername())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("friendingMessage",
                        "greg is no longer your friend."));
        
        assertTrue(!bob.getFriends().contains(greg));
        
        clearUserData(greg);
        userRepository.delete(greg);
    }
    
    @After
    @Transactional
    public void tearDown() {
        clearUserData(bob);
        userRepository.delete(bob);
    }
}
