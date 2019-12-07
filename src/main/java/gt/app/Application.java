package gt.app;

import gt.app.config.AppProperties;
import gt.app.config.Constants;
import gt.app.domain.Authority;
import gt.app.domain.User;
import gt.app.repository.AuthorityRepository;
import gt.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
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

    @Bean
    public CommandLineRunner initData(AuthorityRepository authorityRepository,
                                      UserRepository userRepository) {

        return args -> {

            /*
            user accounts
             */

            Authority adminAuthority = new Authority();
            adminAuthority.setName(Constants.ROLE_ADMIN);
            authorityRepository.save(adminAuthority);

            Authority userAuthority = new Authority();
            userAuthority.setName(Constants.ROLE_USER);
            authorityRepository.save(userAuthority);

            String pwd = "$2a$10$UtqWHf0BfCr41Nsy89gj4OCiL36EbTZ8g4o/IvFN2LArruHruiRXO"; // to make it faster

            User adminUser = new User("system", LocalDate.now().minusYears(10), "System", "Tiwari", "system@email");
            adminUser.setPassword(pwd);
            adminUser.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_ADMIN, Constants.ROLE_USER));
            userRepository.save(adminUser);

            User user1 = new User("user1", LocalDate.now().minusYears(10), "Ganesh", "Tiwari", "gt@email");
            user1.setPassword(pwd);
            user1.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER));
            userRepository.save(user1);


            User user2 = new User("user2", LocalDate.now().minusYears(1), "Jyoti", "Kattel", "jk@email");
            user2.setPassword(pwd);
            user2.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER));
            userRepository.save(user2);


            /*
            Notes:
             */
        };


    }

}
