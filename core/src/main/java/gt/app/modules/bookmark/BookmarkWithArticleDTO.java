package gt.app.modules.bookmark;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
public class BookmarkWithArticleDTO {
    private Long id;
    private Long subscriberId;
    private Instant createdDate;
    private ArticleDTO article;

    public BookmarkWithArticleDTO(Long id, Long subscriberId, Instant bookmarkedDate, String title, Instant createdDate, Long authorId) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.createdDate = bookmarkedDate;
        this.article = new ArticleDTO(title, createdDate, authorId);
    }

    @Data
    @AllArgsConstructor
    private class ArticleDTO {
        private String title;
        private Instant createdDate;
        private Long authorId;
    }


}

