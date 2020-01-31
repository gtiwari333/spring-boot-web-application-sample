package gt.app.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "subcription")
@Data
public class Subscription extends BaseEntity implements Serializable {

    private Instant startedDate = Instant.now();

    @ManyToOne(optional = false)
    private Topic topic;

    @ManyToOne(optional = false)
    private User subscriber;
}
