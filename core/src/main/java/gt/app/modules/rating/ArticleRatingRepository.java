package gt.app.modules.rating;

import gt.app.domain.ArticleRating;
import org.springframework.data.jpa.repository.JpaRepository;

interface ArticleRatingRepository extends JpaRepository<ArticleRating, Long> {
}
