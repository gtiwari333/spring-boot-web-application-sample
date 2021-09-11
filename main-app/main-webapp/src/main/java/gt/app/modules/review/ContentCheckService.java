package gt.app.modules.review;

import gt.app.domain.Article;
import gt.app.domain.Comment;

public interface ContentCheckService {
    void sendForAutoContentReview(Article a);

    void sendForAutoContentReview(Comment c);
}
