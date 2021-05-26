package gt.profanity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@Slf4j
public class ProfanityCheckerApp {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(ProfanityCheckerApp.class);
        Environment env = app.run(args).getEnvironment();

        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                "Local: \t\t\thttp://localhost:{}\n\t" +
                "External: \t\thttp://{}:{}\n\t" +
                "Environment: \t{} \n" +
                "----------------------------------------------------------",
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            Arrays.toString(env.getActiveProfiles())
        );
    }
}

@Component
@Slf4j
@RequiredArgsConstructor
class ProfanityCheckHandler {

    final JmsTemplate jmsTemplate;
    final ProfanityChecker checker;

    @JmsListener(destination = "${jms.profanity-checker-request-queue}")
    void onMessage(Request msg) {
        log.info("Received msg for profanity check {}", msg);

        var result = checker.isOkay(msg.text);

        jmsTemplate.convertAndSend(msg.postBackTopic, Response.withResult(msg, result));
    }

}

@Component
@Slf4j
@RequiredArgsConstructor
class ProfanityChecker {

    private final List<String> badWords = List.of("fuck", "suck", "ass");

    boolean isOkay(String text) {
        return Stream.of(text.split(" ")).anyMatch(badWords::contains);
    }

}



