package gt.report;

import gt.app.domain.ArticleStatus;
import gtapp.jooq.tables.records.GArticleRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static gtapp.jooq.Tables.G_ARTICLE;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatReport {
    final DSLContext db;

    @Scheduled(fixedRate = 1000)
    public FlagCount run() {
        Result<GArticleRecord> a = db.selectFrom(G_ARTICLE)
            .where(G_ARTICLE.STATUS.eq(ArticleStatus.FLAGGED_FOR_MANUAL_REVIEW.name()))
            .fetch();

        return new FlagCount(a.size());
    }

    public record FlagCount(int value) {
    }
}
