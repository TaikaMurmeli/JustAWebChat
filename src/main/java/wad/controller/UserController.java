package wad.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wad.domain.User;
import wad.repository.UserRepository;
import wad.service.UserService;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String create(Model model,
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        String password = request.getParameter("password");

        if (bindingResult.hasErrors()) {    
            return "signup";
        }
       
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("signupMessage",
                    "Username already in use!");
            return "redirect:/signup";
        }
        
        if (!passwordIsValid(password)) {
             redirectAttributes.addFlashAttribute("passwordErrorMessage", "Invalid password!");
             return "redirect:/signup";
        }
        
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("signupMessage",
                "Account succesfully created!");
        return "redirect:/signup";
    }

    @RequestMapping(value = "/friend", method = RequestMethod.POST)
    public String addFriend(@RequestParam String username, RedirectAttributes redirectAttributes) {
        User self = userService.getAuthenticatedUser();
        User friend = userRepository.findByUsername(username);

        if (friend == null) {
            redirectAttributes.addFlashAttribute("friendingMessage", "Given username doesn not exist.");
        } else if (friend.equals(self)) {
            redirectAttributes.addFlashAttribute("friendingMessage", "You cannot add yourself as friend. Makes no sense...");
        } else {
            self.addFriend(friend);
            userRepository.save(self);
            redirectAttributes.addFlashAttribute("friendingMessage", username + " is now your friend!");
        }

        return "redirect:/index";
    }

    private boolean passwordIsValid(String password) {
        
        if (password.length() < 8) {
             return false;
        }
        String regex = "^(?=.*[a-z\u00e4\u00f6])(?=.*[A-Z\u00c4\u00d6])(?=.*[-0-9_!%.,+*?$€@^]).+$";
        if (!password.matches(regex)) {
            return false;
        }
        return true;
    }
    
    
}
