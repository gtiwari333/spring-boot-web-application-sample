package gt.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface TrendService {
    @GetMapping("/getTrends")
    ResponseEntity<TrendDto> getCurrentTrends();

    @GetMapping("/getTrends/{category}")
    ResponseEntity<TrendDto> getCurrentTrends(@PathVariable String category);
}
