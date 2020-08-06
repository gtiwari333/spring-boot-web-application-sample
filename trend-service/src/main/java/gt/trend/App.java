package gt.trend;

import gt.common.dtos.ArticleCreatedEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class App {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(App.class);
        Environment env = app.run(args).getEnvironment();

        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                "Local: \t\t\thttp://localhost:{}\n\t" +
                "External: \t\thttp://{}:{}\n\t" +
                "Environment: \t{} \n\t" +
                "----------------------------------------------------------",
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            Arrays.toString(env.getActiveProfiles())
        );
    }

    @JmsListener(destination = "article-published")
    void onMessage(ArticleCreatedEventDto msg) {
        log.info("Received msg for trend calculation {}", msg);
    }

}
