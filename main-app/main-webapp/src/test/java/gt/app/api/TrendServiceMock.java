package gt.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import gt.analytics.TrendDto;
import gt.analytics.TrendService;

@Component
public class TrendServiceMock implements TrendService {

    @Override
    public ResponseEntity<TrendDto> getCurrentTrends() {
        return ResponseEntity.ok(new TrendDto());
    }

    @Override
    public ResponseEntity<TrendDto> getCurrentTrends(String category) {
        return ResponseEntity.ok(new TrendDto());
    }
}
