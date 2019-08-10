package g.t.app;

import com.google.common.io.Files;
import g.t.app.config.Constants;
import g.t.app.domain.Authority;
import g.t.app.domain.User;
import g.t.app.repository.AuthorityRepository;
import g.t.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@Slf4j
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
    public CommandLineRunner initData(UserRepository authorRepository,

                                      PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository,
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

            Authority ownerAuthority = new Authority();
            ownerAuthority.setName(Constants.ROLE_OWNER);
            authorityRepository.save(ownerAuthority);

            User adminUser = new User();
            adminUser.setUniqueId("system");
            adminUser.setEmail("system@email");
            adminUser.setFirstName("System");
            adminUser.setDateOfBirth(LocalDate.now().minusYears(1));
            adminUser.setLastName("Tiwari");
            adminUser.setPassword(passwordEncoder.encode("pass"));
            adminUser.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_ADMIN, Constants.ROLE_OWNER, Constants.ROLE_USER));
            adminUser.setAccountNonExpired(true);
            adminUser.setAccountNonLocked(true);
            adminUser.setCredentialsNonExpired(true);
            adminUser.setActive(true);
            userRepository.save(adminUser);

            /*
            other users
             */

            User user1 = new User();
            user1.setUniqueId("user1");
            user1.setEmail("gt@email");
            user1.setFirstName("Ganesh");
            user1.setLastName("User");
            user1.setDateOfBirth(LocalDate.now().minusYears(10));
            user1.setAvatar(Files.toByteArray(new ClassPathResource("static/img/male-coat.png").getFile()));
            user1.setPassword(passwordEncoder.encode("pass"));
            user1.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER));
            user1.setAccountNonExpired(true);
            user1.setAccountNonLocked(true);
            user1.setCredentialsNonExpired(true);
            user1.setActive(true);
            authorRepository.save(user1);


            User owner1 = new User();
            owner1.setUniqueId("owner1");
            owner1.setDateOfBirth(LocalDate.now().minusYears(1));
            owner1.setFirstName("Owner 1");
            owner1.setLastName("Owner");
            owner1.setEmail("Owner1@owner");
            owner1.setAvatar(Files.toByteArray(new ClassPathResource("static/img/male-tshirt.png").getFile()));
            owner1.setPassword(passwordEncoder.encode("pass"));
            owner1.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER, Constants.ROLE_OWNER));
            owner1.setAccountNonExpired(true);
            owner1.setAccountNonLocked(true);
            owner1.setCredentialsNonExpired(true);
            owner1.setActive(true);
            userRepository.save(owner1);


            User owner3 = new User();
            owner3.setUniqueId("owner2");
            owner3.setDateOfBirth(LocalDate.now().minusYears(1));
            owner3.setFirstName("Owner 2");
            owner3.setLastName("Author2 lastname");
            owner3.setEmail("owner2@owner");
            owner3.setAvatar(Files.toByteArray(new ClassPathResource("static/img/male-coat.png").getFile()));
            owner3.setPassword(passwordEncoder.encode("pass"));
            owner3.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER, Constants.ROLE_OWNER));
            owner3.setAccountNonExpired(true);
            owner3.setAccountNonLocked(true);
            owner3.setCredentialsNonExpired(true);
            owner3.setActive(true);
            userRepository.save(owner3);


        };


    }

}
