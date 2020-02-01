package gt.app.modules.bookmark;

import gt.app.domain.Bookmark;
import gt.app.modules.article.ArticleService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ArticleService articleService;
    private final UserService userService;

    public Page<BookmarkWithArticleDTO> findAllBySubscriberId(Long subscriberId, Pageable pageable) {
        return bookmarkRepository.getAllBySubscriberId(subscriberId, pageable);
    }

    public Bookmark add(Long userId, Long articleId) {
        Bookmark bookmark = new Bookmark();
        bookmark.setArticle(articleService.getReference(articleId));
        bookmark.setSubscriber(userService.getReference(userId));
        return bookmarkRepository.save(bookmark);
    }

    public void delete(Long subscriberId, Long authorUniqueId) {
        bookmarkRepository.deleteBySubscriberIdAndArticleId(subscriberId, authorUniqueId);
    }

    public boolean isBookmarked(Long subscriberId, Long authorUniqueId) {
        return bookmarkRepository.existsBySubscriberIdAndArticleId(subscriberId, authorUniqueId);
    }

}
