package gt.app.modules.bookmark;

import gt.app.domain.Bookmark;
import gt.app.modules.article.ArticleService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ArticleService articleService;
    private final UserService userService;

    public Page<BookmarkWithArticleDTO> findAllBySubscriberId(UUID subscriberId, Pageable pageable) {
        return bookmarkRepository.getAllBySubscriberId(subscriberId, pageable);
    }

    public Bookmark add(UUID userId, Long articleId) {
        Bookmark bookmark = new Bookmark();
        bookmark.setArticle(articleService.getReference(articleId));
        bookmark.setSubscriber(userService.getReference(userId));
        return bookmarkRepository.save(bookmark);
    }

    public void delete(UUID subscriberId, Long articleId) {
        bookmarkRepository.deleteBySubscriberIdAndArticleId(subscriberId, articleId);
    }

    public boolean isBookmarked(UUID subscriberId, Long articleId) {
        return bookmarkRepository.existsBySubscriberIdAndArticleId(subscriberId, articleId);
    }

}
