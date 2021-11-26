package gt.analytics.web;

import org.springframework.http.ResponseEntity;
import gt.analytics.TrendDto;
import gt.analytics.TrendService;

public class TrendController implements TrendService {
    @Override
    public ResponseEntity<TrendDto> getCurrentTrends() {
        return ResponseEntity.ok(new TrendDto());
    }

    @Override
    public ResponseEntity<TrendDto> getCurrentTrends(String category) {
        return ResponseEntity.ok(new TrendDto());
    }
}
