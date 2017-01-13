package wad.controller;

import javax.transaction.Transactional;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wad.WebChat;
import wad.domain.Chatroom;
import wad.domain.User;
import wad.repository.ChatroomRepository;
import wad.repository.MessageRepository;
import wad.repository.UserRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static testUtil.TestObjectBuilder.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
public class ChatroomControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    AuthenticationManager manager;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatroomRepository chatroomRepository;

    private MockMvc mockMvc;
    private Chatroom chatroom;
    private User bob;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.
                webAppContextSetup(webApplicationContext).
                build();

        bob = userRepository.save(createUser("bob"));

        chatroom = createChatroom();
        chatroom.addUser(bob);
        chatroom = chatroomRepository.save(chatroom);

        bob.addChatroom(chatroom);
        bob = userRepository.save(bob);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(
                "bob", "IzzAccept4ble"));
        context.setAuthentication(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    @Transactional
    public void viewingChatroomSuccessfullyAddsChatroomData() throws Exception {
        mockMvc.perform(get("/chatrooms/" + chatroom.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("chatroom", chatroom));
    }

    @Test
    @Transactional
    public void viewingNonExistentChatroomRedirectsToIndex() throws Exception {
        mockMvc.perform(get("/chatrooms/" + "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @Transactional
    public void viewingChatroomWithoutPermissionDoesNotShowData() throws Exception {
        Chatroom chatroom2 = chatroomRepository.save(createChatroom());

        mockMvc.perform(get("/chatrooms/" + chatroom2.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("unauthorizedAccess", "Were sorry, but you do not belong here."))
                .andExpect(model().attributeDoesNotExist("chatroom"));

        clearChatroomData(chatroom2);
        chatroomRepository.delete(chatroom2);
    }

    @Test
    @Transactional
    public void testCreateChatroom() throws Exception {
        int chatRoomAmount = bob.getChatrooms().size();
        mockMvc.perform(post("/chatrooms/")
                .param("title", "new chatroom")
                .param("description", "testing testing..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertEquals(bob.getChatrooms().size(), chatRoomAmount + 1);
        assertEquals(bob.getChatrooms().get(chatRoomAmount).getTitle(),
                "new chatroom");
    }

    @Test
    @Transactional
    public void testAddingMemberToChatroom() throws Exception {
        User matt = userRepository.save(createUser("matt"));

        mockMvc.perform(post("/chatrooms/addUser/" + chatroom.getId())
                .param("username", "matt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatrooms/" + chatroom.getId()));

        assertTrue(chatroom.getUsers().contains(matt));
        assertTrue(matt.getChatrooms().contains(chatroom));

        clearUserData(matt);
        userRepository.delete(matt);
    }

    @Test
    @Transactional
    public void testAddingNonExistentUserToChatroom() throws Exception {

        assertEquals(chatroom.getUsers().size(), 1);

        mockMvc.perform(post("/chatrooms/addUser/" + chatroom.getId())
                .param("username", "matt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatrooms/" + chatroom.getId()));

        assertEquals(chatroom.getUsers().size(), 1);
    }

    @Test
    @Transactional
    public void noDuplicateUsersInChatroom() throws Exception {

        assertTrue(chatroom.getUsers().contains(bob));
        assertEquals(chatroom.getUsers().size(), 1);

        mockMvc.perform(post("/chatrooms/addUser/" + chatroom.getId())
                .param("username", "bob"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatrooms/" + chatroom.getId()));

        assertTrue(chatroom.getUsers().contains(bob));
        assertEquals(chatroom.getUsers().size(), 1);
    }

    @Test
    @Transactional
    public void testLeavingChatroom() throws Exception {
        User greg = userRepository.save(createUser("greg"));
        Chatroom chatroom2 = createChatroom();
        chatroom2.addUser(bob);
        chatroom2.addUser(greg);
        chatroom2 = chatroomRepository.save(chatroom2);
        bob.addChatroom(chatroom2);
        greg.addChatroom(chatroom2);

        assertTrue(bob.getChatrooms().contains(chatroom2));
        mockMvc.perform(post("/chatrooms/leave/" + chatroom2.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertTrue(!bob.getChatrooms().contains(chatroom2));
        assertTrue(greg.getChatrooms().contains(chatroom2));

        clearUserData(greg);
        userRepository.delete(greg);

        clearChatroomData(chatroom2);
        chatroomRepository.delete(chatroom2);
    }

    @Test
    @Transactional
    public void leavingChatroomAsLastPersonDestroysTheChatroom() throws Exception {
        Chatroom chatroom2 = createChatroom();
        chatroom2.addUser(bob);
        chatroom2 = chatroomRepository.save(chatroom2);
        bob.addChatroom(chatroom2);

        assertTrue(chatroom2.getUsers().contains(bob));
        assertEquals(chatroom2.getUsers().size(), 1);
        mockMvc.perform(post("/chatrooms/leave/" + chatroom2.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertTrue(!chatroomRepository.exists(chatroom2.getId()));
    }

    @After
    public void tearDown() {
        clearChatroomData(chatroom);
        chatroomRepository.delete(chatroom);

        clearUserData(bob);
        userRepository.delete(bob);

    }

}
