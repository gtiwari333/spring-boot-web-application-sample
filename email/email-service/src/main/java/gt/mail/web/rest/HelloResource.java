package gt.mail.web.rest;

import gt.common.config.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class HelloResource {

    @GetMapping("/hello")
    public Map<String, String> sayHello() throws Exception {
        log.info("Received hello request");
        if (new Random().nextBoolean()) {
            throw new BaseException("Something");
        }
        return Map.of("hello", "world");
    }
}
