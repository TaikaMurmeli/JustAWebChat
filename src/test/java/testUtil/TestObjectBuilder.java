
package testUtil;

import java.util.ArrayList;
import wad.domain.Chatroom;
import wad.domain.User;

public class TestObjectBuilder {
    
    public static final String plainTextPassword = "IzzAccept4ble";
    
    public static Chatroom createChatroom() {
        Chatroom chatroom = new Chatroom();
        chatroom.setTitle("test chatroom one");
        chatroom.setDescription("this is a test chatroom");
        chatroom.setUsers(new ArrayList());
        chatroom.setMessages(new ArrayList());
        
        return chatroom;
    }
    
    public static void clearChatroomData(Chatroom chatroom) {
        chatroom.getMessages().clear();
        chatroom.getUsers().clear();
    }
    
    public static User createUser(String username) {
        User user = new User();
        user.setName("maybeShouldGetRidOfThisField");
        user.setUsername(username);
        user.setEmail("example@example.com");
        user.setPassword(plainTextPassword);
        user.setFriends(new ArrayList());
        user.setChatrooms(new ArrayList());
        user.setMessages(new ArrayList());
        
        return user;
    }
    
    public static void clearUserData(User user) {
        user.getChatrooms().clear();
        user.getFriends().clear();
        user.getMessages().clear();
    }
}
