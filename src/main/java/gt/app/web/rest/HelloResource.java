package gt.app.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloResource {

    @GetMapping("/api/hello")
    public Map<String, String> sayHello() {
        return Map.of("hello", "world");
    }
}
