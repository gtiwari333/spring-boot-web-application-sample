package gt.app.modules.review;

import gt.contentchecker.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentCheckResponseListenerService {

    private final CommentReviewResponseService commentReviewResponseService;
    private final ArticleReviewResponseService articleReviewResponseService;

    @JmsListener(destination = "${app-properties.jms.content-checkercallback-response-queue}")
    void handleContentCheckResponse(Response resp) {

        switch (resp.getRequestType()) {
            case COMMENT:
                commentReviewResponseService.handle(resp);
                break;

            case ARTICLE:
                articleReviewResponseService.handle(resp);
                break;

            default:
                throw new UnsupportedOperationException("Article is not supported yet");
        }

    }

}

