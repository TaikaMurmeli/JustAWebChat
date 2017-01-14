package wad.controller;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import wad.domain.Chatroom;
import wad.domain.User;
import wad.repository.ChatroomRepository;
import wad.repository.UserRepository;
import wad.service.ChatroomService;
import wad.service.UserService;

@Controller
@RequestMapping("/chatrooms")
public class ChatroomController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private ChatroomService chatroomService;
    
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    UserRepository userRepository;
    
    @RequestMapping(value = "/{chatroomId}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable long chatroomId) {
        User user = userService.getAuthenticatedUser();
        Chatroom chatroom = chatroomRepository.findOne(chatroomId);
        
        if (chatroom == null) {
            return "redirect:/";
        }
        
        if (chatroom.getUsers().contains(user)) {
            model.addAttribute("chatroom", chatroom);
            model.addAttribute("absentFriends",
                    chatroomService.getFriendsNotInChatroom(user, chatroom));
        } else {
            model.addAttribute("unauthorizedAccess", "Were sorry, but you "
                    + "do not belong here.");
        }
        
        return "chatroom";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String create(@ModelAttribute Chatroom chatroom) {
        User user = userService.getAuthenticatedUser();
        List<User> users = new ArrayList();
        chatroom.setUsers(users);
        chatroom.addUser(user);
        chatroom = chatroomRepository.save(chatroom);
        user.addChatroom(chatroom);
        userRepository.save(user);
        
        return "redirect:/";
    }
    
    @Transactional
    @RequestMapping(value = "/addUser/{chatroomId}", method = RequestMethod.POST)
    public String addMember(@PathVariable long chatroomId, @RequestParam String username) {
        Chatroom chatroom = chatroomRepository.findOne(chatroomId);
        User user = userRepository.findByUsername(username);
        if (user != null && !chatroom.getUsers().contains(user)) {
            chatroom.addUser(user);
            user.addChatroom(chatroom);
        }
        return "redirect:/chatrooms/" + chatroomId;
    }
    
    @Transactional
    @RequestMapping(value = "/leave/{chatroomId}", method = RequestMethod.POST)
    public String leaveChatroom(@PathVariable long chatroomId) {
        User user = userService.getAuthenticatedUser();
        Chatroom chatroom = chatroomRepository.findOne(chatroomId);
        
        //Add confirmation alert?
        chatroom.getUsers().remove(user);
        user.getChatrooms().remove(chatroom);
        
        if (chatroom.getUsers().isEmpty()) {
            chatroomRepository.delete(chatroom);
        }
        return "redirect:/";
    }
    
}
