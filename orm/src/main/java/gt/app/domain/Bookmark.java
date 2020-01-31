package gt.app.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "bookmark")
@Data
public class Bookmark extends BaseEntity implements Serializable {

    private Instant createdDate = Instant.now();

    @ManyToOne(optional = false)
    private Article article;

    @ManyToOne(optional = false)
    private User subscriber;


    public Bookmark() {
    }

    public Bookmark(Article article, User user) {
        this.article = article;
        this.subscriber = user;
    }
}
