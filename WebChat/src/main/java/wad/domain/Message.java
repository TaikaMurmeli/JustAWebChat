package wad.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Message extends UUIDPersistable{
    
    @ManyToOne
    private User author;
    @ManyToOne
    private Chatroom chatroom;
    @Column(name = "POST_DATE")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date date;
    private String content;

    public Message() {
        this.date = new Date();
    }

    
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
