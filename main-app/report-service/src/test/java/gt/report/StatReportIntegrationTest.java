package gt.report;

import static org.assertj.core.api.Assertions.assertThat;

import gt.report.frwk.TestContainerConfig;
import java.util.UUID;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfig.class)
class StatReportIntegrationTest {

    @Autowired
    StatReport statReport;

    @Autowired
    DSLContext db;

    @AfterEach
    void cleanUp() {
        db.execute("DELETE FROM g_article");
        db.execute("DELETE FROM g_app_user");
    }

    @Test
    void returnsZeroWhenNoFlaggedArticles() {
        assertThat(statReport.run().value()).isZero();
    }

    @Test
    void countsFlaggedArticlesCorrectly() {
        String userUuid = UUID.randomUUID().toString().replace("-", "");

        db.execute(
            "INSERT INTO g_app_user (id, email, first_name, last_name, username) " +
            "VALUES (UNHEX('" + userUuid + "'), 'test@test.com', 'Test', 'User', 'testuser')"
        );

        db.execute(
            "INSERT INTO g_article (title, content, status, created_by_user_id, created_date, last_modified_date) " +
            "VALUES ('Flagged Article', 'Bad content', 'FLAGGED_FOR_MANUAL_REVIEW', UNHEX('" + userUuid + "'), NOW(), NOW())"
        );
        db.execute(
            "INSERT INTO g_article (title, content, status, created_by_user_id, created_date, last_modified_date) " +
            "VALUES ('Clean Article', 'Good content', 'PUBLISHED', UNHEX('" + userUuid + "'), NOW(), NOW())"
        );

        assertThat(statReport.run().value()).isEqualTo(1);
    }

    @Test
    void countsMultipleFlaggedArticles() {
        String userUuid = UUID.randomUUID().toString().replace("-", "");

        db.execute(
            "INSERT INTO g_app_user (id, email, first_name, last_name, username) " +
            "VALUES (UNHEX('" + userUuid + "'), 'test@test.com', 'Test', 'User', 'testuser')"
        );

        for (int i = 0; i < 3; i++) {
            db.execute(
                "INSERT INTO g_article (title, content, status, created_by_user_id, created_date, last_modified_date) " +
                "VALUES ('Flagged " + i + "', 'Content " + i + "', 'FLAGGED_FOR_MANUAL_REVIEW', UNHEX('" + userUuid + "'), NOW(), NOW())"
            );
        }

        assertThat(statReport.run().value()).isEqualTo(3);
    }
}
