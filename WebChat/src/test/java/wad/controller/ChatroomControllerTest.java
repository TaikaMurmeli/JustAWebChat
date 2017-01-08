package wad.controller;

import java.util.ArrayList;
import javax.transaction.Transactional;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.springframework.test.web.servlet.MvcResult;
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
    private Chatroom chatroom2;
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

        chatroom = new Chatroom();
        chatroom.setTitle("test chatroom one");
        chatroom.setDescription("this is a test chatroom");
        chatroom.setUsers(new ArrayList());
        chatroom.setMessages(new ArrayList());
        chatroom.addUser(bob);
        chatroom = chatroomRepository.save(chatroom);

        bob.addChatroom(chatroom);
        bob = userRepository.save(bob);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication user = manager.authenticate(new UsernamePasswordAuthenticationToken("bob", "password"));
        context.setAuthentication(user);

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
        chatroom2 = new Chatroom();
        chatroom2.setTitle("test chatroom two");
        chatroom2.setDescription("bob doesn't belong here");
        chatroom2.setUsers(new ArrayList());
        chatroom2.setMessages(new ArrayList());
        chatroom2 = chatroomRepository.save(chatroom2);
        
        mockMvc.perform(get("/chatrooms/" + chatroom2.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("unauthorizedAccess", "Were sorry, but you do not belong here."))
                .andExpect(model().attributeDoesNotExist("chatroom"));
        
        chatroom2.getMessages().clear();
        chatroom2.getUsers().clear();
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
        User matt = new User();
        matt.setName("Matt");
        matt.setUsername("matt");
        matt.setPassword("password");
        matt.setFriends(new ArrayList());
        matt.setChatrooms(new ArrayList());
        matt.setMessages(new ArrayList());
        matt = userRepository.save(matt);
       
        mockMvc.perform(post("/chatrooms/addUser/" + chatroom.getId())
                .param("username", "matt"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatrooms/" + chatroom.getId()));

        assertTrue(chatroom.getUsers().contains(matt));
        assertTrue(matt.getChatrooms().contains(chatroom));
        
        matt.getChatrooms().clear();
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

    @After
    public void tearDown() {
        chatroom.getMessages().clear();
        chatroom.getUsers().clear();
        chatroomRepository.delete(chatroom);

        bob.getMessages().clear();
        bob.getChatrooms().clear();
        userRepository.delete(bob);

    }

}
