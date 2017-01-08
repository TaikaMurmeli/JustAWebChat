
package wad.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.springframework.data.domain.Persistable;

/**
 *
 * @author sjsarsa
 */
@MappedSuperclass
public class UUIDPersistable implements Persistable<String> {
    
    @Id
    private String id;

    public UUIDPersistable() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @JsonIgnore
    @Override
    public boolean isNew() {
        return false;
    }
    

}
