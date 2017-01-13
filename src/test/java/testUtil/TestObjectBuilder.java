package testUtil;

import java.util.ArrayList;
import wad.domain.Chatroom;
import wad.domain.User;

public class TestObjectBuilder {

    public static final String VALID_PLAIN_TEXT_PASSWORD = "IzzAccept4ble";
    public static final String VALID_EMAIL = "email@email.com";

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
        user.setEmail(VALID_EMAIL);
        user.setPassword(VALID_PLAIN_TEXT_PASSWORD);
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
