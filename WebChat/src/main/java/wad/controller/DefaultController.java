package wad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wad.domain.User;
import wad.service.UserService;

@Controller
@RequestMapping("*")
public class DefaultController {

    @Autowired
    private UserService userService;
    
    @RequestMapping(value="login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    
     @RequestMapping(value="signup", method = RequestMethod.GET)
    public String signup(@ModelAttribute User user) {
        return "signup";
    }
    
    @RequestMapping(value="info", method = RequestMethod.GET)
    public String viewInfo() {
        return "info";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String viewIndex(Model model) {
        User user = userService.getAuthenticatedUser();
        model.addAttribute("chatrooms", user.getChatrooms());
        model.addAttribute("friends", user.getFriends());
        String welcomeMessage = "You are currently logged in as " + user.getUsername() + ".";
        model.addAttribute("welcomeMessage", welcomeMessage);
        return "index";
    }
}
