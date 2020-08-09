package gt.trend.web;

import org.springframework.http.ResponseEntity;
import trend.TrendDto;
import trend.TrendService;

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
