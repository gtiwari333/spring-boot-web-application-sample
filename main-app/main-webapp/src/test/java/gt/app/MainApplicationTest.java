package gt.app;

import gt.app.frwk.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MainApplicationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        assertTrue(true, "Context loads !!!");
    }

}


