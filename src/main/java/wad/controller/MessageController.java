package wad.controller;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wad.domain.Chatroom;
import wad.domain.Message;
import wad.domain.User;
import wad.repository.ChatroomRepository;
import wad.repository.MessageRepository;
import wad.repository.UserRepository;
import wad.service.UserService;

@Controller
@RequestMapping("message")
public class MessageController {
    
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    
    @Transactional
    @RequestMapping(value = "/{chatroomId}", method = RequestMethod.POST)
    public String sendMessage(@PathVariable long chatroomId,
            @ModelAttribute Message message) {
        User author = userService.getAuthenticatedUser();
        Chatroom chatroom = chatroomRepository.findOne(chatroomId);
        message.setAuthor(author);
        message.setChatroom(chatroom);
        message = messageRepository.save(message);
        author.getMessages().add(message);
        chatroom.getMessages().add(message);
        userRepository.save(author);
        chatroomRepository.save(chatroom);
        return "redirect:/chatrooms/" + chatroomId;
    }
}
