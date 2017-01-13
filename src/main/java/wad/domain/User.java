package wad.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
public class User extends AbstractPersistable<Long> {

    @Valid
    //The unicode characters are Ä,ä,Ö,ö
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z_\\s\\u00c4\\u00e4\\u00d6\\u00f6]+$",
            message = "can only contain characters and spaces.")
    private String name;
    
    @NotBlank
    @Length(min = 3, max = 15, message = "length must be between 3 and 15")
    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9_\\s\\u00c4\\u00e4\\u00d6\\u00f6]+$",
            message = "can only contain characters, numbers, spaces and underlines.")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    //Password validation done in controller because of hashing with salt
    private String password;
    private String salt;

    @ManyToMany
    private List<User> friends;
    @ManyToMany
    private List<Chatroom> chatrooms;
    @OneToMany(mappedBy = "author")
    private List<Message> messages;
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date lastOnline;

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public void addFriend(User user) {
        this.friends.add(user);
    }

    public List<Chatroom> getChatrooms() {
        return chatrooms;
    }

    public void setChatrooms(List<Chatroom> chatrooms) {
        this.chatrooms = chatrooms;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.salt = BCrypt.gensalt();
        this.password = BCrypt.hashpw(password, salt);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addChatroom(Chatroom chatroom) {
        this.chatrooms.add(chatroom);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
