
package wad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import wad.domain.User;
import wad.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        return userRepository.findByUsername(authentication.getName());
    }
}
