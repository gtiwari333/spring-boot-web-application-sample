package gt.app.modules.review;

import gt.api.email.EmailDto;
import gt.app.api.EmailClient;
import gt.app.config.AppProperties;
import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.modules.article.ArticleMapper;
import gt.app.modules.article.ArticleRepository;
import gt.contentchecker.ContentCheckOutcome;
import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
class ArticleReviewResponseService {
    private final ArticleRepository articleRepository;
    private final JmsTemplate jmsTemplate;
    private final EmailClient emailClient;
    private final AppProperties appProperties;

    void handle(Response resp) {
        Article a = articleRepository.findOneWithUserById(Long.valueOf(resp.getEntityId())).orElseThrow();
        switch (resp.getContentCheckOutcome()) {

            case PASSED:
                a.setStatus(ArticleStatus.PUBLISHED);
                break;

            case MANUAL_REVIEW_NEEDED:
                a.setStatus(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW);
                break;

            case FAILED:
                a.setStatus(ArticleStatus.BLOCKED);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        articleRepository.save(a);

        if (resp.getContentCheckOutcome() == ContentCheckOutcome.PASSED) {
            jmsTemplate.convertAndSend("article-published", ArticleMapper.INSTANCE.INSTANCE.mapForPublishedEvent(a));
        }

        sendNotificationToAuthor(a, resp.getContentCheckOutcome());

    }

    void sendNotificationToAuthor(Article a, ContentCheckOutcome outcome) {
        var email = EmailDto.builder()
            .to(List.of(a.getCreatedByUser().getEmail()))
            .fromName(appProperties.getEmail().getAuthorNotificationsFromName())
            .fromEmail(appProperties.getEmail().getAuthorNotificationsFromEmail())
            .subject("Article Review Result " + outcome)
            .content("Result " + outcome + " For article titled: " + a.getTitle())
            .build();

        emailClient.sendEmailWithAttachments(email);
    }

}


