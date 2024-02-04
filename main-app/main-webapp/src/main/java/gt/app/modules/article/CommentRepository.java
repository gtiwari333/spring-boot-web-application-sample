package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.modules.common.AbstractRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends AbstractRepository<Comment>, CommentRepositoryCustom {

    List<Comment> findAllByArticleId(Long id);

    boolean existsByIdAndArticleId(Long id, Long articleId);

    @EntityGraph(attributePaths = {"createdByUser"})
    Optional<Comment> findWithUserById(Long id);

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
    @CacheEvict(cacheNames = {"articleRead"}, key = "#result.articleId")
    Comment save(Comment c);
}
