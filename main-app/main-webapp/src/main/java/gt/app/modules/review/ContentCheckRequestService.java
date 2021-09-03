package gt.app.modules.review;

import gt.app.config.AppProperties;
import gt.app.domain.Article;
import gt.app.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static gt.contentchecker.Request.RequestType.ARTICLE;
import static gt.contentchecker.Request.RequestType.COMMENT;
import static gt.contentchecker.Request.withArticle;

@RequiredArgsConstructor
@Service
public class ContentCheckRequestService {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    public void sendForAutoContentReview(Article a) {
        var req = withArticle(a.getContent(), appProperties.getJms().getContentCheckerCallBackResponseQueue(), Long.toString(a.getId()), ARTICLE);
        jmsTemplate.convertAndSend(appProperties.getJms().getContentCheckerRequestQueue(), req);
    }

    public void sendForAutoContentReview(Comment c) {
        var req = withArticle(c.getContent(), appProperties.getJms().getContentCheckerCallBackResponseQueue(), Long.toString(c.getId()), COMMENT);
        jmsTemplate.convertAndSend(appProperties.getJms().getContentCheckerRequestQueue(), req);
    }

}
