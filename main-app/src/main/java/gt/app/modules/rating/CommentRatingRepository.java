package gt.app.modules.rating;

import gt.app.domain.CommentRating;
import org.springframework.data.jpa.repository.JpaRepository;

interface CommentRatingRepository extends JpaRepository<CommentRating, Long> {
}
