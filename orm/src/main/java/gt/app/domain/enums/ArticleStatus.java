package gt.app.domain.enums;

import java.util.Collection;
import java.util.List;

public enum ArticleStatus {
    INITIAL_DRAFT, PUBLISHED, REVERTED_TO_DRAFT, DELETED;

    public static Collection<ArticleStatus> getStatusesThatCanBeChangedToByUser() {
        return List.of(INITIAL_DRAFT, PUBLISHED);
    }

    public static Collection<ArticleStatus> getPublishableStatuses() {
        return List.of(INITIAL_DRAFT, REVERTED_TO_DRAFT);
    }
}
