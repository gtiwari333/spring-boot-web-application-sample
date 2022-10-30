package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.modules.common.AbstractRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.Modifying;

import jakarta.transaction.Transactional;
import java.util.List;

public interface CommentRepository extends AbstractRepository<Comment>, CommentRepositoryCustom {

    List<Comment> findAllByArticleId(Long id);

    boolean existsByIdAndArticleId(Long id, Long articleId);

    @Transactional
    @Modifying
    @Caching(
        evict = {
            @CacheEvict(cacheNames = {"articleForReview"}, key = "#id"),
            @CacheEvict(cacheNames = {"previewForPublicHomePage", "previewAllWithFilesByUser", "getAllToReview"}, allEntries = true)
        }
    )
    void deleteByArticleId(Long id);

    @Override
    @CacheEvict(cacheNames = {"articleRead"}, key = "#c.articleId")
    Comment save(Comment c);
}
