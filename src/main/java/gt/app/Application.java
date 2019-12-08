package gt.app;

import gt.app.config.AppProperties;
import gt.app.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@Slf4j
@EnableConfigurationProperties(AppProperties.class)
public class Application {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(Map.of("spring.profiles.default", Constants.SPRING_PROFILE_DEVELOPMENT));
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

}
