package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.modules.common.AbstractRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface ArticleRepository extends AbstractRepository<Article>, ArticleRepositoryCustom {

    @EntityGraph(attributePaths = {"attachedFiles", "createdByUser"})
    Optional<Article> findWithFilesAndUserById(Long id);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Page<Article> findWithUserAndAttachedFilesByStatus(ArticleStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Optional<Article> findOneWithUserAndAttachedFilesByIdAndStatus(Long id, ArticleStatus status);

    @EntityGraph(attributePaths = {"createdByUser", "comments", "comments.createdByUser", "attachedFiles"})
    Optional<Article> findOneWithAllByIdAndStatus(Long id, ArticleStatus status, Sort sort);

    @EntityGraph(attributePaths = {"createdByUser"})
    Optional<Article> findOneWithUserById(Long id);

    @EntityGraph(attributePaths = {"createdByUser", "attachedFiles"})
    Page<Article> findWithFilesAndUserByCreatedByUser_IdAndStatusOrderByCreatedDateDesc(UUID userId, ArticleStatus status, Pageable pageable);

    @Query("select n.createdByUser.id from Article n where n.id=:id ")
    UUID findCreatedByUserIdById(@Param("id") Long id);

    Optional<Article> findByIdAndStatus(Long id, ArticleStatus flagged);
}
