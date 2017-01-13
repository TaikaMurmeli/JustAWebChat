package wad.profiles;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wad.domain.Chatroom;
import wad.domain.User;
import wad.repository.ChatroomRepository;
import wad.repository.UserRepository;

@Configuration
@Profile(value = {"dev", "default"})
public class DevProfile {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @PostConstruct
    public void init() {
        //Profiles for testing the app by hand

        User juan = new User();
        juan.setName("Juan");
        juan.setUsername("juan");
        juan.setPassword("JuanLikesDogs");
        juan.setEmail("juan@email.com");
        juan.setFriends(new ArrayList());
        juan.setChatrooms(new ArrayList());

        User dan = new User();
        dan.setName("Dan");
        dan.setUsername("dan");
        dan.setEmail("dan@email.com");
        dan.setPassword("g00dPazzwurd!?");
        dan.setFriends(new ArrayList());
        dan.setChatrooms(new ArrayList());

        User mike = new User();
        mike.setName("Mike");
        mike.setUsername("mike");
        mike.setEmail("mike@email.com");
        mike.setPassword("C0m=An*G37MEHAKKARZ*!$€");
        mike.setFriends(new ArrayList());
        mike.setChatrooms(new ArrayList());

        mike = userRepository.save(mike);
        juan = userRepository.save(juan);
        dan = userRepository.save(dan);

        dan.addFriend(juan);

        Chatroom chatroom = new Chatroom();
        chatroom.setTitle("chatroom number one");
        chatroom.setDescription("this is an example chatroom");
        chatroom.setUsers(new ArrayList());
        chatroom.addUser(dan);
        chatroom.addUser(juan);

        chatroom = chatroomRepository.save(chatroom);
        juan.addChatroom(chatroom);
        dan.addChatroom(chatroom);

        userRepository.save(juan);
        userRepository.save(dan);

    }
}
