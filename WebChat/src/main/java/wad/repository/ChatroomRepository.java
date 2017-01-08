
package wad.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.Chatroom;
import wad.domain.User;

/**
 *
 * @author sjsarsa
 */
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    List<Chatroom> findByUsers(User user);
}
