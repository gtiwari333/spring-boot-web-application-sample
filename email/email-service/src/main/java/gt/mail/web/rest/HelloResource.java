package gt.mail.web.rest;

import gt.common.config.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class HelloResource {
    private static final Random RANDOM = new SecureRandom();

    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        log.info("Received hello request");
        if (RANDOM.nextBoolean()) {
            throw new BaseException("Something");
        }
        return Map.of("hello", "world");
    }
}
