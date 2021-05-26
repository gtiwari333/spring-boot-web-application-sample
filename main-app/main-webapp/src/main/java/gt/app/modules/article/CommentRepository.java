package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.modules.common.AbstractRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

interface CommentRepository extends AbstractRepository<Comment>, CommentRepositoryCustom {

    List<Comment> findAllByArticleId(Long id);

    boolean existsByIdAndArticleId(Long id, Long articleId);

    @Transactional
    @Modifying
    void deleteByArticleId(Long id);
}
