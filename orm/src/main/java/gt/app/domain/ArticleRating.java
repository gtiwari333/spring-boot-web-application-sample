package gt.app.domain;

import gt.app.domain.enums.Rating;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class ArticleRating extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Type(type = "persistentEnum")
    private Rating rating;

    public ArticleRating() {
    }

    public ArticleRating(Article article, User user, Rating rating) {
        this.article = article;
        this.user = user;
        this.rating = rating;
    }
}
