package gt.app.modules.article;

import gt.app.domain.NoteStatus;
import lombok.Data;

@Data
public class ArticleReviewDto {

    private Long id;
    private NoteStatus verdict;

}
