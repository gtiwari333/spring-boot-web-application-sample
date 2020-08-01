package gt.mail;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@DirtiesContext
class EmailApplicationTest {

    @Test
    void contextLoads() {
        assertTrue(true, "Context loads !!!");
    }

}


