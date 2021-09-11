package gt.app.modules.review;

import gt.app.domain.Comment;
import gt.app.domain.CommentStatus;
import gt.app.modules.article.CommentService;
import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CommentReviewResponseService {

    private final CommentService commentService;

    void handle(Response resp) {
        Comment c = commentService.findById(Long.valueOf(resp.getEntityId())).orElseThrow();
        switch (resp.getContentCheckOutcome()) {

            case PASSED:
            case MANUAL_REVIEW_NEEDED:  //its okay for comment
                c.setStatus(CommentStatus.SHOWING);
                break;

            case FAILED:
                c.setStatus(CommentStatus.HIDDEN);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        commentService.save(c);
    }
}
