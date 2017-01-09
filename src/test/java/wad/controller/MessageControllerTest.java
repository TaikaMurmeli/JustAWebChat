package wad.controller;

import java.util.ArrayList;
import java.util.List;
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
import wad.domain.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebChat.class)
@WebAppConfiguration
public class MessageControllerTest {

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
        chatroom.setTitle("chatroom number one");
        chatroom.setDescription("this is an example chatroom");
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
    public void testChat() throws Exception {

        List<Message> messages = chatroom.getMessages();

        assertTrue(messages.isEmpty());

        mockMvc.perform(post("/message/" + chatroom.getId())
                .param("content", "first!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatrooms/" + chatroom.getId()));

        messages = chatroom.getMessages();
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0).getContent(), "first!");
        assertEquals(messages.get(0).getChatroom(), chatroom);
        assertEquals(messages.get(0).getAuthor(), bob);
        assertEquals(bob.getMessages().get(0), messages.get(0));
    }

    @After
    @Transactional
    public void tearDown() {

        chatroom.getMessages().clear();
        chatroom.getUsers().clear();
        chatroomRepository.delete(chatroom);

        bob.getMessages().clear();
        bob.getChatrooms().clear();
        userRepository.delete(bob);
    }
}
