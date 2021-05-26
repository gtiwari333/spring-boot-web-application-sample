package trend;

import lombok.Data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Data
public class TrendDto {

    List<Trend> allTrends = new LinkedList<>();
    Instant currentTime = Instant.now();
    Instant validUpto = currentTime.plus(1, ChronoUnit.HOURS);

    @Data
    static class Trend {
        long articleId;
        String trendCategory;
        String preview;
        String username;
    }
}
