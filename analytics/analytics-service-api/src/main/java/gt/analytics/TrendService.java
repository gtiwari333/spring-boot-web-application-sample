package gt.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface TrendService {
    @GetMapping("/trends")
    ResponseEntity<TrendDto> getCurrentTrends();
}
