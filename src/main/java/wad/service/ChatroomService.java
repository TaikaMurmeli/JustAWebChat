
package wad.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wad.domain.Chatroom;
import wad.domain.User;
import wad.repository.ChatroomRepository;
import wad.repository.UserRepository;

@Service
public class ChatroomService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatroomRepository chatroomRepository;
    
    public List<User> getFriendsNotInChatroom(User user, Chatroom chatroom) {
        ArrayList<User> filteredFriends = new ArrayList();
        for (User friend : user.getFriends()) {
            if(!chatroom.getUsers().contains(friend)) {
                filteredFriends.add(friend);
            }
        }
        Collections.sort(filteredFriends);
        return filteredFriends;
    }
}
