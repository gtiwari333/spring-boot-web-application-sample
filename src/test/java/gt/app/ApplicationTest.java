package gt.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@Slf4j
@DirtiesContext
class ApplicationTest {

    @Test
    void contextLoads() {
        log.info("Context loads !!!");
    }

}


