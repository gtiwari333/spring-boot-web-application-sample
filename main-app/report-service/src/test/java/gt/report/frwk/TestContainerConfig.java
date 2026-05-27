package gt.report.frwk;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    static final MySQLContainer mysql = new MySQLContainer("mysql");

    static {
        mysql.start();
    }

    @Bean
    @ServiceConnection
    static MySQLContainer mysql() {     //mysql is lightweight
        return mysql;
    }

}
