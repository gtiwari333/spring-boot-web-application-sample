package gt.app.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "follow")
@Data
public class Follow extends BaseEntity {

    /*
     * User is being followed by follower
     */

    private Instant startedDate = Instant.now();

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private User follower;
}
