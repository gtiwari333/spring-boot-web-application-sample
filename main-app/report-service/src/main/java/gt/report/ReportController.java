package gt.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ReportController {

    final StatReport statReport;

    @PostMapping(value = "/to-review", produces = MediaType.APPLICATION_JSON_VALUE)
    public StatReport.FlagCount getFlaggedForReviewCount() {
        log.info("Got request to get flagged review count");
        return statReport.run();
    }
}
