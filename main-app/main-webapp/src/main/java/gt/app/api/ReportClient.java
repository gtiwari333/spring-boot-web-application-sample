package gt.app.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "report-service", url = "${feign-clients.report-service.url}", fallback = ReportClient.ReportClientFallback.class, configuration = InternalKeycloakAuthConfig.class)
public interface ReportClient {
    @PostMapping(value = "/to-review", produces = MediaType.APPLICATION_JSON_VALUE)
    FlagCount getFlaggedForReviewCount();

    @Slf4j
    class ReportClientFallback implements ReportClient {


        @Override
        public FlagCount getFlaggedForReviewCount() {
            return new FlagCount(-100);
        }
    }
    record FlagCount(int value) {
    }
}



