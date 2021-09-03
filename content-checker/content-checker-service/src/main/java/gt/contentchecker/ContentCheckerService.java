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

    @JmsListener(destination = "${jms.content-checkerrequest-queue}")
    void onMessage(Request msg) {
        log.info("Received msg for profanity check {}", msg);

        var result = checker.isOkay(msg.text);

        jmsTemplate.convertAndSend(msg.postBackTopic, Response.withResult(msg, result));
    }

}

@Component
@Slf4j
@RequiredArgsConstructor
class ContentChecker {

    private final List<String> badWords = List.of("fuck", "suck", "ass");

    boolean isOkay(String text) {
        return Stream.of(text.split(" ")).anyMatch(badWords::contains);
    }

}



