package gt.app.modules.article.search;

import gt.app.domain.Article;
import gt.app.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Document(indexName = "article_search")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSearch {

    @Id
    protected Long id;

    private String title;

    private String content;

    private User createdByUser;

    public static ArticleSearch from(Article a) {
        return new ArticleSearchBuilder()
            .id(a.getId())
            .title(a.getTitle())
            .content(a.getContent())
            .createdByUser(a.getCreatedByUser())
            .build();
    }
}
