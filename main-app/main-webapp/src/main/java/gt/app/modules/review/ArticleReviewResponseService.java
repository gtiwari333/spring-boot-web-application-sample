package gt.app.modules.review;

import gt.api.email.EmailDto;
import gt.app.api.EmailClient;
import gt.app.config.AppProperties;
import gt.app.domain.Article;
import gt.app.modules.article.ArticleMapper;
import gt.app.modules.article.ArticleRepository;
import gt.contentchecker.ContentCheckOutcome;
import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static gt.app.domain.ArticleStatus.*;
import static gt.contentchecker.ContentCheckOutcome.PASSED;

@RequiredArgsConstructor
@Service
class ArticleReviewResponseService {
    private final ArticleRepository articleRepository;
    private final JmsTemplate jmsTemplate;
    private final EmailClient emailClient;
    private final AppProperties appProperties;
    private final ReviewStatusHandler reviewStatusHandler;

    void handle(Response resp) {
        Article a = articleRepository.findOneWithUserById(Long.valueOf(resp.getEntityId())).orElseThrow();
        switch (resp.getContentCheckOutcome()) {
            case PASSED -> a.setStatus(PUBLISHED);
            case MANUAL_REVIEW_NEEDED, FAILED -> a.setStatus(FLAGGED_FOR_MANUAL_REVIEW);
            default -> throw new UnsupportedOperationException();
        }

        articleRepository.save(a);

        if (resp.getContentCheckOutcome() == ContentCheckOutcome.PASSED) {
            jmsTemplate.convertAndSend("article-published", ArticleMapper.INSTANCE.INSTANCE.mapForPublishedEvent(a));
        }

        reviewStatusHandler.handle(a.getLastModifiedByUser().getUsername(), "Your article " + a.getTitle() + " has been " + (resp.getContentCheckOutcome() == PASSED ? "approved." : "queued for manual review."));

        sendEmailNotificationToAuthor(a, resp.getContentCheckOutcome());

    }

    void sendEmailNotificationToAuthor(Article a, ContentCheckOutcome outcome) {
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


