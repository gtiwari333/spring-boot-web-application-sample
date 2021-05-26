package gt.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import trend.TrendDto;
import trend.TrendService;

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
