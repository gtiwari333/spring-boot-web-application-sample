package gt.app.modules.article;

import gt.app.config.AppProperties;
import gt.app.domain.Article;
import gt.app.domain.Comment;
import gt.app.domain.CommentStatus;
import gt.profanity.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import static gt.profanity.Request.RequestType.ARTICLE;
import static gt.profanity.Request.RequestType.COMMENT;
import static gt.profanity.Request.withArticle;

@Component
@RequiredArgsConstructor
@Slf4j
class ProfanityChecker {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;
    final CommentRepository commentRepository;

    void handleProfanityCheck(Article a) {
        var req = withArticle(a.getContent(), appProperties.getJms().getProfanityCheckerCallBackResponseQueue(), Long.toString(a.getId()), ARTICLE);
        jmsTemplate.convertAndSend(appProperties.getJms().getProfanityCheckerRequestQueue(), req);
    }

    void handleProfanityCheck(Comment c) {
        var req = withArticle(c.getContent(), appProperties.getJms().getProfanityCheckerCallBackResponseQueue(), Long.toString(c.getId()), COMMENT);
        jmsTemplate.convertAndSend(appProperties.getJms().getProfanityCheckerRequestQueue(), req);
    }

    @JmsListener(destination = "${app-properties.jms.profanity-checker-callback-response-queue}")
    void handleProfanityResponse(Response resp) {

        switch (resp.getRequestType()) {
            case COMMENT:
                Comment c = commentRepository.findById(Long.valueOf(resp.getEntityId())).orElseThrow();
                if (resp.isAllowed()) {
                    c.setStatus(CommentStatus.SHOWING);
                } else {
                    c.setStatus(CommentStatus.HIDDEN);
                }
                commentRepository.save(c);
                break;
            case ARTICLE:
                throw new UnsupportedOperationException("Article is not supported yet");
        }

    }

}

