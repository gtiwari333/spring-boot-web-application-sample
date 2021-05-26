package gt.app.modules.article;

import gt.app.domain.ArticleStatus;
import lombok.Data;

@Data
public class ArticleReviewResultDto {

    private Long id;
    private ArticleStatus verdict;

}
