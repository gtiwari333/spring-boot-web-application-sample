package gt.app.modules.review;

import gt.app.config.AppProperties;
import gt.app.domain.Article;
import gt.app.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static gt.contentchecker.Request.RequestType.ARTICLE;
import static gt.contentchecker.Request.RequestType.COMMENT;
import static gt.contentchecker.Request.withArticle;

@RequiredArgsConstructor
@Service
@Profile("!test")
public class JmsContentCheckService implements ContentCheckService {

    private final JmsTemplate jmsTemplate;
    private final AppProperties appProperties;

    @Override
    public void sendForAutoContentReview(Article a) {
        var req = withArticle(a.getContent(), appProperties.getJms().getContentCheckerCallBackResponseQueue(), Long.toString(a.getId()), ARTICLE);
        jmsTemplate.convertAndSend(appProperties.getJms().getContentCheckerRequestQueue(), req);
    }

    @Override
    public void sendForAutoContentReview(Comment c) {
        var req = withArticle(c.getContent(), appProperties.getJms().getContentCheckerCallBackResponseQueue(), Long.toString(c.getId()), COMMENT);
        jmsTemplate.convertAndSend(appProperties.getJms().getContentCheckerRequestQueue(), req);
    }

}
