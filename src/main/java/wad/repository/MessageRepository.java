
package wad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wad.domain.Message;

/**
 *
 * @author sjsarsa
 */
public interface MessageRepository extends JpaRepository<Message, String>{
    
}
