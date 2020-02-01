package gt.app.domain;

import gt.app.domain.enums.Rating;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class CommentRating extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Type(type = "persistentEnum")
    private Rating rating;

    public CommentRating() {
    }

    public CommentRating(Comment comment, User user, Rating rating) {
        this.comment = comment;
        this.user = user;
        this.rating = rating;
    }
}
