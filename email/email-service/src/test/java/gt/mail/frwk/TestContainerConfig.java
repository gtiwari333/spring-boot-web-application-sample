package gt.mail.frwk;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;

import static java.lang.System.setProperty;

@Configuration
public class TestContainerConfig {

    static {
        var mailHog = new GenericContainer<>("richarvey/mailhog");
        mailHog.withExposedPorts(1025);
        mailHog.start();

        setProperty("MAILHOG_PORT", Integer.toString(mailHog.getMappedPort(1025)));

    }
}
