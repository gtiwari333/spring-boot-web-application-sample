package gt.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app-properties", ignoreUnknownFields = false)
@Data
public class AppProperties {

    final FileStorage fileStorage = new FileStorage();
    final JmsProps jms = new JmsProps();
    final Web web = new Web();

    final Email email = new Email();

    @Data
    public static class FileStorage {
        String uploadFolder;
    }

    @Data
    public static class JmsProps {
        String contentCheckerRequestQueue;
        String contentCheckerCallBackResponseQueue;
    }

    @Data
    public static class Web {
        /**
         * the registered host.. without trailing /
         */
        String baseUrl;
    }

    @Data
    public static class Email {
        String authorNotificationsFromEmail = "no-reply@system";
        String authorNotificationsFromName = "Article App";
    }

}
