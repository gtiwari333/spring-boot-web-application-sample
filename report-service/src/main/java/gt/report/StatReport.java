package gt.report;

import gt.app.domain.ArticleStatus;
import gtapp.jooq.tables.records.JArticleRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static gtapp.jooq.Tables.ARTICLE;

@Slf4j
@RequiredArgsConstructor
@Component
public class StatReport {
    final DSLContext db;


    @Scheduled(fixedRate = 1000)
    public void run() {
        Result<JArticleRecord> a = db.selectFrom(ARTICLE)
            .where(ARTICLE.STATUS.eq(ArticleStatus.FLAGGED.name()))
            .fetch();

        System.out.println(a.size());
    }
}
