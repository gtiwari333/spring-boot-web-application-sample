package gt.app.modules.review;

import gt.app.domain.ArticleStatus;
import gt.app.domain.Comment;
import gt.app.modules.article.CommentRepository;
import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static gt.app.domain.CommentStatus.HIDDEN;
import static gt.app.domain.CommentStatus.SHOWING;
import static gt.contentchecker.ContentCheckOutcome.PASSED;

@Service
@RequiredArgsConstructor
class CommentReviewResponseService {

    private final CommentRepository commentRepository;
    private final ReviewStatusHandler reviewStatusHandler;

    void handle(Response resp) {
        Comment c = commentRepository.findWithUserById(Long.valueOf(resp.getEntityId())).orElseThrow();
        switch (resp.getContentCheckOutcome()) {
            case PASSED -> {
                c.setStatus(SHOWING);
            }
            case FAILED, MANUAL_REVIEW_NEEDED -> c.setStatus(HIDDEN);
            default -> throw new UnsupportedOperationException();
        }

        reviewStatusHandler.handle(c.getLastModifiedByUser().getUsername(), "Your comment " + c.getContent().substring(0, 20) + " has been " + (resp.getContentCheckOutcome() == PASSED ? "approved." : "queued for manual review."));

        commentRepository.save(c);
    }
}
