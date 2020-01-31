package gt.app.modules.article;

import gt.app.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByArticleId(Long id);
}
