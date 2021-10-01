package gt.app.modules.review;

import gt.app.domain.Comment;
import gt.app.domain.CommentStatus;
import gt.app.modules.article.CommentRepository;
import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CommentReviewResponseService {

    private final CommentRepository commentRepository;

    void handle(Response resp) {
        Comment c = commentRepository.findById(Long.valueOf(resp.getEntityId())).orElseThrow();
        switch (resp.getContentCheckOutcome()) {
            case PASSED, MANUAL_REVIEW_NEEDED -> c.setStatus(CommentStatus.SHOWING); //its okay for comment
            case FAILED -> c.setStatus(CommentStatus.HIDDEN);
            default -> throw new UnsupportedOperationException();
        }

        commentRepository.save(c);
    }
}
