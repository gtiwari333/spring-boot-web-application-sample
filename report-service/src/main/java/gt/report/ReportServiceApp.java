package gt.report;

import gt.report.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class ReportServiceApp {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(ReportServiceApp.class);
        app.setDefaultProperties(Map.of("spring.profiles.default", Constants.SPRING_PROFILE_DEVELOPMENT));
        Environment env = app.run(args).getEnvironment();

        log.info("Environment: \t{} \n\t" +
                "----------------------------------------------------------",
            Arrays.toString(env.getActiveProfiles())
        );
    }

}
