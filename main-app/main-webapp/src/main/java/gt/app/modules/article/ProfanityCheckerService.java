package gt.app.modules.article;

import gt.app.config.AppProperties;
import gt.app.domain.Article;
import gt.app.domain.Comment;
import gt.app.domain.CommentStatus;
import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static gt.contentchecker.Request.RequestType.ARTICLE;
import static gt.contentchecker.Request.RequestType.COMMENT;
import static gt.contentchecker.Request.withArticle;

@Service
@RequiredArgsConstructor
@Slf4j
class ProfanityCheckerService {

    final CommentRepository commentRepository;
    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    void handleProfanityCheck(Article a) {
        var req = withArticle(a.getContent(), appProperties.getJms().getProfanityCheckerCallBackResponseQueue(), Long.toString(a.getId()), ARTICLE);
        jmsTemplate.convertAndSend(appProperties.getJms().getProfanityCheckerRequestQueue(), req);
    }

    void handleProfanityCheck(Comment c) {
        var req = withArticle(c.getContent(), appProperties.getJms().getProfanityCheckerCallBackResponseQueue(), Long.toString(c.getId()), COMMENT);
        jmsTemplate.convertAndSend(appProperties.getJms().getProfanityCheckerRequestQueue(), req);
    }

    @JmsListener(destination = "${app-properties.jms.content-checkercallback-response-queue}")
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

