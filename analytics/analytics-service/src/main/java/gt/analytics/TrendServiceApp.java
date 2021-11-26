package gt.analytics;

import gt.common.dtos.ArticleSummaryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class TrendServiceApp {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(TrendServiceApp.class);
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

    @JmsListener(destination = "article-published")
    void onArticlePublished(ArticleSummaryDto msg) {
        log.info("Received msg for gt.trend calculation {}", msg);
    }


    @JmsListener(destination = "article-read")
    void onArticleRead(ArticleSummaryDto msg) {
        log.info("Received msg for gt.trend calculation {}", msg);
    }

}
