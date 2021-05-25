package gt.app.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
@Slf4j
public class HelloResource {

    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        return Map.of("hello", "world");
    }

}
