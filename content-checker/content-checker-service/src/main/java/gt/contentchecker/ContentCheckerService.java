package gt.contentchecker;

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
public class ContentCheckerService {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(ContentCheckerService.class);
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
class ContentCheckHandler {

    final JmsTemplate jmsTemplate;
    final ContentChecker checker;

    @JmsListener(destination = "${jms.content-checker-request-queue}")
    void onMessage(Request msg) {
        log.info("Received msg for content check {}", msg);

        var result = checker.isOkay(msg.text);

        jmsTemplate.convertAndSend(msg.postBackTopic, Response.withResult(msg, result));
    }

}

@Component
@Slf4j
@RequiredArgsConstructor
class ContentChecker {

    private final List<String> badWords = List.of("fuck", "suck", "ass");
    private final List<String> controversial = List.of("party", "politics", "libtard", "freedom", "conspiracy", "snowflake");

    ContentCheckOutcome isOkay(String text) {
        if (Stream.of(text.split(" ")).anyMatch(badWords::contains)) {
            return ContentCheckOutcome.FAILED;
        }

        if (Stream.of(text.split(" ")).anyMatch(controversial::contains)) {
            return ContentCheckOutcome.MANUAL_REVIEW_NEEDED;
        }

        return ContentCheckOutcome.PASSED;
    }

}



