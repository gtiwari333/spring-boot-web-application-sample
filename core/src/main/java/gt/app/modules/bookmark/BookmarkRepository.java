package gt.app.modules.bookmark;

import gt.app.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("select new gt.app.modules.bookmark.BookmarkWithArticleDTO(bm.id, bm.subscriber.id, bm.createdDate, bm.article.title,   bm.article.createdDate, bm.article.createdByUser.id)" +
        " from  Bookmark bm  where bm.subscriber.id = :id")
    Page<BookmarkWithArticleDTO> getAllBySubscriberId(@Param("id") Long subscriberId, Pageable pageable);

    boolean existsBySubscriberIdAndArticleId(Long subscriberId, Long articleId);

    @Transactional
    @Modifying
    void deleteBySubscriberIdAndArticleId(Long subscriberId, Long articleId);

}
